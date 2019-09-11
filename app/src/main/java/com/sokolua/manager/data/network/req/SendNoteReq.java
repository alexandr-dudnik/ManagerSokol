package com.sokolua.manager.data.network.req;

import androidx.annotation.Keep;

import com.sokolua.manager.data.storage.realm.NoteRealm;

import java.text.SimpleDateFormat;
import java.util.Locale;


@Keep
public class SendNoteReq {
    private String id;
    private String customer_id;
    private String date;
    private String text;

    public SendNoteReq(String id, String customer_id, String date, String text) {
        this.id = id;
        this.customer_id = customer_id;
        this.date = date;
        this.text = text;
    }

    public SendNoteReq(NoteRealm note) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        this.id = note.getNoteId();
        this.customer_id = note.getCustomer().getCustomerId();
        this.date = sdf.format(note.getDate());
        this.text = note.getData();
    }

    //region ===================== Getters =========================

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public String getCustomerId() {
        return customer_id;
    }

    //endregion ================== Getters =========================
}
