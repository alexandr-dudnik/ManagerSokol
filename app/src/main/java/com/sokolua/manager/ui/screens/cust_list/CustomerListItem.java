package com.sokolua.manager.ui.screens.cust_list;


import com.sokolua.manager.data.storage.realm.CustomerRealm;

public class CustomerListItem {
    private String headerText;
    private CustomerRealm customer;
    private boolean header;


    public CustomerListItem(String headerText) {
        this.headerText = headerText;
        this.header = true;
    }

    public CustomerListItem(CustomerRealm customerRealm) {
        this.customer = customerRealm;
        this.header = false;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }


    //region ================================ Getters ==================================

    public String getHeaderText() {
        return headerText;
    }

    public CustomerRealm getCustomer() {
        return customer;
    }

    public boolean isHeader() {
        return header;
    }


    //endregion ============================= Getters ==================================




}
