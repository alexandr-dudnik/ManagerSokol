package com.sokolua.manager.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.sokolua.manager.data.storage.realm.DebtRealm;

public class DebtDto implements Parcelable {
    private String customerId="";
    private String currency;
    private float amount;
    private float amountUSD;
    private boolean outdated;

    public DebtDto(String currency, float amount, float amountUSD, boolean outdated) {
        this.currency = currency;
        this.amount = amount;
        this.amountUSD = amountUSD;
        this.outdated = outdated;
    }

    protected DebtDto(Parcel in) {
        this.customerId = in.readString();
        this.currency = in.readString();
        this.amount = in.readFloat();
        this.amountUSD = in.readFloat();
        this.outdated = (in.readByte()==1);
    }

    public DebtDto(DebtRealm debt) {
        this.customerId = debt.getCustomer().getCustomerId();
        this.currency = debt.getCurrency();
        this.amount = debt.getAmount();
        this.amountUSD = debt.getAmountUSD();
        this.outdated = debt.isOutdated();
    }

    //region ===================== Parcelable =========================

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.customerId);
        dest.writeString(this.currency);
        dest.writeFloat(this.amount);
        dest.writeFloat(this.amountUSD);
        dest.writeByte((byte) (this.outdated?1:0));
    }

    public static final Creator<DebtDto> CREATOR = new Creator<DebtDto>() {
        @Override
        public DebtDto createFromParcel(Parcel in) {
            return new DebtDto(in);
        }

        @Override
        public DebtDto[] newArray(int size) {
            return new DebtDto[size];
        }
    };

    //endregion ================== Parcelable =========================

    //region ===================== Getters =========================

    public String getCurrency() {
        return currency;
    }

    public float getAmount() {
        return amount;
    }

    public float getAmountUSD() {
        return amountUSD;
    }

    public String getCustomerId() {
        return customerId;
    }

    public boolean isOutdated() {
        return outdated;
    }
    //endregion ================== Getters =========================

    //region ===================== Setters =========================

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setAmountUSD(float amountUSD) {
        this.amountUSD = amountUSD;
    }

    //endregion ================== Setters =========================
}
