package com.sokolua.manager.data.storage.realm;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Index;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

@Keep
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
    private String category = "";
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
    @LinkingObjects("customer")
    private final RealmResults<OrderRealm> orders = null;

    public CustomerRealm() {
    }

    public CustomerRealm(String customerId, @NonNull String name, String contactName, String address, String phone, String email, String category) {
        this.customerId = customerId;
        this.name = name;
        this.contactName = contactName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.category = category;
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
        return contactName==null?"":contactName;
    }

    public String getAddress() {
        return address==null?"":address;
    }

    public String getPhone() {
        return phone==null?"":phone;
    }

    public String getEmail() {
        return email==null?"":email;
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

    public RealmResults<OrderRealm> getOrders() {
        return orders;
    }

    public String getCategory() {
        return category==null?"":category;
    }

    //endregion ================== Getters =========================
}
