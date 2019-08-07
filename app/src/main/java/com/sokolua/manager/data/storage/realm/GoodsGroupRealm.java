package com.sokolua.manager.data.storage.realm;


import androidx.annotation.Keep;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

@Keep
public class GoodsGroupRealm extends RealmObject implements Serializable {
    @PrimaryKey
    @Required
    private String groupId;
    @Required
    private String name;
    private GoodsGroupRealm parent;
    private String imageURI;
    @LinkingObjects("parent")
    private final RealmResults<GoodsGroupRealm> subGroups = null;
    @LinkingObjects("group")
    private final RealmResults<ItemRealm> items = null;



    public GoodsGroupRealm() {
    }

    public GoodsGroupRealm(String groupId, String groupName, GoodsGroupRealm parent, String imageURI) {
        this.groupId = groupId;
        this.name = groupName;
        this.imageURI = imageURI;
        this.parent = parent;
    }

    public GoodsGroupRealm(String groupId, String groupName) {
        this.groupId = groupId;
        this.name = groupName;
        this.imageURI = "";
        this.parent = null;
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
