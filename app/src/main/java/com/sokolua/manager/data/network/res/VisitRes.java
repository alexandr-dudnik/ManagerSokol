package com.sokolua.manager.data.network.res;

import android.support.annotation.Keep;

@Keep
public class VisitRes {
    private String id;
    private String date;
    private boolean done;

    public VisitRes(String id, String date, boolean done) {
        this.id = id;
        this.date = date;
        this.done = done;
    }

    //region =======================  Getters  =========================

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public boolean isDone() {
        return done;
    }


    //endregion ====================  Getters  =========================

}
