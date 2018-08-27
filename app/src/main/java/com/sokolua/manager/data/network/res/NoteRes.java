package com.sokolua.manager.data.network.res;

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


    //region =======================  Setters  =========================

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setText(String text) {
        this.text = text;
    }


    //endregion ====================  Setters  =========================
}
