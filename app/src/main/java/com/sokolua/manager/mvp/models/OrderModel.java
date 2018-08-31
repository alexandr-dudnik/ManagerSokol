package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;

public class OrderModel extends AbstractModel {
    public void setDeliveryDate(OrderRealm currentOrder, Date mDate) {
        mDataManager.setDeliveryDate(currentOrder, mDate);
    }

    public void updateOrderItemPrice(OrderRealm order, ItemRealm item, Float value) {
        mDataManager.updateOrderItemPrice(order, item, value);
    }

    public void updateOrderItemQty(OrderRealm order, ItemRealm item, Float value) {
        mDataManager.updateOrderItemQty(order, item, value);
    }

    public void removeOrderItem(OrderRealm order, ItemRealm item) {
        mDataManager.removeOrderItem(order, item);
    }

    public Observable<List<OrderLineRealm>> getLinesList(OrderRealm order) {
        Observable<OrderLineRealm> obs = mDataManager.getOrderLines(order);
        return obs.toList().toObservable();
    }

    public boolean sendOrder(OrderRealm order) {
        mDataManager.updateOrderStatus(order, ConstantManager.ORDER_STATUS_IN_PROGRESS);
        return false;
    }

    public void clearOrderLines(OrderRealm order) {
        mDataManager.clearOrderLines(order);
    }

    public void updateOrderComment(OrderRealm order, String comment) {
        mDataManager.updateOrderComment(order, comment);
    }

    public void updateOrderPayment(OrderRealm order, int payment) {
        mDataManager.updateOrderPayment(order, payment);
    }

    public void updateOrderFromRemote(String orderId) {
        mDataManager.updateOrderFromRemote(orderId);
    }
}
