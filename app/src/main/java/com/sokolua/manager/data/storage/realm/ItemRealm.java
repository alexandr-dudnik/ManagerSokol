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
    private Float restOfficial = 0f;
    private GoodsCategoryRealm category;
    private GoodsGroupRealm group;
    private BrandsRealm brand;
    @Index
    private String index;

    public ItemRealm() {
    }

    public ItemRealm(String itemId, String name, String artNumber, Float basePrice, Float lowPrice, Float restStore, Float restDistr, Float restOfficial, GoodsCategoryRealm category, GoodsGroupRealm group, BrandsRealm brand) {
        this.itemId = itemId;
        this.name = name;
        this.artNumber = artNumber;
        this.basePrice = basePrice;
        this.lowPrice = lowPrice;
        this.restStore = restStore;
        this.restDistr = restDistr;
        this.restOfficial = restOfficial;
        this.category = category;
        this.group = group;
        this.brand = brand;
        this.index = name.toLowerCase()+"#"+artNumber.toLowerCase()+"#"+brand.getName().toLowerCase();
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

    public Float getBasePrice() {
        return basePrice;
    }

    public Float getLowPrice() {
        return lowPrice;
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
}
