package com.sokolua.manager.data.network.res;

import android.support.annotation.Keep;

@Keep
public class OrderPlanRes {
    private String category_id;
    private String category_name;
    private float amount;

    public OrderPlanRes(String category_id, String category_name, float amount) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.amount = amount;
    }

    //region =======================  Getters  =========================

    public String getCategoryId() {
        return category_id;
    }

    public float getAmount() {
        return amount;
    }

    public String getCategoryName() {
        return category_name;
    }

    //endregion ====================  Getters  =========================



}
