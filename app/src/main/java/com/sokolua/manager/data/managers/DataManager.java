package com.sokolua.manager.data.managers;

import com.sokolua.manager.data.network.RestService;
import com.sokolua.manager.data.network.error.AccessDenied;
import com.sokolua.manager.data.network.error.AccessError;
import com.sokolua.manager.data.network.error.ApiError;
import com.sokolua.manager.data.network.req.UserLoginReq;
import com.sokolua.manager.data.network.res.UserRes;
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
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.NetworkStatusChecker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Observable;
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

        //updateLocalDataWithTimer();
    }

    public static DataManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new DataManager();
        }
        return ourInstance;
    }

    public void clearDataBase() {
        mRealmManager.clearDataBase();
    }

    //region ===================== Getters =========================


    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public RestService getRestservice() {
        return mRestService;
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
        return mRestService.loginUser(new UserLoginReq(userName, password))
                .flatMap(userResResponse -> {
                    switch (userResResponse.code()) {
                        case 200:
                            updateUserName(userName);
                            updateUserPassword(password);
                            return Observable.just(userResResponse.body());
                        case 401:
                            return Observable.error(new AccessError());
                        case 403:
                            return Observable.error(new AccessDenied());
                        default:
                            return Observable.error(new ApiError(userResResponse.code()));

                    }
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
    public Observable<CustomerRealm> getCustomersFromRealm(String filter) {
        return mRealmManager.getCustomersFromRealm(filter);
    }

    public CustomerRealm getCustomerById(String id){
        return mRealmManager.getCustomerById(id);
    }

    public int getCustomerDebtType(String id){
        return mRealmManager.getCustomerDebtType(id);
    }

    public Observable<NoteRealm> getCustomerNotes(String customerId) {
        return mRealmManager.getCustomerNotes(customerId);
    }

    public Observable<DebtRealm> getCustomerDebt(String customerId) {
        return mRealmManager.getCustomerDebt(customerId);
    }

    public Observable<TaskRealm> getCustomerTasks(String customerId) {
        return mRealmManager.getCustomerTasks(customerId);
    }

    public Observable<DebtRealm> getCustomerDebtByType(String customerId, int debtType) {
        return mRealmManager.getCustomerDebtByType(customerId, debtType);
    }

    public Observable<TaskRealm> getCustomerTasksByType(String customerId, int taskType) {
        return mRealmManager.getCustomerTaskByType(customerId, taskType);
    }

    public Observable<OrderPlanRealm> getCustomerPlan(String customerId) {
        return mRealmManager.getCustomerPlan(customerId);
    }


    public void updateCustomerTask(String taskId, boolean checked, String result) {
        mRealmManager.updateCustomerTask(taskId, checked, result);
    }

    public Observable<CustomerRealm> getCustomersByVisitDate(Date day) {
        return mRealmManager.getCustomersByVisitDate(day);
    }

    public void addNewNote(CustomerRealm customer, String note) {
        mRealmManager.addNewNote(customer, note);
    }

    public void deleteNote(NoteRealm note) {
        mRealmManager.deleteNote(note);
    }



    //endregion ================== Customers =========================


    //region ===================== Orders =========================
    public Observable<OrderRealm> getCustomerOrders(String customerId) {
        return mRealmManager.getCustomerOrders(customerId);
    }

    public Observable<OrderRealm> getOrders() {
        return mRealmManager.getAllOrders();
    }


    public void setDeliveryDate(OrderRealm currentOrder, Date mDate) {
        mRealmManager.setDeliveryDate(currentOrder, mDate);
    }

    public void updateOrderItemPrice(OrderRealm order, ItemRealm item, Float value) {
        mRealmManager.updateOrderItemPrice(order, item, value);
    }

    public void updateOrderItemQty(OrderRealm order, ItemRealm item, Float value) {
        mRealmManager.updateOrderItemQty(order, item, value);
    }

    public void removeOrderItem(OrderRealm order, ItemRealm item) {
        mRealmManager.removeOrderItem(order, item);
    }

    public Observable<OrderLineRealm> getOrderLines(OrderRealm order) {
        return mRealmManager.getOrderLinesList(order);
    }

    public void updateOrderStatus(OrderRealm order, int orderStatus) {
        mRealmManager.updateOrderStatus(order, orderStatus);
    }

    public void clearOrderLines(OrderRealm order) {
        mRealmManager.clearOrderLines(order);
    }

    public OrderRealm getCartForCustomer(CustomerRealm customer) {
        return mRealmManager.getCartForCustomer(customer) ;
    }

    public void updateOrderComment(OrderRealm order, String comment) {
        mRealmManager.updateOrderComment(order, comment);
    }

    public void updateOrderPayment(OrderRealm order, int payment) {
        mRealmManager.updateOrderPayment(order, payment);
    }

    public void addItemToCart(OrderRealm customerCart, ItemRealm item, float newQty, float newPrice) {
        mRealmManager.addItemToCart(customerCart, item, newQty, newPrice);
    }

    public OrderRealm getOrderById(String orderId) {
        return mRealmManager.getOrderById(orderId);
    }

    //endregion ================== Orders =========================


    //region ===================== Goods =========================
    public Observable<GoodsGroupRealm> getGroupList(GoodsGroupRealm parent) {
        return mRealmManager.getGroupList(parent);
    }

    public Observable<ItemRealm> getItemList(GoodsGroupRealm parent, String filter) {
        return mRealmManager.getItemList(parent, filter);
    }


    //endregion ================== Goods =========================


    //region ===================== Preferences =========================

    public String getServerAddress() {
        return mPreferencesManager.getServerAddress();
    }

    public void updateServerAddress(String address) {
        mPreferencesManager.updateServerAddress(address);
    }


    public Boolean getAutoSynchronize() {
        return mPreferencesManager.getAutoSynchronize();
    }

    public void updateAutoSynchronize(Boolean sync) {
        mPreferencesManager.updateAutoSynchronize(sync);
    }


    //endregion ================== Preferences =========================

}

