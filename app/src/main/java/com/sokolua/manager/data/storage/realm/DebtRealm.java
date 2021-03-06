package com.sokolua.manager.data.storage.realm;


import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import io.realm.internal.Keep;

@Keep
public class DebtRealm extends RealmObject implements Serializable {
    @PrimaryKey
    @Required
    private String debtId;
    private CustomerRealm customer;
    @Required
    private String currency;
    private float amount = 0;
    private float amountUSD = 0;
    private boolean outdated = false;

    public DebtRealm() {
    }

    public DebtRealm(CustomerRealm customer, String currency, float amount, float amountUSD, boolean outdated) {
        this.customer = customer;
        this.currency = currency;
        this.amount = amount;
        this.amountUSD = amountUSD;
        this.outdated = outdated;
        this.debtId = customer.getCustomerId()+"#"+currency+"#"+(outdated?"!":"");
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

    public CustomerRealm getCustomer() {
        return customer;
    }

    public boolean isOutdated() {
        return outdated;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setAmountUSD(float amountUSD) {
        this.amountUSD = amountUSD;
    }

//endregion ================== Getters =========================
}
