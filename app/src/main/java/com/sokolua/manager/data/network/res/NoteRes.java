package com.sokolua.manager.data.network.res;

import android.support.annotation.Keep;

@Keep
public class NoteRes {
    private String id;
    private String date;
    private String text;

    public NoteRes(String id, String date, String text) {
        this.id = id;
        this.date = date;
        this.text = text;
    }

    //region =======================  Getters  =========================

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }


    //endregion ====================  Getters  =========================

}
