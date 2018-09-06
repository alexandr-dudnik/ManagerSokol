package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.storage.realm.OrderRealm;

import java.util.List;

import io.reactivex.Observable;
import io.realm.RealmResults;

public class OrderListModel extends AbstractModel {

    public Observable<List<OrderRealm>> getOrderList() {
        Observable<OrderRealm> obs = mDataManager.getOrders();
        return obs.toList().toObservable();
    }

    public RealmResults<OrderRealm> getOrdersQuery() {
        return mDataManager.getOrdersQuery();
    }
}

