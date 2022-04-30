package com.sokolua.manager.data.managers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.TagConstraint;
import com.birbit.android.jobqueue.callback.JobManagerCallback;
import com.birbit.android.jobqueue.config.Configuration;
import com.sokolua.manager.data.network.RestCallTransformer;
import com.sokolua.manager.data.network.RestService;
import com.sokolua.manager.data.network.req.SendNoteReq;
import com.sokolua.manager.data.network.req.SendOrderReq;
import com.sokolua.manager.data.network.req.SendTaskReq;
import com.sokolua.manager.data.network.req.SendVisitReq;
import com.sokolua.manager.data.network.req.UserLoginReq;
import com.sokolua.manager.data.network.res.CustomerRes;
import com.sokolua.manager.data.network.res.GoodGroupRes;
import com.sokolua.manager.data.network.res.GoodItemRes;
import com.sokolua.manager.data.network.res.OrderRes;
import com.sokolua.manager.data.network.res.UserRes;
import com.sokolua.manager.data.storage.realm.BrandsRealm;
import com.sokolua.manager.data.storage.realm.CurrencyRealm;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderPlanRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.data.storage.realm.PriceListItemRealm;
import com.sokolua.manager.data.storage.realm.PriceListRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;
import com.sokolua.manager.data.storage.realm.TradeRealm;
import com.sokolua.manager.data.storage.realm.VisitRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.components.DaggerDataManagerComponent;
import com.sokolua.manager.di.components.DataManagerComponent;
import com.sokolua.manager.di.modules.LocalModule;
import com.sokolua.manager.di.modules.NetworkModule;
import com.sokolua.manager.jobs.FetchRemoteCurrencyJob;
import com.sokolua.manager.jobs.FetchRemoteCustomersJob;
import com.sokolua.manager.jobs.FetchRemoteGoodGroupsJob;
import com.sokolua.manager.jobs.FetchRemoteGoodItemsJob;
import com.sokolua.manager.jobs.FetchRemoteOrdersJob;
import com.sokolua.manager.jobs.FetchRemoteTradesJob;
import com.sokolua.manager.jobs.SendCustomerNoteJob;
import com.sokolua.manager.jobs.SendCustomerTaskJob;
import com.sokolua.manager.jobs.SendOrderJob;
import com.sokolua.manager.jobs.SendVisitJob;
import com.sokolua.manager.jobs.UpdateCustomerJob;
import com.sokolua.manager.jobs.UpdateGoodGroupJob;
import com.sokolua.manager.jobs.UpdateGoodItemJob;
import com.sokolua.manager.jobs.UpdateOrderJob;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.AppConfig;
import com.sokolua.manager.utils.MiscUtils;
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
import io.reactivex.disposables.Disposable;
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

    private final JobManager mJobManager;
    private Disposable autoUpdateDisposable;


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
                .resetDelaysOnRestart()
                .build();
        mJobManager = new JobManager(config);
        mJobManager.addCallback(new JobManagerCallback() {
            @Override
            public void onJobAdded(@NonNull Job job) {}

            @Override
            public void onJobRun(@NonNull Job job, int resultCode) {}

            @Override
            public void onJobCancelled(@NonNull Job job, boolean byCancelRequest, @Nullable Throwable throwable) {
                if (!byCancelRequest && job.isCancelled() && job.isPersistent() ){
                    if (job instanceof FetchRemoteCurrencyJob) mJobManager.addJobInBackground(new FetchRemoteCurrencyJob());
                    if (job instanceof FetchRemoteCustomersJob) mJobManager.addJobInBackground(new FetchRemoteCustomersJob());
                    if (job instanceof FetchRemoteGoodGroupsJob) mJobManager.addJobInBackground(new FetchRemoteGoodGroupsJob());
                    if (job instanceof FetchRemoteGoodItemsJob) mJobManager.addJobInBackground(new FetchRemoteGoodItemsJob());
                    if (job instanceof FetchRemoteOrdersJob) mJobManager.addJobInBackground(new FetchRemoteOrdersJob());
                    if (job instanceof FetchRemoteTradesJob) mJobManager.addJobInBackground(new FetchRemoteTradesJob());
                    if (job instanceof SendCustomerNoteJob) mJobManager.addJobInBackground(new SendCustomerNoteJob(((SendCustomerNoteJob) job).getJobId()));
                    if (job instanceof SendCustomerTaskJob) mJobManager.addJobInBackground(new SendCustomerTaskJob(((SendCustomerTaskJob) job).getJobId()));
                    if (job instanceof SendOrderJob) mJobManager.addJobInBackground(new SendOrderJob(((SendOrderJob) job).getJobId()));
                    if (job instanceof SendVisitJob) mJobManager.addJobInBackground(new SendVisitJob(((SendVisitJob) job).getJobId()));
                    if (job instanceof UpdateGoodGroupJob) mJobManager.addJobInBackground(new UpdateGoodGroupJob(((UpdateGoodGroupJob) job).getJobId()));
                    if (job instanceof UpdateGoodItemJob) mJobManager.addJobInBackground(new UpdateGoodItemJob(((UpdateGoodItemJob) job).getJobId()));
                    if (job instanceof UpdateOrderJob) mJobManager.addJobInBackground(new UpdateOrderJob(((UpdateOrderJob) job).getJobId()));

                    Log.d(App.getContext().getPackageName(), "Job "+job.getClass().getSimpleName()+" id = " + job.getSingleInstanceId() + " restarted!");
                    if (throwable!=null) {
                        Log.e(App.getContext().getPackageName(), throwable.getMessage(), throwable);
                    }
                }
            }

            @Override
            public void onDone(@NonNull Job job) {}

            @Override
            public void onAfterJobRun(@NonNull Job job, int resultCode) {}
        });

        if (!getAutoSynchronize()) {
            mJobManager.stop();
        }

        updateLocalDataWithTimer();
    }

    @Override
    protected void finalize() throws Throwable {
        if (autoUpdateDisposable != null) {
            autoUpdateDisposable.dispose();
        }
        super.finalize();
    }

    private void updateLocalDataWithTimer() {
        autoUpdateDisposable = Observable.interval(AppConfig.JOB_UPDATE_DATA_INTERVAL, TimeUnit.SECONDS) //генерируем последовательность из элдементов каждые 30 сек
                .filter(aLong -> mPreferencesManager.getAutoSynchronize()) //идем дальше только если есть настройка
                .filter(aLong -> mJobManager.count() == 0)
                .flatMap(aLong -> NetworkStatusChecker.isInternetAvailableObs()) //проверяем состяние интернета
                .filter(aBoolean -> aBoolean) //идем дальше только если интернет есть
                .doOnNext(aBoolean -> updateAllAsync()) //получаем новые товары из сети
                .doOnError(throwable -> Log.e("ERROR","Update all", throwable) )
                .doOnComplete(() -> autoUpdateDisposable.dispose())
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
        mJobManager.cancelJobsInBackground(cancelResult -> mJobManager.clear(), TagConstraint.ANY, ConstantManager.UPDATE_JOB_TAG);
    }

    public Boolean jobQueueIsEmpty(){
        return mJobManager.count() == 0;
    }

    public void updateAllAsync() {
        mRealmManager.compactDatabase();

        sendAllNotes("");
        sendAllTasks("");
        sendAllOrders("");
        sendAllVisits("");

        Job jCurrency = new FetchRemoteCurrencyJob();
        Job jTrades = new FetchRemoteTradesJob();
        Job jGroups = new FetchRemoteGoodGroupsJob();
        Job jItems = new FetchRemoteGoodItemsJob();
        Job jCustomers = new FetchRemoteCustomersJob();
        Job jOrders = new FetchRemoteOrdersJob();

        try {mJobManager.addJobInBackground(jCurrency);}catch (Throwable ignore){}
        try {mJobManager.addJobInBackground(jTrades);}catch (Throwable ignore){}
        try {mJobManager.addJobInBackground(jGroups);}catch (Throwable ignore){}
        try {mJobManager.addJobInBackground(jItems);}catch (Throwable ignore){}
        try {mJobManager.addJobInBackground(jCustomers);}catch (Throwable ignore){}
        try {mJobManager.addJobInBackground(jOrders);}catch (Throwable ignore){}
    }


    //region ===================== Getters =========================


    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public JobManager getJobManager() { return mJobManager; }


    //endregion ================== Getters =========================

    
    //region ===================== UserInfo =========================

    public boolean isUserAuth() {
        if (mPreferencesManager.getUserAuthToken().isEmpty()){
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date dueDate = sdf.parse(mPreferencesManager.getUserAuthTokenExpiration());
            if (dueDate == null) {
                return false;
            }else{
                return !dueDate.before(Calendar.getInstance().getTime()) || !NetworkStatusChecker.isNetworkAvailable();
            }

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
//                .onErrorResumeNext(throwable -> {
//                    Log.e("SYNC","login", throwable);
//                    return Observable.empty();
//                })
                .flatMap(res -> {
                    updateUserName(userName);
                    updateUserPassword(password);
                    return Observable.just(res);
                })
                ;
    }

    private void setUserAuthToken(String token, String expires) {
        mPreferencesManager.updateUserAuthToken(token, expires);
    }

    private void setManagerName(String managerName) {
        mPreferencesManager.updateManagerName(managerName);
    }

    public void updateUserData(UserRes userRes) {
        setManagerName(userRes.getFullName());
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
        Job sendJob = new SendCustomerTaskJob(taskId);
        mJobManager.addJobInBackground(sendJob);
    }

    public Observable<List<CustomerRealm>> getCustomersByVisitDate(Date day) {
        return mRealmManager.getCustomersByVisitDate(day);
    }

    public VisitRealm getVisitById(String visitId) {
        return mRealmManager.getVisitById(visitId);
    }

    public Observable<List<VisitRealm>> getVisitsByDate(Date day) {
        return mRealmManager.getVisitsByDate(day);
    }

    public void addNewNote(String customerId, String note) {
        mRealmManager.addNewNote(customerId, note);

        sendAllNotes(customerId);
    }

    public void deleteNote(String noteId) {
        mRealmManager.deleteNote(noteId);
    }


    public Observable<CustomerRealm> updateCustomersFromRemote(){
        if (!isUserAuth() || getUserName().equals(AppConfig.TEST_USERNAME)){
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
                .onExceptionResumeNext(Observable.empty())
                .onErrorResumeNext(throwable -> {
                    Log.e("SYNC","customer list", throwable);
                    return Observable.empty();
                })
               ;

    }


    public Observable<CustomerRealm> updateCustomerFromRemote(String customerId){
        if (!isUserAuth() || getUserName().equals(AppConfig.TEST_USERNAME)){
            return Observable.empty();
        }
        return mRestService.getCustomer(mPreferencesManager.getUserAuthToken(), customerId)
                .compose(new RestCallTransformer<>()) //трансформируем response и выбрасываем ApiError в слуае ошибки, проверяем статус сети перед запросом, обрабатываем коды ответов
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.GET_DATA_RETRY_COUNT), (throwable, retryCount) -> retryCount)  // последовательность попыток от 1 до 5\
                        .map(retryCount -> (long) (AppConfig.INITIAL_BACK_OFF_IN_MS * Math.pow(Math.E, retryCount))) //генерируем задержку экспоненциально
                        .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS)))  //запускаем таймер
                .doOnNext(custRes -> {
                    if (!custRes.isActive()) {
                        mRealmManager.deleteCustomerFromRealm(custRes.getId()); //удалить запись из локальной БД
                    }
                })
                .filter(CustomerRes::isActive) //только активные Клиенты
                .doOnNext(custRes ->  mRealmManager.saveCustomerToRealm(custRes, false)) //Save data on disk
                .onErrorResumeNext(throwable -> {
                    Log.e("SYNC","customer", throwable);
                    return Observable.empty();
                })
                .flatMap(custRes -> Observable.empty())
                ;
    }

    public Observable<NoteRealm> sendSingleNote(String noteId) {
        NoteRealm note = mRealmManager.getCustomerNoteById(noteId);
        if (!isUserAuth() || note==null || !note.isValid() || note.getCustomer()==null || !note.getCustomer().isValid()){
            return Observable.empty();
        }

        return mRestService.sendNote(mPreferencesManager.getUserAuthToken(), note.getCustomer().getCustomerId(), new SendNoteReq(note))
                .compose(new RestCallTransformer<>())
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.GET_DATA_RETRY_COUNT), (throwable, retryCount) -> retryCount)  // последовательность попыток от 1 до 5\
                        .map(retryCount -> (long) (AppConfig.INITIAL_BACK_OFF_IN_MS * Math.pow(Math.E, retryCount))) //генерируем задержку экспоненциально
                        .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS)))  //запускаем таймер
                .doOnNext(ids -> {
                    mRealmManager.updateNoteExternalId(ids.getOldId(),ids.getNewId() );
                })
                .onExceptionResumeNext(Observable.empty())
                .onErrorResumeNext(throwable -> {
                    Log.e("SYNC","note send", throwable);
                    return Observable.empty();
                })
                .flatMap(ids -> Observable.empty())
                ;
    }

    public void sendAllNotes(String filter) {
        if (!isUserAuth() || getUserName().equals(AppConfig.TEST_USERNAME)){
            return;
        }

        mRealmManager.getNotesToSend(filter)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .doOnNext(note -> {
                            Job job = new SendCustomerNoteJob(note.getNoteId());
                            try {
                                mJobManager.addJobInBackground(job);
                            } catch (Throwable ignore) {
                            }
                        }
                )
                .doOnError(throwable -> Log.e("ERROR", "Send notes", throwable))
                .subscribe(

                );

    }

    public Observable<TaskRealm> sendSingleTask(String taskId) {
        TaskRealm task = mRealmManager.getCustomerTaskByTypeId(taskId);
        if (!isUserAuth() || task==null || !task.isValid() || task.getCustomer()==null || !task.getCustomer().isValid()){
            return Observable.empty();
        }

        return mRestService.sendTask(mPreferencesManager.getUserAuthToken(), task.getCustomer().getCustomerId(), new SendTaskReq(task))
                .compose(new RestCallTransformer<>())
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.GET_DATA_RETRY_COUNT), (throwable, retryCount) -> retryCount)  // последовательность попыток от 1 до 5\
                        .map(retryCount -> (long) (AppConfig.INITIAL_BACK_OFF_IN_MS * Math.pow(Math.E, retryCount))) //генерируем задержку экспоненциально
                        .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS)))  //запускаем таймер
                .doOnNext(ids ->
                    mRealmManager.setCustomerTaskSynced(taskId)
                )
                .onExceptionResumeNext(Observable.empty())
                .onErrorResumeNext(throwable -> {
                    Log.e("SYNC","task send", throwable);
                    return Observable.empty();
                })
                .flatMap(ids -> Observable.empty())
                ;
    }

    public void sendAllTasks(String filter) {
        if (!isUserAuth() || getUserName().equals(AppConfig.TEST_USERNAME)){
            return;
        }

        mRealmManager.getCustomerTasks(filter)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .flatMapIterable(task -> task)
                .filter(TaskRealm::isToSync)
                .doOnNext(task ->{
                            Job job = new SendCustomerTaskJob(task.getTaskId());
                            try{mJobManager.addJobInBackground(job);}catch (Throwable ignore){}
                        }
                )
                .doOnError(throwable -> Log.e("ERROR","Send tasks", throwable) )
                .subscribe();

    }

    public void updateVisitGeolocation(String visitId, float mLat, float mLong) {
        mRealmManager.updateVisitGeolocation(visitId, mLat, mLong);
        Job job = new SendVisitJob(visitId);
        try{mJobManager.addJobInBackground(job);}catch (Throwable ignore){}
    }

    public void updateVisitScreenshot(String visitId, String imageURI) {
        mRealmManager.updateVisitScreenshot(visitId, imageURI);
        Job job = new SendVisitJob(visitId);
        try{mJobManager.addJobInBackground(job);}catch (Throwable ignore){}
    }

    public Observable<VisitRealm> sendSingleVisit(String visitId) {
        VisitRealm visit = mRealmManager.getVisitById(visitId);
        if (!isUserAuth() || visit==null || !visit.isValid() || visit.getCustomer()==null || !visit.getCustomer().isValid()){
            return Observable.empty();
        }

        return mRestService.sendVisit(mPreferencesManager.getUserAuthToken(), visit.getCustomer().getCustomerId(), new SendVisitReq(visit))
                .compose(new RestCallTransformer<>())
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.GET_DATA_RETRY_COUNT), (throwable, retryCount) -> retryCount)  // последовательность попыток от 1 до 5\
                        .map(retryCount -> (long) (AppConfig.INITIAL_BACK_OFF_IN_MS * Math.pow(Math.E, retryCount))) //генерируем задержку экспоненциально
                        .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS)))  //запускаем таймер
                .doOnNext(ids ->
                        mRealmManager.setVisitSynced(visitId)
                )
                .onExceptionResumeNext(Observable.empty())
                .onErrorResumeNext(throwable -> {
                    Log.e("SYNC","visit send", throwable);
                    return Observable.empty();
                })
                .flatMap(ids -> Observable.empty())
                ;
    }



    public void sendAllVisits(String filter) {
        if (!isUserAuth() || getUserName().equals(AppConfig.TEST_USERNAME)){
            return;
        }

        mRealmManager.getCustomerVisits(filter)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .flatMapIterable(task -> task)
                .filter(VisitRealm::isToSync)
                .doOnNext(visit ->{
                            Job job = new SendVisitJob(visit.getId());
                            try{mJobManager.addJobInBackground(job);}catch (Throwable ignore){}
                        }
                )
                .doOnError(throwable -> Log.e("ERROR","Send visits", throwable) )
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
        if (!isUserAuth() || getUserName().equals(AppConfig.TEST_USERNAME)){
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
                .onExceptionResumeNext(Observable.empty())
                .onErrorResumeNext(throwable -> {
                    Log.e("SYNC","order list", throwable);
                    return Observable.empty();
                })

                ;

    }


    public Observable<OrderRealm> updateOrderFromRemote(String orderId){
        if (!isUserAuth() || getUserName().equals(AppConfig.TEST_USERNAME)){
            return Observable.empty();
        }
        
        return mRestService.getOrder(mPreferencesManager.getUserAuthToken(), orderId)
                .compose(new RestCallTransformer<>()) //трансформируем response и выбрасываем ApiError в слуае ошибки, проверяем статус сети перед запросом, обрабатываем коды ответов
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.GET_DATA_RETRY_COUNT), (throwable, retryCount) -> retryCount)  // последовательность попыток от 1 до 5\
                        .map(retryCount -> (long) (AppConfig.INITIAL_BACK_OFF_IN_MS * Math.pow(Math.E, retryCount))) //генерируем задержку экспоненциально
                        .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS)))  //запускаем таймер
                .doOnNext(order -> {
                    if (!order.isActive()) {
                        mRealmManager.deleteOrderFromRealm(order.getId()); //удалить запись из локальной БД
                    }
                })
                .filter(OrderRes::isActive) //только активные заказы
                .doOnNext(order ->  mRealmManager.saveOrderToRealm(order, false)) //Save data on disk
                .onExceptionResumeNext(Observable.empty())
                .onErrorResumeNext(throwable -> {
                    Log.e("SYNC","order", throwable);
                    return Observable.empty();
                })
                .flatMap(item -> Observable.empty())
                ;
    }

    public List<OrderLineRealm> getCopyOfOrderLines(String orderId){
      return mRealmManager.getOrderLines(orderId);
    }

    public Observable<OrderRealm> sendSingleOrder(String orderId){
        OrderRealm order = mRealmManager.getOrderById(orderId);
        List<OrderLineRealm> lines = mRealmManager.getOrderLines(orderId);
        if (!isUserAuth() || order==null || !order.isValid()){
            return Observable.empty();
        }
        return mRestService.sendOrder(mPreferencesManager.getUserAuthToken(), new SendOrderReq(order, lines))
                .compose(new RestCallTransformer<>())
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.GET_DATA_RETRY_COUNT), (throwable, retryCount) -> retryCount)  // последовательность попыток от 1 до 5\
                        .map(retryCount -> (long) (AppConfig.INITIAL_BACK_OFF_IN_MS * Math.pow(Math.E, retryCount))) //генерируем задержку экспоненциально
                        .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS)))  //запускаем таймер
                .doOnNext(ids -> {
                    try {
                        //noinspection ResultOfMethodCallIgnored
                        UUID.fromString(ids.getNewId());
                        mRealmManager.updateOrderExternalId(ids.getOldId(), ids.getNewId());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                })
                .onExceptionResumeNext(Observable.empty())
                .onErrorResumeNext(throwable -> {
                    Log.e("SYNC","order send", throwable);
                    return Observable.empty();
                })
                .flatMap(ids -> Observable.empty())
                ;
    }

    public void sendAllOrders(String filter) {
        if (!isUserAuth() || getUserName().equals(AppConfig.TEST_USERNAME)){
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
                .doOnError(throwable -> Log.e("ERROR","Send orders", throwable) )
                .subscribe();

    }


    public void updateOrderCurrency(String orderId, String currencyCode) {
        mRealmManager.updateOrderCurrency(orderId, currencyCode);
    }

    public void updateOrderTrade(String orderId, String tradeId) {
        mRealmManager.updateOrderTrade(orderId, tradeId);
    }

    public void updateOrderFactFlag(String orderId, boolean fact) {
        mRealmManager.updateOrderFactFlag(orderId, fact);
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
        if (!isUserAuth() || getUserName().equals(AppConfig.TEST_USERNAME)){
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
                .onExceptionResumeNext(Observable.empty())
                .onErrorResumeNext(throwable -> {
                    Log.e("SYNC","groups list", throwable);
                    return Observable.empty();
                })

                ;

    }

    public Observable<GoodsGroupRealm> updateGroupFromRemote(String groupId){
        if (!isUserAuth() || getUserName().equals(AppConfig.TEST_USERNAME)){
            return Observable.empty();
        }

        return mRestService.getGoodsGroup(mPreferencesManager.getUserAuthToken(), groupId)
                .compose(new RestCallTransformer<>()) //трансформируем response и выбрасываем ApiError в слуае ошибки, проверяем статус сети перед запросом, обрабатываем коды ответов
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.GET_DATA_RETRY_COUNT), (throwable, retryCount) -> retryCount)  // последовательность попыток от 1 до 5\
                        .map(retryCount -> (long) (AppConfig.INITIAL_BACK_OFF_IN_MS * Math.pow(Math.E, retryCount))) //генерируем задержку экспоненциально
                        .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS)))  //запускаем таймер
                .doOnNext(group -> {
                    if (!group.isActive()) {
                        mRealmManager.deleteGoodsGroupFromRealm(group.getId()); //удалить запись из локальной БД
                    }
                })
                .filter(GoodGroupRes::isActive) //только активные товары
                .doOnNext(group ->  mRealmManager.saveGoodGroupToRealm(group, false)) //Save data on disk
                .onExceptionResumeNext(Observable.empty())
                .onErrorResumeNext(throwable -> {
                    Log.e("SYNC","group", throwable);
                    return Observable.empty();
                })
                .flatMap(group -> Observable.empty())
        ;
    }

    private GoodsGroupRealm getGoodGroupById(String id) {
        return mRealmManager.getGroupById(id);
    }


    public Observable<ItemRealm> updateItemsFromRemote(){
        if (!isUserAuth() || getUserName().equals(AppConfig.TEST_USERNAME)){
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
                .onExceptionResumeNext(Observable.empty())
                .onErrorResumeNext(throwable -> {
                    Log.e("SYNC","items list", throwable);
                    return Observable.empty();
                })
                ;

    }


    public Observable<ItemRealm> updateGoodItemFromRemote(String itemId){
        if (!isUserAuth() || getUserName().equals(AppConfig.TEST_USERNAME)){
            return Observable.empty();
        }

        return mRestService.getGoodItem(mPreferencesManager.getUserAuthToken(), itemId)
                .compose(new RestCallTransformer<>()) //трансформируем response и выбрасываем ApiError в слуае ошибки, проверяем статус сети перед запросом, обрабатываем коды ответов
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.GET_DATA_RETRY_COUNT), (throwable, retryCount) -> retryCount)  // последовательность попыток от 1 до 5\
                        .map(retryCount -> (long) (AppConfig.INITIAL_BACK_OFF_IN_MS * Math.pow(Math.E, retryCount))) //генерируем задержку экспоненциально
                        .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS)))  //запускаем таймер
                .doOnNext(item -> {
                    if (!item.isActive()) {
                        mRealmManager.deleteGoodItemFromRealm(item.getId()); //удалить запись из локальной БД
                    }
                })
                .filter(GoodItemRes::isActive) //только активные товары
                .doOnNext(item ->  mRealmManager.saveGoodItemToRealm(item, false)) //Save data on disk
                .onExceptionResumeNext(Observable.empty())
                .onErrorResumeNext(throwable -> {
                    Log.e("SYNC","item", throwable);
                    return Observable.empty();
                })
                .flatMap(item -> Observable.empty())
                ;
    }


    public Observable<List<BrandsRealm>> getBrands() {
        return mRealmManager.getAllBrands();
    }

    public ItemRealm getItemById(String itemId) {
        return mRealmManager.getItemById(itemId);
    }

    //endregion ================== Goods =========================


    //region ======================= Prices =========================

    public Observable<CurrencyRealm> updateCurrencyFromRemote() {
        if (!isUserAuth() || getUserName().equals(AppConfig.TEST_USERNAME)){
            return Observable.empty();
        }

        return mRestService.getCurrencyList(mPreferencesManager.getUserAuthToken())
                .compose(new RestCallTransformer<>()) //трансформируем response и выбрасываем ApiError в слуае ошибки, проверяем статус сети перед запросом, обрабатываем коды ответов
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.GET_DATA_RETRY_COUNT), (throwable, retryCount) -> retryCount)  // последовательность попыток от 1 до 5\
                        .map(retryCount -> (long) (AppConfig.INITIAL_BACK_OFF_IN_MS * Math.pow(Math.E, retryCount))) //генерируем задержку экспоненциально
                        .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS)))  //запускаем таймер
                .flatMap(Observable::fromIterable) //List of Currencies
                .doOnNext(item ->  mRealmManager.saveCurrencyToRealm(item)) //Save data on disk
                .onExceptionResumeNext(Observable.empty())
                .onErrorResumeNext(throwable -> {
                    Log.e("SYNC","currency", throwable);
                    return Observable.empty();
                })
                .flatMap(item -> Observable.empty())
                ;    }

    public Observable<TradeRealm> updateTradesFromRemote() {
        if (!isUserAuth() || getUserName().equals(AppConfig.TEST_USERNAME)){
            return Observable.empty();
        }

        return mRestService.getTradesList(mPreferencesManager.getUserAuthToken())
                .compose(new RestCallTransformer<>()) //трансформируем response и выбрасываем ApiError в слуае ошибки, проверяем статус сети перед запросом, обрабатываем коды ответов
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.GET_DATA_RETRY_COUNT), (throwable, retryCount) -> retryCount)  // последовательность попыток от 1 до 5\
                        .map(retryCount -> (long) (AppConfig.INITIAL_BACK_OFF_IN_MS * Math.pow(Math.E, retryCount))) //генерируем задержку экспоненциально
                        .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS)))  //запускаем таймер
                .flatMap(Observable::fromIterable) //List of Trades
                .doOnNext(item ->  mRealmManager.saveTradeToRealm(item)) //Save data on disk
                .onExceptionResumeNext(Observable.empty())
                .onErrorResumeNext(throwable -> {
                    Log.e("SYNC","trade", throwable);
                    return Observable.empty();
                })
                .flatMap(item -> Observable.empty())
                ;    }

    public Observable<List<PriceListRealm>> getPriceLists() {
        return mRealmManager.getAllPriceLists();
    }

    public Observable<List<TradeRealm>> getTrades(@Nullable Boolean fop, @Nullable Boolean cash, @Nullable Boolean fact, @Nullable Boolean remote) {
        return mRealmManager.getAllTrades(fop, cash, fact, remote);
    }

    public PriceListRealm getPriceById(String priceId) {
        return mRealmManager.getPriceListById(priceId);
    }

    public Observable<List<CurrencyRealm>> getCurrencyList() {
        return mRealmManager.getAllCurrencies();
    }

    public TradeRealm getTradeByName(String tradeName) {
        return mRealmManager.getTradeByName(tradeName);
    }

    public PriceListRealm getPriceByName(String priceName) {
        return mRealmManager.getPriceByName(priceName);
    }

    public CurrencyRealm getCurrencyByCode(String code) {
        return mRealmManager.getCurrencyById(code);
    }

    public CurrencyRealm getCurrencyByName(String currencyName) {
        return mRealmManager.getCurrencyByName(currencyName);
    }

    public float getTradePercent(String itemId, String tradeId) {
        ItemRealm mItem = mRealmManager.getItemById(itemId);
        return (mItem == null) ? 0 : mRealmManager.getTradePercent(tradeId, (mItem.getCategory()==null)?"":mItem.getCategory().getCategoryId());
    }

    public Float getCustomerDiscount(String customerId, String itemId) {
        return mRealmManager.getCustomerDiscount(customerId, itemId);
    }


    public float getItemPrice(String itemId, String priceId, String tradeId, String currencyId, String customerId, boolean roundVAT) {
        final ItemRealm mItem = mRealmManager.getItemById(itemId);
        final PriceListItemRealm mItemPrice = mRealmManager.getPriceListItem(itemId, (priceId == null || priceId.isEmpty())?ConstantManager.PRICE_BASE_PRICE_ID:priceId);
        final CurrencyRealm mCurrency = mRealmManager.getCurrencyById((currencyId==null || currencyId.isEmpty()? ConstantManager.MAIN_CURRENCY_CODE:currencyId));
        final CurrencyRealm priceCurrency;

        if (mItem == null || mItemPrice == null || mCurrency == null ||mCurrency.getRate() == 0 ) return 0;

        final Pair<CurrencyRealm, Float> personalPrice = mRealmManager.getCustomerPrice(customerId, itemId);

        final float discount;
        final float tradePercent;
        final float rate;
        final float price;
        if (personalPrice != null && personalPrice.first !=null && personalPrice.second !=null && personalPrice.second > 0){
            priceCurrency = personalPrice.first;
            discount = 0;
            tradePercent = 0;
            rate = (priceCurrency.getCurrencyId().equals(mCurrency.getCurrencyId())) ? 1f : priceCurrency.getRate()/mCurrency.getRate();
            price = personalPrice.second;
        }else{
            priceCurrency = mItemPrice.getCurrency();
            if (priceCurrency == null || priceCurrency.getRate() == 0) return 0;
            discount = getCustomerDiscount(customerId, itemId);
            tradePercent = mRealmManager.getTradePercent(tradeId, (mItem.getCategory()==null)?"":mItem.getCategory().getCategoryId());
            rate = (priceCurrency.getCurrencyId().equals(mCurrency.getCurrencyId())) ? 1f : priceCurrency.getRate()/mCurrency.getRate();
            price = mItemPrice.getPrice();
        }

        final float itemPrice = Math.round(price*rate*(100+tradePercent-discount))/100f;

        return roundVAT ? MiscUtils.roundPrice(itemPrice) : itemPrice;
    }

    public TradeRealm getFactTradeForTrade(String tradeId) {
        return mRealmManager.getFactTradeForTrade(tradeId);
    }

    public TradeRealm getTradeById(String tradeId) {
        return mRealmManager.getTradeById(tradeId);
    }


    //endregion ===================== Prices =========================


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
        if (sync) {
            mJobManager.start();
        }else{
            mJobManager.stop();
        }
    }


    public String getLastUpdate(String module){
        return mPreferencesManager.getLastUpdate(module);
    }

    public void setLastUpdate(String module, String lastModified){
        mPreferencesManager.saveLastUpdate(module, lastModified);
    }



    //endregion ================== Preferences =========================


}

