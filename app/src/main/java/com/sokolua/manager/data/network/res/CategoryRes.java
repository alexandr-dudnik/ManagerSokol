package com.sokolua.manager.data.network.res;

public class CategoryRes {
    public String id;
    public String name;

    public CategoryRes(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
