package com.sokolua.manager.data.storage.realm;

import android.support.annotation.Keep;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

@Keep

public class BrandsRealm extends RealmObject implements Serializable{
    @PrimaryKey
    @Required
    private String brandId;
    @Required
    private String name;
    private String imageURI;

    public BrandsRealm() {
    }

    public BrandsRealm(String brandId, String brandName, String imageURI) {
        this.brandId = brandId;
        this.name = brandName;
        this.imageURI = imageURI;
    }


    //region ================================ Getters ==================================

    public String getBrandId() {
        return brandId;
    }

    public String getName() {
        return name;
    }

    public String getImageURI() {
        return imageURI;
    }

    //endregion ============================= Getters ==================================

}
