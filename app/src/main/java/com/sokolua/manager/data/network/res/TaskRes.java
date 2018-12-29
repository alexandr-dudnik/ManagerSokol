package com.sokolua.manager.data.network.res;

import android.support.annotation.Keep;

@Keep
public class TaskRes {
    private String id;
    private String text;
    private Integer type;

    public TaskRes(String id, String text, Integer type) {
        this.id = id;
        this.text = text;
        this.type = type;
    }

    //region =======================  Getters  =========================

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Integer getType() {
        return type;
    }


    //endregion ====================  Getters  =========================


}
