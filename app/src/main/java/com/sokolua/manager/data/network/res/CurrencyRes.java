package com.sokolua.manager.data.network.res;


import androidx.annotation.Keep;

@Keep
public class CurrencyRes {
    private String id;
    private String currency;
    private float rate;

    public CurrencyRes(String id, String currency, float rate) {
        this.id = id;
        this.currency = currency;
        this.rate = rate;
    }

    //region =======================  Getters  =========================
    public String getCurrency() {
        return currency;
    }

    public float getRate() {
        return rate;
    }

    public String getId() {
        return id;
    }

    //endregion =======================  Getters  =========================
}
