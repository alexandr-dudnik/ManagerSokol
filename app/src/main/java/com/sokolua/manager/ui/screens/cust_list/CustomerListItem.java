package com.sokolua.manager.ui.screens.cust_list;


import java.util.List;

class CustomerListItem {
    public static final int DEBT_NO_DEBT  = 0;
    public static final int DEBT_NORMAL   = 1;
    public static final int DEBT_OUTDATED = 2;
    private String customerId;
    private String customerName;
    private int debtType;
    private String address;
    private String phone;
    private boolean header;


    public CustomerListItem(String customerId, String customerName, double debt, double debtOutdated, String address, String phone) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.debtType = debt<=0?DEBT_NO_DEBT:(debtOutdated<=0?DEBT_NORMAL:DEBT_OUTDATED);
        this.address = address;
        this.phone = phone;
        header = false;
    }

    private CustomerListItem(String customerName) {
        header = true;
        this.customerName = getHeader(customerName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this.header){
            if (!((CustomerListItem)obj).isHeader()){
                return false;
            }else{
                return ((CustomerListItem)obj).getCustomerName().equals(this.customerName);
            }
        }else {
            return ((CustomerListItem) obj).getCustomerId().equals(this.customerId);
        }
    }

    @Override
    public String toString() {
        return this.customerName;
    }

    //region ================================ Static ==================================

    public static String getHeader(String customerName){
        return customerName.substring(0,1);
    }

    public static List<CustomerListItem> prepareList(List<CustomerListItem> list, boolean insertHeaders){
        return list;
    }

    //endregion ============================= Static ==================================

    //region ================================ Getters ==================================

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getDebtType() {
        return debtType;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isHeader() {
        return header;
    }

    //endregion ============================= Getters ==================================


//region ================================ Setters ==================================

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setDebtType(int debtType) {
        this.debtType = debtType;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


//endregion ============================= Setters ==================================






}
