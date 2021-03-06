package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.storage.realm.OrderRealm;

import java.util.List;

import io.reactivex.Observable;

public class OrderListModel extends AbstractModel {

    public Observable<List<OrderRealm>> getOrderList() {
        return mDataManager.getOrders();
    }

}

