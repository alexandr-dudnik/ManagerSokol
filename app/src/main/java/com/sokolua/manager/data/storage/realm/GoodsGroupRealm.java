package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmObject;

public class GoodsGroupRealm extends RealmObject implements Serializable {
    private String groupId;
    private String groupName;
    private String imageURI;


    public GoodsGroupRealm() {
    }

    public GoodsGroupRealm(String groupId, String groupName, String imageURI) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.imageURI = imageURI;
    }

//region ================================ Getters ==================================

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getImageURI() {
        return imageURI;
    }

//endregion ============================= Getters ==================================
}
