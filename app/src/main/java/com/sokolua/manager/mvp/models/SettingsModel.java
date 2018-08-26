package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.data.storage.realm.ItemRealm;

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

//    public Observable<GoodsGroupRealm> updateGoodGroupFromRemote(String groupId){
//        return mDataManager.updateGroupFromRemote(groupId);
//    }

    public Observable<ItemRealm> updateAllGoodItemsFromRemote(){
        return mDataManager.updateItemsFromRemote();
    }

//    public Observable<ItemRealm> updateGoodItemFromRemote(String goodId){
//        return mDataManager.updateGoodItemFromRemote(goodId);
//    }
}
