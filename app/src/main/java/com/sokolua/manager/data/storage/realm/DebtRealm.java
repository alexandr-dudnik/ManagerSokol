package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class DebtRealm extends RealmObject implements Serializable {
    @Required
    private String customerId;
    @Required
    private String currency;
    private float amount;
    private float amountUSD;
    private boolean outdated;

    public DebtRealm() {
    }

    public DebtRealm(String customerId, String currency, float amount, float amountUSD, boolean outdated) {
        this.customerId = customerId;
        this.currency = currency;
        this.amount = amount;
        this.amountUSD = amountUSD;
        this.outdated = outdated;
    }

//region ===================== Getters =========================

    public String getCurrency() {
        return currency;
    }

    public float getAmount() {
        return amount;
    }

    public float getAmountUSD() {
        return amountUSD;
    }

    public String getCustomerId() {
        return customerId;
    }

    public boolean isOutdated() {
        return outdated;
    }

//endregion ================== Getters =========================
}
