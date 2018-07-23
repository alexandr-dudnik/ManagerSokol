package com.sokolua.manager.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CustomerDto implements Parcelable {
    private String customerId;
    private String customerName;
    private String address;
    private String phone;
    private ArrayList<DebtDto> debt;

    public CustomerDto(String customerId, String customerName, String address, String phone, ArrayList<DebtDto> debt) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.phone = phone;
        this.debt = debt;
    }

    protected CustomerDto(Parcel in){
        this.customerId = in.readString();
        this.customerName = in.readString();
        this.address = in.readString();
        this.phone = in.readString();
        this.debt = in.createTypedArrayList(DebtDto.CREATOR);
    }

    //region ===================== Parcelable =========================

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.customerId);
        dest.writeString(this.customerName);
        dest.writeString(this.address);
        dest.writeString(this.phone);
        dest.writeTypedList(this.debt);
    }

    public static final Creator<CustomerDto> CREATOR = new Creator<CustomerDto>() {
        @Override
        public CustomerDto createFromParcel(Parcel in) {
            return new CustomerDto(in);
        }

        @Override
        public CustomerDto[] newArray(int size) {
            return new CustomerDto[size];
        }
    };

    //endregion ================== Parcelable =========================

    //region ===================== Getters =========================

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public ArrayList<DebtDto> getDebt() {
        return debt;
    }

    //endregion ================== Getters =========================


    //region ===================== Setters =========================

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDebt(ArrayList<DebtDto> debt) {
        this.debt = debt;
    }

    //endregion ================== Setters =========================
}
