package com.sokolua.manager.data.network.res;

public class TaskRes {
    public String id;
    public String text;
    public String type;

    public TaskRes(String id, String text, String type) {
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

    public String getType() {
        return type;
    }


    //endregion ====================  Getters  =========================

    //region =======================  Setters  =========================

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(String type) {
        this.type = type;
    }


    //endregion ====================  Setters  =========================

}
