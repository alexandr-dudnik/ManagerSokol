package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class NoteRealm extends RealmObject implements Serializable {
    @PrimaryKey
    @Required
    private String noteId;
    private CustomerRealm customer ;
    @Required
    private String date;
    private String data;

    public NoteRealm() {
    }

    public NoteRealm(CustomerRealm customer, String noteId, String date, String data) {
        this.customer = customer;
        this.noteId = noteId;
        this.date = date;
        this.data = data;
    }

    //region ===================== Getters =========================

    public String getNoteId() {
        return noteId;
    }

    public String getDate() {
        return date;
    }

    public String getData() {
        return data;
    }

    public CustomerRealm getCustomer() {
        return customer;
    }

    //endregion ================== Getters =========================
}
