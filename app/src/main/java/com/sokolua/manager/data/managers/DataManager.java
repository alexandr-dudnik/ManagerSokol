package com.sokolua.manager.data.managers;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.TagConstraint;
import com.birbit.android.jobqueue.config.Configuration;
import com.sokolua.manager.data.network.RestCallTransformer;
import com.sokolua.manager.data.network.RestService;
import com.sokolua.manager.data.network.req.SendNoteReq;
import com.sokolua.manager.data.network.req.SendOrderReq;
import com.sokolua.manager.data.network.req.UserLoginReq;
import com.sokolua.manager.data.network.res.CustomerRes;
import com.sokolua.manager.data.network.res.GoodGroupRes;
import com.sokolua.manager.data.network.res.GoodItemRes;
import com.sokolua.manager.data.network.res.OrderRes;
import com.sokolua.manager.data.network.res.UserRes;
import com.sokolua.manager.data.storage.realm.BrandsRealm;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderPlanRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.components.DaggerDataManagerComponent;
import com.sokolua.manager.di.components.DataManagerComponent;
import com.sokolua.manager.di.modules.LocalModule;
import com.sokolua.manager.di.modules.NetworkModule;
import com.sokolua.manager.jobs.FetchRemoteCustomersJob;
import com.sokolua.manager.jobs.FetchRemoteGoodGroupsJob;
import com.sokolua.manager.jobs.FetchRemoteGoodItemsJob;
import com.sokolua.manager.jobs.FetchRemoteOrdersJob;
import com.sokolua.manager.jobs.SendCustomerNoteJob;
import com.sokolua.manager.jobs.SendOrderJob;
import com.sokolua.manager.jobs.UpdateCustomerJob;
import com.sokolua.manager.jobs.UpdateGoodGroupJob;
import com.sokolua.manager.jobs.UpdateGoodItemJob;
import com.sokolua.manager.jobs.UpdateOrderJob;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.AppConfig;
import com.sokolua.manager.utils.NetworkStatusChecker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class DataManager {
    private static DataManager ourInstance;

    @Inject
    PreferencesManager mPreferencesManager;
    @Inject
    RestService mRestService;
    @Inject
    Retrofit mRetrofit;
    @Inject
    RealmManager mRealmManager;

    private JobManager mJobManager;


    private DataManager() {
        DataManagerComponent dmComponent = DaggerService.getComponent(DataManagerComponent.class);
        if (dmComponent==null){
            dmComponent = DaggerDataManagerComponent.builder()
                    .appComponent(App.getAppComponent())
                    .networkModule(new NetworkModule())
                    .localModule(new LocalModule())
                    .build();
            DaggerService.registerComponent(DataManagerComponent.class, dmComponent);
        }
        dmComponent.inject(this);

        updateRetrofitBaseUrl();


        Configuration config = new Configuration.Builder(App.getContext())
                .minConsumerCount(AppConfig.MIN_CONSUMER_COUNT) //minimum 1 consumer alive
                .maxConsumerCount(AppConfig.MAX_CONSUMER_COUNT) //maximum 3 consumers
                .loadFactor(AppConfig.LOAD_FACTOR) // maximum 3 jobs per consumer
                .consumerKeepAlive(AppConfig.KEEP_ALIVE) //wait 2 minutes
                .build();
        mJobManager = new JobManager(config);

        updateLocalDataWithTimer();
    }



    private void updateLocalDataWithTimer() {
        Observable.interval(AppConfig.JOB_UPDATE_DATA_INTERVAL, TimeUnit.SECONDS) //генерируем последовательность из элдементов каждые 30 сек
                .filter(aLong -> mPreferencesManager.getAutoSynchronize()) //идем дальше только если интернет есть
                .filter(aLong -> mJobManager.count()==0)
                .flatMap(aLong -> NetworkStatusChecker.isInternetAvailiableObs()) //проверяем состяние интернета
                .filter(aBoolean -> aBoolean) //идем дальше только если интернет есть
                .doOnNext(aBoolean -> updateAllAsync()) //получаем новые товары из сети
                .subscribe(aBoolean -> {

                }, throwable -> {

                });
    }
    private void updateRetrofitBaseUrl(){
        String baseServer = mPreferencesManager.getServerAddress();
        mRetrofit =  mRetrofit.newBuilder().baseUrl(String.format(AppConfig.API_URL, baseServer)).build();
        mRestService = mRetrofit.create(RestService.class);
    }


    public static DataManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new DataManager();
        }
        return ourInstance;
    }

    public void clearDataBase() {
        mRealmManager.clearDataBase();
        mPreferencesManager.clearLastUpdate();
    }

    public void cancelAllJobs(){
        mJobManager.clear();
        mJobManager.cancelJobs(TagConstraint.ALL, "");
    }

    public void updateAllAsync() {
        Job jGroups = new FetchRemoteGoodGroupsJob();
        Job jItems = new FetchRemoteGoodItemsJob();
        Job jCustomers = new FetchRemoteCustomersJob();
        Job jOrders = new FetchRemoteOrdersJob();

        try {mJobManager.addJobInBackground(jGroups);}catch (Throwable ignore){}
        try {mJobManager.addJobInBackground(jItems);}catch (Throwable ignore){}
        try {mJobManager.addJobInBackground(jCustomers);}catch (Throwable ignore){}
        try {mJobManager.addJobInBackground(jOrders);}catch (Throwable ignore){}
    }


    //region ===================== Getters =========================


    public Retrofit getRetrofit() {
        return mRetrofit;
    }


    //endregion ================== Getters =========================

    
    //region ===================== UserInfo =========================

    public boolean isUserAuth() {
        if (mPreferencesManager.getUserAuthToken().isEmpty()){
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date dueDate = sdf.parse(mPreferencesManager.getUserAuthTokenExpiration());
            return !dueDate.before(Calendar.getInstance().getTime()) || !NetworkStatusChecker.isNetworkAvailable();

        } catch (ParseException ignore) {
        }
        return false;
    }


    public String getUserName() {
        return mPreferencesManager.getUserName();
    }

    public void updateUserName(String login) {
        mPreferencesManager.updateUserName(login);
    }

    public String getUserPassword() {
        return mPreferencesManager.getUserPassword();
    }

    public void updateUserPassword(String pass) {
        mPreferencesManager.updateUserPassword(pass);
    }


    public Observable<UserRes> loginUser(String userName, String password) {

        if (userName.equals(AppConfig.TEST_USERNAME) && password.equals(AppConfig.TEST_USERPASSWORD)){
            updateUserName(userName);
            updateUserPassword(password);
            return Observable.just(DebugManager.loginUser(userName, password));
        }

        return mRestService.loginUser(new UserLoginReq(userName, password))
                .compose(new RestCallTransformer<>())
                .flatMap(res -> {
                    updateUserName(userName);
                    updateUserPassword(password);
                    return Observable.just(res);
                });
    }

    private void setUserAuthToken(String token, String expires) {
        mPreferencesManager.updateUserAuthToken(token, expires);
    }

    private void setManagerName(String managerName) {
        mPreferencesManager.updateManagerName(managerName);
    }

    public void updateUserData(UserRes userRes) {
        setManagerName(userRes.getFullname());
        setUserAuthToken(userRes.getToken(), userRes.getDueDate());
    }

    public String getManagerName() {
        return mPreferencesManager.getManagerName();
    }

    //endregion ================== UserInfo =========================

    
    //region ===================== Customers =========================
    public Observable<List<CustomerRealm>> getCustomersFromRealm(String filter) {
        return mRealmManager.getCustomersList(filter);
    }

    public CustomerRealm getCustomerById(String id){
        return mRealmManager.getCustomerById(id);
    }

    public int getCustomerDebtType(String id){
        return mRealmManager.getCustomerDebtType(id);
    }

    public Observable<List<NoteRealm>> getCustomerNotes(String customerId) {
        return mRealmManager.getCustomerNotes(customerId);
    }

    public Observable<List<DebtRealm>> getCustomerDebtByType(String customerId, int debtType) {
        return mRealmManager.getCustomerDebtByType(customerId, debtType);
    }

    public Observable<List<TaskRealm>> getCustomerTasksByType(String customerId, int taskType) {
        return mRealmManager.getCustomerTaskByType(customerId, taskType);
    }

    public Observable<List<OrderPlanRealm>> getCustomerPlan(String customerId) {
        return mRealmManager.getCustomerPlan(customerId);
    }


    public void updateCustomerTask(String taskId, boolean checked, String result) {
        mRealmManager.updateCustomerTask(taskId, checked, result);
    }

    public Observable<List<CustomerRealm>> getCustomersByVisitDate(Date day) {
        return mRealmManager.getCustomersByVisitDate(day);
    }

    public void addNewNote(String customerId, String note) {
        mRealmManager.addNewNote(customerId, note);

        sendAllNotes(customerId);
    }

    public void deleteNote(String noteId) {
        mRealmManager.deleteNote(noteId);
    }


    public Observable<CustomerRealm> updateCustomersFromRemote(){
        if (getUserName().equals(AppConfig.TEST_USERNAME)){
            return Observable.empty();
        }
        
        return mRestService.getCustomerList(mPreferencesManager.getUserAuthToken(), mPreferencesManager.getLastUpdate(CustomerRes.class.getSimpleName()))
                .compose(new RestCallTransformer<>()) //трансформируем response и выбрасываем ApiError в слуае ошибки, проверяем статус сети перед запросом, обрабатываем коды ответов
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.GET_DATA_RETRY_COUNT), (throwable, retryCount) -> retryCount)  // последовательность попыток от 1 до 5\
                        .map(retryCount -> (long) (AppConfig.INITIAL_BACK_OFF_IN_MS * Math.pow(Math.E, retryCount))) //генерируем задержку экспоненциально
                        .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS)))  //запускаем таймер
                .flatMap(Observable::fromIterable) //List of ProductRes
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnNext(cust -> {
                    if (!cust.isActive()) {
                        mRealmManager.deleteCustomerFromRealm(cust.getId()); //удалить запись из локальной БД
                    }
                })
                .filter(CustomerRes::isActive) //только активные клиенты
                .flatMap(cust->{
                    mRealmManager.saveCustomerToRealm(cust, true);
                    Job job = new UpdateCustomerJob(cust.getId());
                    try{mJobManager.addJobInBackground(job);}catch (Throwable ignore){}
                    CustomerRealm customer = getCustomerById(cust.getId());
                    if (customer!=null) {
                        return Observable.just(customer);
                    }else{
                        return Observable.empty();
                    }
                })
               ;

    }


    public Observable<CustomerRealm> updateCustomerFromRemote(String customerId){
        if (getUserName().equals(AppConfig.TEST_USERNAME)){
            return Observable.empty();
        }
        
        sendAllNotes(customerId);

        return mRestService.getCustomer(mPreferencesManager.getUserAuthToken(), customerId)
                .compose(new RestCallTransformer<>()) //трансформируем response и выбрасываем ApiError в слуае ошибки, проверяем статус сети перед запросом, обрабатываем коды ответов
                .doOnNext(custRes -> {
                    if (!custRes.isActive()) {
                        mRealmManager.deleteCustomerFromRealm(custRes.getId()); //удалить запись из локальной БД
                    }
                })
                .filter(CustomerRes::isActive) //только активные Клиенты
                .doOnNext(custRes ->  mRealmManager.saveCustomerToRealm(custRes, false)) //Save data on disk
                .flatMap(custRes -> Observable.empty())
                ;
    }

    public Observable<NoteRealm> sendSingleNote(String noteId) {
        NoteRealm note = mRealmManager.getCustomerNoteById(noteId);
        if (note==null || !note.isValid() || note.getCustomer()==null || !note.getCustomer().isValid()){
            return Observable.empty();
        }

        return mRestService.sendNote(mPreferencesManager.getUserAuthToken(), note.getCustomer().getCustomerId(), new SendNoteReq(note))
                .compose(new RestCallTransformer<>())
                .doOnNext(ids -> {
                    mRealmManager.updateNoteExternalId(ids.getOldId(),ids.getNewId() );
                })
                .flatMap(ids -> Observable.empty())
                ;
    }

    public void sendAllNotes(String filter) {
        if (getUserName().equals(AppConfig.TEST_USERNAME)){
            return;
        }

        mRealmManager.getNotesToSend(filter)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .doOnNext(note ->{
                    Job job= new SendCustomerNoteJob(note.getNoteId());
                    try{mJobManager.addJobInBackground(job);}catch (Throwable ignore){}
                    }
                )
                .subscribe();

    }

    //endregion ================== Customers =========================


    //region ===================== Orders =========================

    public Observable<List<OrderRealm>> getCustomerOrders(String customerId) {
        return mRealmManager.getCustomerOrders(customerId);
    }

    public Observable<List<OrderRealm>> getOrders() {
        return mRealmManager.getAllOrders();
    }


    public void setDeliveryDate(String orderId, Date mDate) {
        mRealmManager.setDeliveryDate(orderId, mDate);
    }

    public void updateOrderItemPrice(String orderId, String itemId, Float value) {
        mRealmManager.updateOrderItemPrice(orderId, itemId, value);
    }

    public void updateOrderItemQty(String orderId, String itemId, Float value) {
        mRealmManager.updateOrderItemQty(orderId, itemId, value);
    }

    public void removeOrderItem(String orderId, String itemId) {
        mRealmManager.removeOrderItem(orderId, itemId);
    }

    public Observable<List<OrderLineRealm>> getOrderLines(String orderId) {
        return mRealmManager.getOrderLinesList(orderId);
    }

    public void updateOrderStatus(String orderId, int orderStatus) {
        mRealmManager.updateOrderStatus(orderId, orderStatus);
        if (orderStatus == ConstantManager.ORDER_STATUS_IN_PROGRESS) {
            sendAllOrders(orderId);
        }
    }

    public void clearOrderLines(String orderId) {
        mRealmManager.clearOrderLines(orderId);
    }

    public OrderRealm getCartForCustomer(String customerId) {
        return mRealmManager.getCartForCustomer(customerId) ;
    }

    public void updateOrderComment(String orderId, String comment) {
        mRealmManager.updateOrderComment(orderId, comment);
    }

    public void updateOrderPayment(String orderId, int payment) {
        mRealmManager.updateOrderPayment(orderId, payment);
    }

    public void addItemToCart(String orderId, String itemId, float newQty, float newPrice) {
        mRealmManager.addItemToCart(orderId, itemId, newQty, newPrice);
    }

    public OrderRealm getOrderById(String orderId) {
        return mRealmManager.getOrderById(orderId);
    }


    public Observable<OrderRealm> updateOrdersFromRemote(){
        if (getUserName().equals(AppConfig.TEST_USERNAME)){
            return Observable.empty();
        }
        
        return mRestService.getOrderList(mPreferencesManager.getUserAuthToken(), mPreferencesManager.getLastUpdate(OrderRes.class.getSimpleName()))
                .compose(new RestCallTransformer<>()) //трансформируем response и выбрасываем ApiError в слуае ошибки, проверяем статус сети перед запросом, обрабатываем коды ответов
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.GET_DATA_RETRY_COUNT), (throwable, retryCount) -> retryCount)  // последовательность попыток от 1 до 5\
                        .map(retryCount -> (long) (AppConfig.INITIAL_BACK_OFF_IN_MS * Math.pow(Math.E, retryCount))) //генерируем задержку экспоненциально
                        .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS)))  //запускаем таймер
                .flatMap(Observable::fromIterable) //List of ProductRes
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnNext(order -> {
                    if (!order.isActive()) {
                        mRealmManager.deleteOrderFromRealm(order.getId()); //удалить запись из локальной БД
                    }
                })
                .filter(OrderRes::isActive) //только активные заказы
                .flatMap(order -> {
                    mRealmManager.saveOrderToRealm(order, true);
                    Job job = new UpdateOrderJob(order.getId());
                    try{mJobManager.addJobInBackground(job);}catch (Throwable ignore){}
                    OrderRealm tmpOrder = getOrderById(order.getId());
                    if (tmpOrder != null) {
                        return Observable.just(tmpOrder);
                    }else{
                        return Observable.empty();
                    }

                })
                ;

    }


    public Observable<OrderRes> updateOrderFromRemote(String orderId){
        if (getUserName().equals(AppConfig.TEST_USERNAME)){
            return Observable.empty();
        }
        
        return mRestService.getOrder(mPreferencesManager.getUserAuthToken(), orderId)
                .compose(new RestCallTransformer<>()) //трансформируем response и выбрасываем ApiError в слуае ошибки, проверяем статус сети перед запросом, обрабатываем коды ответов
                .doOnNext(order -> {
                    if (!order.isActive()) {
                        mRealmManager.deleteOrderFromRealm(order.getId()); //удалить запись из локальной БД
                    }
                })
                .filter(OrderRes::isActive) //только активные заказы
                .doOnNext(order ->  mRealmManager.saveOrderToRealm(order, false)) //Save data on disk
                ;
    }

    public Observable<OrderRealm> sendSingleOrder(String orderId){
        OrderRealm order = mRealmManager.getOrderById(orderId);
        List<OrderLineRealm> lines = mRealmManager.getOrderLines(orderId);
        if (order==null || !order.isValid()){
            return Observable.empty();
        }
        return mRestService.sendOrder(mPreferencesManager.getUserAuthToken(), new SendOrderReq(order, lines))
                .compose(new RestCallTransformer<>())
                .doOnNext(ids -> {
                    try {
                        //noinspection ResultOfMethodCallIgnored
                        UUID.fromString(ids.getNewId());
                        mRealmManager.updateOrderExternalId(ids.getOldId(), ids.getNewId());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                })
                .flatMap(ids -> Observable.empty())
                ;
    }

    public void sendAllOrders(String filter) {
        if (getUserName().equals(AppConfig.TEST_USERNAME)){
            return;
        }
        mRealmManager.getOrdersToSend(filter)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .doOnNext(order ->{
                        Job job = new SendOrderJob(order.getId());
                        try{mJobManager.addJobInBackground(job);}catch (Throwable ignore){}
                    }
                )
                .subscribe();

    }


    //endregion ================== Orders =========================


    //region ===================== Goods =========================
    public Observable<List<GoodsGroupRealm>> getGroupList(String parentId, String brand) {
        return mRealmManager.getGroupList(parentId, brand);
    }

    public Observable<List<ItemRealm>> getItemList(String parentId, String filter, String brand, String categoryId) {
        return mRealmManager.getItemList(parentId, filter, brand, categoryId);
    }


    public Observable<GoodsGroupRealm> updateGroupsFromRemote(){
        if (getUserName().equals(AppConfig.TEST_USERNAME)){
            return Observable.empty();
        }

        return mRestService.getGoodsGroupList(mPreferencesManager.getUserAuthToken(), mPreferencesManager.getLastUpdate(GoodGroupRes.class.getSimpleName()))
                .compose(new RestCallTransformer<>()) //трансформируем response и выбрасываем ApiError в слуае ошибки, проверяем статус сети перед запросом, обрабатываем коды ответов
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.GET_DATA_RETRY_COUNT), (throwable, retryCount) -> retryCount)  // последовательность попыток от 1 до 5\
                        .map(retryCount -> (long) (AppConfig.INITIAL_BACK_OFF_IN_MS * Math.pow(Math.E, retryCount))) //генерируем задержку экспоненциально
                        .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS)))  //запускаем таймер
                .flatMap(Observable::fromIterable) //List of ProductRes
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnNext(group -> {
                    if (!group.isActive()) {
                        mRealmManager.deleteGoodsGroupFromRealm(group.getId()); //удалить запись из локальной БД
                    }
                })
                .filter(GoodGroupRes::isActive) //только активные товары
                .flatMap(group ->{
                    mRealmManager.saveGoodGroupToRealm(group, true);
                    Job job = new UpdateGoodGroupJob(group.getId());
                    try{mJobManager.addJobInBackground(job);}catch (Throwable ignore){}
                    GoodsGroupRealm tmpGroup = getGoodGroupById(group.getId());
                    if (tmpGroup != null) {
                        return Observable.just(tmpGroup);
                    }else{
                        return Observable.empty();
                    }
                })
                ;

    }

    public Observable<GoodsGroupRealm> updateGroupFromRemote(String groupId){
        if (getUserName().equals(AppConfig.TEST_USERNAME)){
            return Observable.empty();
        }

        return mRestService.getGoodsGroup(mPreferencesManager.getUserAuthToken(), groupId)
                .compose(new RestCallTransformer<>()) //трансформируем response и выбрасываем ApiError в слуае ошибки, проверяем статус сети перед запросом, обрабатываем коды ответов
                .doOnNext(group -> {
                    if (!group.isActive()) {
                        mRealmManager.deleteGoodsGroupFromRealm(group.getId()); //удалить запись из локальной БД
                    }
                })
                .filter(GoodGroupRes::isActive) //только активные товары
                .doOnNext(group ->  mRealmManager.saveGoodGroupToRealm(group, false)) //Save data on disk
                .flatMap(group -> Observable.empty())
        ;
    }

    private GoodsGroupRealm getGoodGroupById(String id) {
        return mRealmManager.getGroupById(id);
    }


    public Observable<ItemRealm> updateItemsFromRemote(){
        if (getUserName().equals(AppConfig.TEST_USERNAME)){
            return Observable.empty();
        }

        return mRestService.getGoodsList(mPreferencesManager.getUserAuthToken(), mPreferencesManager.getLastUpdate(GoodItemRes.class.getSimpleName()))
                .compose(new RestCallTransformer<>()) //трансформируем response и выбрасываем ApiError в слуае ошибки, проверяем статус сети перед запросом, обрабатываем коды ответов
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.GET_DATA_RETRY_COUNT), (throwable, retryCount) -> retryCount)  // последовательность попыток от 1 до 5\
                        .map(retryCount -> (long) (AppConfig.INITIAL_BACK_OFF_IN_MS * Math.pow(Math.E, retryCount))) //генерируем задержку экспоненциально
                        .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS)))  //запускаем таймер
                .flatMap(Observable::fromIterable) //List of GoodItemRes
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnNext(item -> {
                    if (!item.isActive()) {
                        mRealmManager.deleteGoodItemFromRealm(item.getId()); //удалить запись из локальной БД
                    }
                })
                .filter(GoodItemRes::isActive) //только активные товары
                .flatMap(item -> {
                    mRealmManager.saveGoodItemToRealm(item, true); //Save data on disk
                    Job job = new UpdateGoodItemJob(item.getId());
                    try{mJobManager.addJobInBackground(job);}catch (Throwable ignore){}
                    ItemRealm tmpItem = getItemById(item.getId());
                    if (tmpItem != null) {
                        return Observable.just(tmpItem);
                    }else{
                        return Observable.empty();
                    }
                })
                ;

    }


    public Observable<ItemRealm> updateGoodItemFromRemote(String itemId){
        if (getUserName().equals(AppConfig.TEST_USERNAME)){
            return Observable.empty();
        }

        return mRestService.getGoodItem(mPreferencesManager.getUserAuthToken(), itemId)
                .compose(new RestCallTransformer<>()) //трансформируем response и выбрасываем ApiError в слуае ошибки, проверяем статус сети перед запросом, обрабатываем коды ответов
                .doOnNext(item -> {
                    if (!item.isActive()) {
                        mRealmManager.deleteGoodItemFromRealm(item.getId()); //удалить запись из локальной БД
                    }
                })
                .filter(GoodItemRes::isActive) //только активные товары
                .doOnNext(item ->  mRealmManager.saveGoodItemToRealm(item, false)) //Save data on disk
                .flatMap(item -> Observable.empty())
                ;
    }

    public Float getCustomerDiscount(String customerId, String itemId) {
        return mRealmManager.getCustomerDiscount(customerId, itemId);
    }

    public Observable<List<BrandsRealm>> getBrands() {
        return mRealmManager.getAllBrands();
    }

    public ItemRealm getItemById(String itemId) {
        return mRealmManager.getItemById(itemId);
    }

    //endregion ================== Goods =========================


    //region ===================== Preferences =========================

    public String getServerAddress() {
        return mPreferencesManager.getServerAddress();
    }

    public void updateServerAddress(String address) {
        mPreferencesManager.updateServerAddress(address);
        updateRetrofitBaseUrl();
    }


    public Boolean getAutoSynchronize() {
        return mPreferencesManager.getAutoSynchronize();
    }

    public void updateAutoSynchronize(Boolean sync) {
        mPreferencesManager.updateAutoSynchronize(sync);
    }


    public String getLastUpdate(String module){
        return mPreferencesManager.getLastUpdate(module);
    }

    public void setLastUpdate(String module, String lastModified){
        mPreferencesManager.saveLastUpdate(module, lastModified);
    }

    //endregion ================== Preferences =========================


}

