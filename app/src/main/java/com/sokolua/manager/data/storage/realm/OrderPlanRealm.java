package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmObject;

public class OrderPlanRealm extends RealmObject implements Serializable{
    private String customerId;
    private GoodsCategoryRealm goodsCategory;
    private Float amount;

    public OrderPlanRealm() {
    }

    public OrderPlanRealm(String customerId, GoodsCategoryRealm goodsCategory, Float amount) {
        this.customerId = customerId;
        this.goodsCategory = goodsCategory;
        this.amount = amount;
    }

    //region ================================ Getter ==================================

    public String getCustomerId() {
        return customerId;
    }

    public GoodsCategoryRealm getGoodsCategory() {
        return goodsCategory;
    }

    public Float getAmount() {
        return amount;
    }


    //endregion ============================= Getter ==================================
}
