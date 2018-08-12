package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.storage.realm.OrderRealm;

import java.util.Date;

public class OrderModel extends AbstractModel {
    public void setDeliveryDate(OrderRealm currentOrder, Date mDate) {
        mDataManager.setDeliveryDate(currentOrder, mDate);
    }
}
