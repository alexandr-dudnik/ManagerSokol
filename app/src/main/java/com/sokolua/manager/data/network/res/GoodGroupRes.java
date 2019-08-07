package com.sokolua.manager.data.network.res;

import io.realm.internal.Keep;

@Keep
public class GoodGroupRes {
    private String id;
    private String name;
    private String image;
    private String parent;
    private Boolean active;

    public GoodGroupRes(String id, String name, String parent, String image, Boolean active) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.parent = parent;
        this.active = active;
    }

    //region ============================== Getters =================

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getParent() {
        return parent;
    }

    public Boolean isActive() {
        return active==null?false:active;
    }

    //endregion =========================== Getters =================
}
