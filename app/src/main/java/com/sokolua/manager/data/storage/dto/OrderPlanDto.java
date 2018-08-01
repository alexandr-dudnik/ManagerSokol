package com.sokolua.manager.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.sokolua.manager.data.storage.realm.OrderPlanRealm;

public class OrderPlanDto implements Parcelable{
    private String customerId;
    private GoodsCategoryDto category;
    private Float amount;

    public OrderPlanDto(String customerId, GoodsCategoryDto category, Float amount) {
        this.customerId = customerId;
        this.category = category;
        this.amount = amount;
    }

    protected OrderPlanDto(Parcel in) {
        this.customerId = in.readString();
        this.category = in.readParcelable(GoodsCategoryDto.class.getClassLoader());
        this.amount = in.readFloat();
    }

    public OrderPlanDto(OrderPlanRealm plan) {
        this.customerId = plan.getCustomerId();
        this.category = new GoodsCategoryDto(plan.getGoodsCategory());
        this.amount = plan.getAmount();
    }

    //region ===================== Parcelable =========================

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.customerId);
        dest.writeParcelable(this.category, flags);
        dest.writeFloat(this.amount);
    }

    public static final Creator<OrderPlanDto> CREATOR = new Creator<OrderPlanDto>() {
        @Override
        public OrderPlanDto createFromParcel(Parcel in) {
            return new OrderPlanDto(in);
        }

        @Override
        public OrderPlanDto[] newArray(int size) {
            return new OrderPlanDto[size];
        }
    };

    //endregion ================== Parcelable =========================

    //region ===================== Getters =========================

    public String getCustomerId() {
        return customerId;
    }

    public GoodsCategoryDto getCategory() {
        return category;
    }

    public Float getAmount() {
        return amount;
    }

    //endregion ================== Getters =========================

    //region ===================== Setters =========================

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setCategory(GoodsCategoryDto category) {
        this.category = category;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    //endregion ================== Setters =========================

}
