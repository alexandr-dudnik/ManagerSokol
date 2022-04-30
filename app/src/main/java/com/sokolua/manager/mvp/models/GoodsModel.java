package com.sokolua.manager.mvp.models;

import androidx.annotation.Nullable;

import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.BrandsRealm;
import com.sokolua.manager.data.storage.realm.CurrencyRealm;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.data.storage.realm.PriceListRealm;
import com.sokolua.manager.data.storage.realm.TradeRealm;

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

    public float getItemPrice(String itemId, String priceId, String tradeId, String currencyId, String customerId, boolean roundVAT){
        return mDataManager.getItemPrice(itemId, priceId, tradeId, currencyId, customerId, roundVAT);
    }

    public Observable<List<PriceListRealm>> getPrices() {
        return mDataManager.getPriceLists();
    }

    public PriceListRealm getPriceById(String priceId) {
        return mDataManager.getPriceById(priceId);
    }

    public Observable<List<TradeRealm>> getTrades(@Nullable Boolean fop, @Nullable Boolean cash, @Nullable Boolean fact, @Nullable Boolean remote) {
        return mDataManager.getTrades(fop, cash, fact, remote);
    }

    public Observable<List<CurrencyRealm>> getCurrencies() {
        return mDataManager.getCurrencyList();
    }

    public TradeRealm getTradeByName(String tradeName) {
        return mDataManager.getTradeByName(tradeName);
    }

    public PriceListRealm getPriceByName(String priceName) {
        return mDataManager.getPriceByName(priceName);
    }

    public CurrencyRealm getCurrencyByName(String currencyName) {
        return mDataManager.getCurrencyByName(currencyName);
    }

    public float getItemLowPrice(String itemId, String currencyId) {
        return getItemPrice(itemId, ConstantManager.PRICE_LOW_PRICE_ID, null, currencyId, null, false);
    }

    public float getTradePercent(String itemId, String tradeId) {
        return mDataManager.getTradePercent(itemId, tradeId);
    }

    public TradeRealm getTradeById(String tradeId) {
        return mDataManager.getTradeById(tradeId);
    }
}
