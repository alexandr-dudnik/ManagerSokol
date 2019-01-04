package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class OrderModel extends AbstractModel {
    public void setDeliveryDate(String currentOrderId, Date mDate) {
        mDataManager.setDeliveryDate(currentOrderId, mDate);
    }

    public void updateOrderItemPrice(String order, String item, Float value) {
        mDataManager.updateOrderItemPrice(order, item, value);
    }

    public void updateOrderItemQty(String order, String item, Float value) {
        mDataManager.updateOrderItemQty(order, item, value);
    }

    public void removeOrderItem(String order, String item) {
        mDataManager.removeOrderItem(order, item);
    }

    public Observable<List<OrderLineRealm>> getLinesList(String order) {
        return mDataManager.getOrderLines(order);
    }

    public boolean sendOrder(String order) {
        mDataManager.updateOrderStatus(order, ConstantManager.ORDER_STATUS_IN_PROGRESS);
        return false;
    }

    public void clearOrderLines(String order) {
        mDataManager.clearOrderLines(order);
    }

    public void updateOrderComment(String order, String comment) {
        mDataManager.updateOrderComment(order, comment);
    }

    public void updateOrderPayment(String order, int payment) {
        mDataManager.updateOrderPayment(order, payment);
    }

    public void updateOrderFromRemote(String orderId) {
        Observable.just(orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(id -> mDataManager.updateOrderFromRemote(orderId))
                .subscribe();
    }

    public OrderRealm getOrderById(String orderId) {
        return mDataManager.getOrderById(orderId);
    }
}
