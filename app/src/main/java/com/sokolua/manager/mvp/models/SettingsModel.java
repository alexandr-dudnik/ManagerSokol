package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;

import io.reactivex.Observable;

public class SettingsModel extends AbstractModel {
    public String getServerAddress() {
        return mDataManager.getServerAddress();
    }

    public void updateServerAddress(String address) {
        mDataManager.updateServerAddress(address);
    }

    public Boolean getAutoSynchronize() {
        return mDataManager.getAutoSynchronize();
    }

    public void updateAutoSynchronize(Boolean sync) {
        mDataManager.updateAutoSynchronize(sync);
    }

    public Observable<GoodsGroupRealm> updateAllGroupsFromRemote(){
        return mDataManager.updateGroupsFromRemote();
    }

    public Observable<ItemRealm> updateAllGoodItemsFromRemote(){
        return mDataManager.updateItemsFromRemote();
    }

    public Observable<CustomerRealm> updateAllCustomersFromRemote() {
        return mDataManager.updateCustomersFromRemote();
    }

    public Observable<OrderRealm> updateAllOrdersFromRemote() {
        return mDataManager.updateOrdersFromRemote();
    }


    public void clearDatabase() {
        mDataManager.clearDataBase();
    }

    public Observable<Boolean> sendAllOrders() {
        return mDataManager.sendAllOrders("");
    }

    public Observable<Boolean> sendAllNotes() {
        return mDataManager.sendAllNotes("");
    }

//    public Observable<ItemRealm> updateGoodItemFromRemote(String goodId){
//        return mDataManager.updateGoodItemFromRemote(goodId);
//    }
}
