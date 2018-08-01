package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import javax.annotation.Nullable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class CustomerDiscountRealm extends RealmObject implements Serializable{
    @PrimaryKey
    @Required
    private String discountId;
    private int discountType;
    private CustomerRealm customer;
    private GoodsCategoryRealm category;
    private ItemRealm item;
    private Float percent = 0f;

    public CustomerDiscountRealm() {
    }

    public CustomerDiscountRealm(int discountType, CustomerRealm customer, GoodsCategoryRealm category, ItemRealm item, Float percent) {
        this.discountType = discountType;
        this.customer = customer;
        this.category = category;
        this.item = item;
        this.percent = percent;
    }
}
