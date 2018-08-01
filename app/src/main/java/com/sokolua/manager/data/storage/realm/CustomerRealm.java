package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class CustomerRealm extends RealmObject implements Serializable{
    @PrimaryKey
    @Required
    private String customerId;
    @Required
    private String name;
    private String contactName = "";
    private String address = "";
    private String phone = "";
    private String email = "";
    private RealmList<DebtRealm> debt = new RealmList<>();
    private RealmList<NoteRealm> notes = new RealmList<>();
    private RealmList<TaskRealm> tasks = new RealmList<>();
    private RealmList<OrderPlanRealm> plan = new RealmList<>();
    private RealmList<CustomerDiscountRealm> discounts = new RealmList<>();

    public CustomerRealm() {
    }

    public CustomerRealm(String customerId, String name, String contactName, String address, String phone, String email) {
        this.customerId = customerId;
        this.name = name;
        this.contactName = contactName;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    //region ===================== Getters =========================

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
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

    public RealmList<NoteRealm> getNotes() {
        return notes;
    }

    public RealmList<TaskRealm> getTasks() {
        return tasks;
    }

    public RealmList<OrderPlanRealm> getPlan() {
        return plan;
    }

    public RealmList<CustomerDiscountRealm> getDiscounts() {
        return discounts;
    }

    //endregion ================== Getters =========================
}
