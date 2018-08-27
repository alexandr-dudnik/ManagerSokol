package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

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
        this.noteId = noteId;
        this.externalId = noteId;
        this.date = date;
        this.data = data;
    }

    public NoteRealm(CustomerRealm customer, String data) {
        this.customer = customer;
        this.noteId = "note_"+String.valueOf(Math.random());
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

    //endregion ================== Getters =========================
}
