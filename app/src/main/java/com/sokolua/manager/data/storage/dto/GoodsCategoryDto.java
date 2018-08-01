package com.sokolua.manager.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.sokolua.manager.data.storage.realm.GoodsCategoryRealm;

public class GoodsCategoryDto implements Parcelable {
    private String categoryId="";
    private String categoryName;
    private String imageUri;



    public GoodsCategoryDto(String categoryId, String categoryName, String imageUri) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.imageUri = imageUri;
    }

    protected GoodsCategoryDto(Parcel in) {
        this.categoryId = in.readString();
        this.categoryName = in.readString();
        this.imageUri = in.readString();
    }

    public GoodsCategoryDto(GoodsCategoryRealm cat) {
        this.categoryId = cat.getCategoryId();
        this.categoryName = cat.getCategoryName();
        this.imageUri = cat.getImageUri();
    }

    //region ===================== Parcelable =========================

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.categoryId);
        dest.writeString(this.categoryName);
        dest.writeString(this.imageUri);
    }

    public static final Parcelable.Creator<GoodsCategoryDto> CREATOR = new Parcelable.Creator<GoodsCategoryDto>() {
        @Override
        public GoodsCategoryDto createFromParcel(Parcel in) {
            return new GoodsCategoryDto(in);
        }

        @Override
        public GoodsCategoryDto[] newArray(int size) {
            return new GoodsCategoryDto[size];
        }
    };

    //endregion ================== Parcelable =========================

    //region ===================== Getters =========================

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getImageUri() {
        return imageUri;
    }


    //endregion ================== Getters =========================

    //region ===================== Setters =========================

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    //endregion ================== Setters =========================

}

