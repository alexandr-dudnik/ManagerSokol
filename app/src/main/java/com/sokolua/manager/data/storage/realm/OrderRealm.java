package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class OrderRealm extends RealmObject implements Serializable{
    @Required
    @PrimaryKey
    private String orderId;
    @Required
    private Date date;
    private CustomerRealm customer;
    private int status;
    private int payment;
    private RealmList<OrderLineRealm> lines = new RealmList<>();
    private Float total;
    private String comments;

    public OrderRealm() {
    }

    public OrderRealm(Date date, int status, int payment, Float total, String comments) {
        this.date = date;
        this.status = status;
        this.payment = payment;
        this.total = total;
        this.comments = comments;
    }

    //region ================================ Getters ==================================

    public Date getDate() {
        return date;
    }

    public int getStatus() {
        return status;
    }

    public int getPayment() {
        return payment;
    }

    public Float getTotal() {
        return total;
    }

    public String getComments() {
        return comments;
    }


    //endregion ============================= Getters ==================================
}
