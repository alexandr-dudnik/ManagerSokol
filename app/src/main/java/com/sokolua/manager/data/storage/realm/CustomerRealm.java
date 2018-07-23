package com.sokolua.manager.data.storage.realm;

import com.sokolua.manager.data.storage.dto.DebtDto;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CustomerRealm extends RealmObject implements Serializable{
    @PrimaryKey
    private String customerId;
    private String customerName;
    private String address;
    private String phone;
    private RealmList<DebtRealm> debt = new RealmList<>();

    public CustomerRealm() {
    }

    public CustomerRealm(String customerId, String customerName, String address, String phone) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.phone = phone;
    }

    //region ===================== Getters =========================

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public RealmList<DebtRealm> getDebt() {
        return debt;
    }

    //endregion ================== Getters =========================
}
