package com.sokolua.manager.data.network.res;

import android.support.annotation.Keep;

@Keep
public class DebtRes {
    private String currency;
    private float amount;
    private float amountUSD;
    private boolean outdated;

    public DebtRes(String currency, float amount, float amountUSD, boolean outdated) {
        this.currency = currency;
        this.amount = amount;
        this.amountUSD = amountUSD;
        this.outdated = outdated;
    }

    //region =======================  Getters  =========================

    public String getCurrency() {
        return currency;
    }

    public float getAmount() {
        return amount;
    }

    public float getAmountUSD() {
        return amountUSD;
    }

    public boolean isOutdated() {
        return outdated;
    }


    //endregion ====================  Getters  =========================
}
