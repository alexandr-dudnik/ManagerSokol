package com.sokolua.manager.ui.screens.customer.info;

import java.util.Date;

public class CustomerInfoNoteItem {
    private String noteId;
    private String date;
    private String data;

    public CustomerInfoNoteItem(String noteId, String date, String data) {
        this.noteId = noteId;
        this.date = date;
        this.data = data;
    }


    public String getNoteId() {
        return noteId;
    }

    public String getDate() {
        return date;
    }

    public String getData() {
        return data;
    }
}
