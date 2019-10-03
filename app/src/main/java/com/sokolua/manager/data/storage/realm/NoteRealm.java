package com.sokolua.manager.data.storage.realm;



import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import io.realm.internal.Keep;

@Keep
public class NoteRealm extends RealmObject implements Serializable {
    @PrimaryKey
    @Required
    private String noteId;
    private String externalId;
    private CustomerRealm customer ;
    @Required
    private Date date;
    private String data;

    public NoteRealm() {
    }

    public NoteRealm(CustomerRealm customer, String noteId, Date date, String data) {
        this.customer = customer;
        this.noteId = customer.getCustomerId()+"#"+noteId;
        this.externalId = customer.getCustomerId()+"#"+noteId;
        this.date = date;
        this.data = data;
    }

    public NoteRealm(CustomerRealm customer, String data) {
        this.customer = customer;
        this.noteId = UUID.randomUUID().toString();
        this.externalId = "";
        this.date = Calendar.getInstance().getTime();
        this.data = data;
    }

    //region ===================== Getters =========================

    public String getNoteId() {
        return noteId;
    }

    public Date getDate() {
        return date;
    }

    public String getData() {
        return data;
    }

    public CustomerRealm getCustomer() {
        return customer;
    }

    public String getExternalId() {
        return externalId;
    }
    //endregion ================== Getters =========================


    //region ===================== Setters =========================

    public void setExternalId(String externalId) {
        this.externalId = customer.getCustomerId()+"#"+externalId;
    }

    //endregion ================== Setters =========================
}
