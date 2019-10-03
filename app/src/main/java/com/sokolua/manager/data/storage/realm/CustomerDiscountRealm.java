package com.sokolua.manager.data.storage.realm;


import com.sokolua.manager.data.managers.ConstantManager;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import io.realm.internal.Keep;

@Keep
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
        this.discountId = discountType +"#"+customer.getCustomerId()+"#"+(discountType== ConstantManager.DISCOUNT_TYPE_ITEM?item.getItemId():category.getCategoryId());
    }
    public CustomerDiscountRealm(CustomerRealm customer, GoodsCategoryRealm category, Float percent) {
        this.discountType = ConstantManager.DISCOUNT_TYPE_CATEGORY;
        this.customer = customer;
        this.category = category;
        this.item = null;
        this.percent = percent;
        this.discountId = discountType + "#" + customer.getCustomerId() + "#" + category.getCategoryId();
    }
    public CustomerDiscountRealm(CustomerRealm customer, ItemRealm item, Float percent) {
        this.discountType = ConstantManager.DISCOUNT_TYPE_ITEM;
        this.customer = customer;
        this.category = null;
        this.item = item;
        this.percent = percent;
        this.discountId = discountType + "#" + customer.getCustomerId() + "#" + item.getItemId();
    }


    //region ================================ Getters ==================================
    public String getDiscountId() {
        return discountId;
    }

    public int getDiscountType() {
        return discountType;
    }

    public CustomerRealm getCustomer() {
        return customer;
    }

    public GoodsCategoryRealm getCategory() {
        return category;
    }

    public ItemRealm getItem() {
        return item;
    }

    public Float getPercent() {
        return percent;
    }

    //endregion ============================= Getters ==================================

    //region ================================ Setters ==================================

    public void setPercent(Float percent) {
        this.percent = percent;
    }

    //endregion ============================= Setters ==================================
}
