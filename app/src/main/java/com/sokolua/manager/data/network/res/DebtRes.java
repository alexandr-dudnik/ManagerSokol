package com.sokolua.manager.data.network.res;

import android.support.annotation.Keep;

@Keep
public class DebtRes {
    public String currency;
    public float amount;
    public float amountUSD;
    public boolean outdated;

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

    //region =======================  Setters  =========================

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setAmountUSD(float amountUSD) {
        this.amountUSD = amountUSD;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }


    //endregion ====================  Setters  =========================
}
