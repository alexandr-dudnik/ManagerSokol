package com.sokolua.manager.data.storage.realm;

import androidx.annotation.Keep;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Required;

@Keep
public class CustomerPhoneRealm extends RealmObject implements Serializable {
    private CustomerRealm customer;
    @Required
    private String phoneNumber;

    public CustomerPhoneRealm() {
    }

    public CustomerPhoneRealm(CustomerRealm customer, String phoneNumber) {
        this.customer = customer;
        this.phoneNumber = phoneNumber;
    }

    //region =======================  Getters  =========================

    public CustomerRealm getCustomer() {
        return customer;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    //endregion ====================  Getters  =========================

    //region =======================  Setters  =========================

    public void setCustomer(CustomerRealm customer) {
        this.customer = customer;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    //endregion ====================  Setters  =========================
}
