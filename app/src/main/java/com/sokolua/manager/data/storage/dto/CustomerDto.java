package com.sokolua.manager.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;

import java.util.ArrayList;

public class CustomerDto implements Parcelable {
    private String customerId;
    private String customerName;
    private String contactName;
    private String address;
    private String phone;
    private String email;
    private ArrayList<DebtDto> debt = new ArrayList<>();

    public CustomerDto(String customerId, String customerName, String contactName, String address, String phone, String email, ArrayList<DebtDto> debt) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.contactName = contactName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.debt = debt;
    }

    protected CustomerDto(Parcel in){
        this.customerId = in.readString();
        this.customerName = in.readString();
        this.contactName = in.readString();
        this.address = in.readString();
        this.phone = in.readString();
        this.email = in.readString();
        this.debt = in.createTypedArrayList(DebtDto.CREATOR);
    }

    public CustomerDto(CustomerRealm customer){
        this.customerId = customer.getCustomerId();
        this.customerName = customer.getCustomerName();
        this.contactName = customer.getContactName();
        this.address = customer.getAddress();
        this.phone = customer.getPhone();
        this.email = customer.getEmail();
        for (DebtRealm debt: customer.getDebt()) {
            this.debt.add(new DebtDto(debt));
        }
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
        dest.writeString(this.contactName);
        dest.writeString(this.address);
        dest.writeString(this.phone);
        dest.writeString(this.email);
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

    public String getContactName() {
        return contactName;
    }
    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
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

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDebt(ArrayList<DebtDto> debt) {
        this.debt = debt;
    }



    //endregion ================== Setters =========================
}
