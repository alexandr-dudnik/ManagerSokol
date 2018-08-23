package com.sokolua.manager.data.network.res;

public class GoodGroupRes {
    public String id;
    public String name;
    public String image;

    public GoodGroupRes(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
