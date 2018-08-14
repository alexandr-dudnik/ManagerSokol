package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Index;
import io.realm.annotations.LinkingObjects;
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
    @Index
    private String index = "";
    @LinkingObjects("customer")
    private final RealmResults<DebtRealm> debt = null;
    @LinkingObjects("customer")
    private final RealmResults<NoteRealm> notes = null;
    @LinkingObjects("customer")
    private final RealmResults<TaskRealm> tasks = null;
    @LinkingObjects("customer")
    private final RealmResults<OrderPlanRealm> plan = null;
    @LinkingObjects("customer")
    private final RealmResults<CustomerDiscountRealm> discounts = null;
    @LinkingObjects("customer")
    private final RealmResults<VisitRealm> visits = null;

    public CustomerRealm() {
    }

    public CustomerRealm(String customerId, String name, String contactName, String address, String phone, String email) {
        this.customerId = customerId;
        this.name = name;
        this.contactName = contactName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.index = name.toLowerCase();
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

    public RealmResults<DebtRealm> getDebt() {
        return debt;
    }

    public RealmResults<NoteRealm> getNotes() {
        return notes;
    }

    public RealmResults<TaskRealm> getTasks() {
        return tasks;
    }

    public RealmResults<OrderPlanRealm> getPlan() {
        return plan;
    }

    public RealmResults<CustomerDiscountRealm> getDiscounts() {
        return discounts;
    }

    public RealmResults<VisitRealm> getVisits() {
        return visits;
    }

    //endregion ================== Getters =========================
}
