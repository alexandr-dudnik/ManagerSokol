package com.sokolua.manager.data.storage.realm;

import androidx.annotation.Keep;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

@Keep
public class GoodsCategoryRealm extends RealmObject implements Serializable {
    @PrimaryKey
    @Required
    private String categoryId;
    @Required
    private String name;
    private String imageUri;

    public GoodsCategoryRealm() {
    }

    public GoodsCategoryRealm(String categoryId, String categoryName, String imageUri) {
        this.categoryId = categoryId;
        this.name = categoryName;
        this.imageUri = imageUri;
    }

    //region ================================ Getter ==================================

    public String getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getImageUri() {
        return imageUri;
    }

    //endregion ============================= Getter ==================================

}
