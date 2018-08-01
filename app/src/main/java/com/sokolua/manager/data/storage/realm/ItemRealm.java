package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ItemRealm extends RealmObject implements Serializable{
    @PrimaryKey
    @Required
    private String itemId;
    @Required
    @Index
    private String name;
    @Required
    @Index
    private String artNumber;
    private Float basePrice = 0f;
    private Float lowPrice = 0f;
    private Float restStore = 0f;
    private Float restDistr = 0f;
    private Float restOficial = 0f;
    private GoodsCategoryRealm category;
    private GoodsGroupRealm group;
    private BrandsRealm brand;

    public ItemRealm() {
    }

    public ItemRealm(String itemId, String name, String artNumber) {
        this.itemId = itemId;
        this.name = name;
        this.artNumber = artNumber;
    }
}
