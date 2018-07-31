package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class NoteRealm extends RealmObject implements Serializable {
    @Required
    private String customerId;
    @Required
    private String noteId;
    @Required
    private String date;
    private String data;

    public NoteRealm() {
    }

    public NoteRealm(String customerId, String noteId, String date, String data) {
        this.customerId = customerId;
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

    public String getCustomerId() {
        return customerId;
    }

    //endregion ================== Getters =========================
}
