package com.sokolua.manager.data.network.res;

public class OrderPlanRes {
    public String category_id;
    public String category_name;
    public float amount;

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



    //region =======================  Setters  =========================

    public void setCategoryId(String category_id) {
        this.category_id = category_id;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }


    //endregion ====================  Setters  =========================
}
