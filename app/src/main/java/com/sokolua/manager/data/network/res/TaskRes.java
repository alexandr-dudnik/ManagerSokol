package com.sokolua.manager.data.network.res;

public class TaskRes {
    public String id;
    public String text;
    public Integer type;

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

    //region =======================  Setters  =========================

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(Integer type) {
        this.type = type;
    }


    //endregion ====================  Setters  =========================

}
