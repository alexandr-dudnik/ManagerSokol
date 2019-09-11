package com.sokolua.manager.data.storage.realm;


import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import io.realm.internal.Keep;

@Keep
public class PriceListItemRealm extends RealmObject implements Serializable {
    @Required
    @PrimaryKey
    private String priceItemId;
    private ItemRealm item;
    private PriceListRealm priceList;
    private CurrencyRealm currency;
    private float price;

    public PriceListItemRealm() {
    }

    public PriceListItemRealm(ItemRealm item, PriceListRealm priceList, CurrencyRealm currency, float price) {
        this.priceItemId = item.getItemId()+"#"+priceList.getPriceId();
        this.item = item;
        this.priceList = priceList;
        this.currency = currency;
        this.price = price;
    }

    //region ================================ Getters ==================================

    public ItemRealm getItem() {
        return item;
    }

    public PriceListRealm getPriceList() {
        return priceList;
    }

    public CurrencyRealm getCurrency() {
        return currency;
    }

    public float getPrice() {
        return price;
    }


    //endregion ============================= Getters ==================================



    //region ================================ Setters ==================================


    //endregion ============================= Setters ==================================

}
