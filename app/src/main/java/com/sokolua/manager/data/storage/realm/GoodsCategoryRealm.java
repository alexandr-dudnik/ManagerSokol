package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmObject;

public class GoodsCategoryRealm extends RealmObject implements Serializable {
    private String categoryId;
    private String categoryName;
    private String imageUri;

    public GoodsCategoryRealm() {
    }

    public GoodsCategoryRealm(String categoryId, String categoryName, String imageUri) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.imageUri = imageUri;
    }

    //region ================================ Getter ==================================

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getImageUri() {
        return imageUri;
    }

    //endregion ============================= Getter ==================================

}
