package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;

import java.util.List;

import io.reactivex.Observable;

public class GoodsModel extends AbstractModel {
    public Observable<List<GoodsGroupRealm>> getMainGroupsList() {
        Observable<GoodsGroupRealm> obs = mDataManager.getMainGroupsList();

        return obs.toList().toObservable();
    }
}
