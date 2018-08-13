package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class OrderPlanRealm extends RealmObject implements Serializable{
    @PrimaryKey
    @Required
    private String planId;
    private CustomerRealm customer;
    private GoodsCategoryRealm category;
    private Float amount = 0f;

    public OrderPlanRealm() {
    }

    public OrderPlanRealm(CustomerRealm customer, GoodsCategoryRealm category, Float amount) {
        this.customer = customer;
        this.category = category;
        this.amount = amount;
        this.planId = customer.getCustomerId()+"#"+category.getCategoryId();
    }

    //region ================================ Getter ==================================

    public CustomerRealm getCustomer() {
        return customer;
    }

    public GoodsCategoryRealm getCategory() {
        return category;
    }

    public Float getAmount() {
        return amount;
    }


    //endregion ============================= Getter ==================================
}
