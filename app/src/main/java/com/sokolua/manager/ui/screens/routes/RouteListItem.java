package com.sokolua.manager.ui.screens.routes;


import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.VisitRealm;

public class RouteListItem {
    private String headerText;
    private CustomerRealm customer;
    private VisitRealm visit;
    private boolean header;


    public RouteListItem(String headerText) {
        this.headerText = headerText;
        this.header = true;
    }

    public RouteListItem(VisitRealm visit) {
        this.customer = visit.getCustomer();
        this.visit = visit;
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

    public VisitRealm getVisit() {
        return visit;
    }

    public boolean isHeader() {
        return header;
    }


    //endregion ============================= Getters ==================================




}
