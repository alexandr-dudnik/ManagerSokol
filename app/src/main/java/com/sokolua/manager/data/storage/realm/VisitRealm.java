package com.sokolua.manager.data.storage.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class VisitRealm extends RealmObject {
    @Required
    @PrimaryKey
    private String id;
    private CustomerRealm customer;
    @Index
    private Date date;
    private boolean done;

    public VisitRealm() {
    }

    public VisitRealm(CustomerRealm customer, String id ,Date date, boolean done) {
        this.id = id;
        this.customer = customer;
        this.date = date;
        this.done = done;
    }

    //region ===================== Getters =========================

    public String getId() {
        return id;
    }

    public CustomerRealm getCustomer() {
        return customer;
    }

    public Date getDate() {
        return date;
    }

    public boolean isDone() {
        return done;
    }

    //endregion ================== Getters =========================

    //region ===================== Setters =========================

    public void setDone(boolean done) {
        this.done = done;
    }

    //endregion ================== Setters =========================

}
