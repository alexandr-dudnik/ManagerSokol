package com.sokolua.manager.data.network.res;

import android.support.annotation.Keep;

@Keep
public class CategoryRes {
    private String id;
    private String name;

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
