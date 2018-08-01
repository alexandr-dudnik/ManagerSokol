package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmObject;

public class BrandsRealm extends RealmObject implements Serializable{
    private String brandId;
    private String brandName;
    private String imageURI;

    public BrandsRealm() {
    }

    public BrandsRealm(String brandId, String brandName, String imageURI) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.imageURI = imageURI;
    }


//region ================================ Getters ==================================

    public String getBrandId() {
        return brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getImageURI() {
        return imageURI;
    }

    //endregion ============================= Getters ==================================

}
