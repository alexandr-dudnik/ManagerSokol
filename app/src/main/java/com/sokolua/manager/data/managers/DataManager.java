package com.sokolua.manager.data.managers;

import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.OrderPlanRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.components.DaggerDataManagerComponent;
import com.sokolua.manager.di.components.DataManagerComponent;
import com.sokolua.manager.di.modules.LocalModule;
import com.sokolua.manager.di.modules.NetworkModule;
import com.sokolua.manager.utils.App;

import javax.inject.Inject;

import io.reactivex.Observable;

public class DataManager {
    private static DataManager ourInstance;
    private boolean userAuth=false;

    @Inject
    PreferencesManager mPreferencesManager;

    @Inject
    RealmManager mRealmManager;


    private DataManager() {
        DataManagerComponent dmComponent = DaggerService.getComponent(DataManagerComponent.class);
        if (dmComponent==null){
            dmComponent = DaggerDataManagerComponent.builder()
                    .appComponent(App.getAppComponent())
                    .localModule(new LocalModule())
                    .networkModule(new NetworkModule())
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

    //region ===================== Getters =========================


    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    //endregion ================== Getters =========================

    
    //region ===================== UserInfo =========================

    public boolean isUserAuth() {
        //TODO check auth token in shared preferences
        // TODO: 20.06.2018 send check auth String
        return userAuth;
    }

    public void setUserAuth(boolean state){
        userAuth = state;
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



    //endregion ================== Customers =========================

    //region ===================== Orders =========================
    public Observable<OrderRealm> getCustomerOrders(String customerId) {
        return mRealmManager.getCustomerOrders(customerId);
    }

    public Observable<OrderRealm> getOrders() {
        return mRealmManager.getAllOrders();
    }
    //endregion ================== Orders =========================

}

