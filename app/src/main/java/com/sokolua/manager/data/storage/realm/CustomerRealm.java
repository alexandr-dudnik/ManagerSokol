package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CustomerRealm extends RealmObject implements Serializable{
    @PrimaryKey
    private String customerId;
    private String customerName;
    private String contactName;
    private String address;
    private String phone;
    private String email;
    private RealmList<DebtRealm> debt = new RealmList<>();

    public CustomerRealm() {
    }

    public CustomerRealm(String customerId, String customerName, String contactName, String address, String phone, String email) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.contactName = contactName;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    //region ===================== Getters =========================

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getContactName() {
        return contactName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public RealmList<DebtRealm> getDebt() {
        return debt;
    }

    //endregion ================== Getters =========================
}
