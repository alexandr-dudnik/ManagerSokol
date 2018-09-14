package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class GoodsGroupRealm extends RealmObject implements Serializable {
    @PrimaryKey
    @Required
    private String groupId;
    @Required
    private String name;
    private GoodsGroupRealm parent;
    private String imageURI;


    public GoodsGroupRealm() {
    }

    public GoodsGroupRealm(String groupId, String groupName, GoodsGroupRealm parent, String imageURI) {
        this.groupId = groupId;
        this.name = groupName;
        this.imageURI = imageURI;
        this.parent = parent;
    }


//region ================================ Getters ==================================

    public String getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getImageURI() {
        return imageURI;
    }

    public GoodsGroupRealm getParent() {
        return parent;
    }

    //endregion ============================= Getters ==================================
}
