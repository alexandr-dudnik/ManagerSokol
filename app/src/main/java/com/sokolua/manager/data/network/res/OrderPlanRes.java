package com.sokolua.manager.data.network.res;

public class OrderPlanRes {
    public String category_id;
    public float amount;

    public OrderPlanRes(String category_id, float amount) {
        this.category_id = category_id;
        this.amount = amount;
    }

    //region =======================  Getters  =========================

    public String getCategory_id() {
        return category_id;
    }

    public float getAmount() {
        return amount;
    }


    //endregion ====================  Getters  =========================

    //region =======================  Setters  =========================

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }


    //endregion ====================  Setters  =========================
}
