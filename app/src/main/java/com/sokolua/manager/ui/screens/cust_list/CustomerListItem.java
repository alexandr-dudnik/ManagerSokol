package com.sokolua.manager.ui.screens.cust_list;


import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;

import java.util.ArrayList;
import java.util.List;

class CustomerListItem {
    private String customerId;
    private String customerName;
    private int debtType;
    private String address;
    private String phone;
    private boolean header;


    public CustomerListItem(String customerId, String customerName, double debt, double debtOutdated, String address, String phone) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.debtType = debt<=0? ConstantManager.DEBT_TYPE_NO_DEBT:(debtOutdated<=0?ConstantManager.DEBT_TYPE_NORMAL:ConstantManager.DEBT_TYPE_OUTDATED);
        this.address = address;
        this.phone = phone;
        this.header = false;
    }

    public CustomerListItem(CustomerRealm customerRealm) {
        this.customerId = customerRealm.getCustomerId();
        this.customerName = customerRealm.getCustomerName();
        this.address = customerRealm.getAddress();
        this.phone = customerRealm.getPhone();
        this.header = false;

        float debtUSD = 0;
        float debtUSD_outd = 0;
        for (DebtRealm debt :customerRealm.getDebt()){
            if (debt.isOutdated()){
                debtUSD_outd += debt.getAmountUSD();
            }else{
                debtUSD += debt.getAmountUSD();
            }
        }
        this.debtType = (debtUSD+debtUSD_outd)<=0?ConstantManager.DEBT_TYPE_NO_DEBT:(debtUSD_outd<=0?ConstantManager.DEBT_TYPE_NORMAL:ConstantManager.DEBT_TYPE_OUTDATED);
    }


    public CustomerListItem(String customerName) {
        header = true;
        this.customerName = getHeader(customerName);
        this.customerId = "Header_"+getHeader(customerName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
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

    public static List<CustomerListItem> addHeaders(List<CustomerListItem> list){
        String curHeader = "";
        List<CustomerListItem> res = new ArrayList<>();
        for (CustomerListItem cust:res) {
            String tmpHeader = getHeader(cust.getCustomerName());
            if (!tmpHeader.equals(curHeader)){
                res.add(new CustomerListItem(cust.getCustomerName()));
            }
            res.add(cust);
        }
        return res;
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
