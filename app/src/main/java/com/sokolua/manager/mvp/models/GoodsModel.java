package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;

import java.util.List;

import io.reactivex.Observable;

public class GoodsModel extends AbstractModel {
    public Observable<List<GoodsGroupRealm>> getGroupList(GoodsGroupRealm parent) {
        Observable<GoodsGroupRealm> obs = mDataManager.getGroupList(parent);

        return obs.toList().toObservable();
    }

    public Observable<List<ItemRealm>> getItemList(GoodsGroupRealm parent, String filter) {
        Observable<ItemRealm> obs = mDataManager.getItemList(parent, filter);

        return obs.toList().toObservable();
    }

    public void addItemToCart(OrderRealm customerCart, ItemRealm item, float newQty, float newPrice) {
        mDataManager.addItemToCart(customerCart, item, newQty, newPrice);
    }

    public OrderRealm getOrderById(String orderId) {
        return mDataManager.getOrderById(orderId);
    }

    public Float getCustomerDiscount(CustomerRealm mCustomer, ItemRealm item) {
        return mDataManager.getCustomerDiscount(mCustomer, item);
    }
}
