package com.sokolua.manager.data.storage.realm;


import androidx.annotation.Keep;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Index;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

@Keep
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
    private Float restStore = 0f;
    private Float restDistr = 0f;
    private Float restOfficial = 0f;
    private GoodsCategoryRealm category;
    private GoodsGroupRealm group;
    private BrandsRealm brand;
    @Index
    private String index;
    @LinkingObjects("item")
    private final RealmResults<PriceListItemRealm> priceList = null;

    public ItemRealm() {
    }

    public ItemRealm(String itemId, String name, String artNumber, Float restStore, Float restDistr, Float restOfficial, GoodsCategoryRealm category, GoodsGroupRealm group, BrandsRealm brand) {
        this.itemId = itemId;
        this.name = name;
        this.artNumber = artNumber;
        this.restStore = restStore;
        this.restDistr = restDistr;
        this.restOfficial = restOfficial;
        this.category = category;
        this.group = group;
        this.brand = brand;
        this.index = name.toLowerCase()+"#"+artNumber.toLowerCase();
    }

    public ItemRealm(String itemId, String itemName, String artNumber, GoodsGroupRealm parent) {
        this.itemId = itemId;
        this.name = itemName;
        this.artNumber = artNumber;
        this.restStore = 0f;
        this.restDistr = 0f;
        this.restOfficial = 0f;
        this.category = null;
        this.group = parent;
        this.brand = null;
        this.index = name.toLowerCase()+"#"+artNumber.toLowerCase();
    }

    //region ===================== Getters =========================

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public String getArtNumber() {
        return artNumber;
    }

    public Float getRestStore() {
        return restStore;
    }

    public Float getRestDistr() {
        return restDistr;
    }

    public Float getRestOfficial() {
        return restOfficial;
    }

    public GoodsCategoryRealm getCategory() {
        return category;
    }

    public GoodsGroupRealm getGroup() {
        return group;
    }

    public BrandsRealm getBrand() {
        return brand;
    }

    //endregion ================== Getters =========================


    //region =======================  Setters  =========================

    public void setGroup(GoodsGroupRealm group){
        this.group = group;
    }

    //endregion ====================  Setters  =========================
}
