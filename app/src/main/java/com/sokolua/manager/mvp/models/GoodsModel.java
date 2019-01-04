package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.storage.realm.BrandsRealm;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;

import java.util.List;

import io.reactivex.Observable;

public class GoodsModel extends AbstractModel {
    public Observable<List<GoodsGroupRealm>> getGroupList(String parentId, String brand) {
        return mDataManager.getGroupList(parentId, brand);
    }

    public Observable<List<ItemRealm>> getItemList(String parentId, String filter, String brand, String categoryId) {
        return mDataManager.getItemList(parentId, filter, brand, categoryId);
    }

    public void addItemToCart(String customerCartId, String itemId, float newQty, float newPrice) {
        mDataManager.addItemToCart(customerCartId, itemId, newQty, newPrice);
    }

    public OrderRealm getOrderById(String orderId) {
        return mDataManager.getOrderById(orderId);
    }

    public Float getCustomerDiscount(String customerId, String itemId) {
        return mDataManager.getCustomerDiscount(customerId, itemId);
    }

    public Observable<GoodsGroupRealm> updateAllGroupsFromRemote(){
        return mDataManager.updateGroupsFromRemote();
    }

    public Observable<ItemRealm> updateAllGoodItemsFromRemote(){
        return mDataManager.updateItemsFromRemote();
    }

    public Observable<List<BrandsRealm>> getBrands() {
        return mDataManager.getBrands();
    }

    public ItemRealm getItemById(String itemId) {
        return mDataManager.getItemById(itemId);
    }
}
