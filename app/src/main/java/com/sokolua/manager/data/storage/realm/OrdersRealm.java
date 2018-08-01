package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

public class OrdersRealm extends RealmObject implements Serializable{
    private Date date;
    private int status;
    private int payment;
    private Float total;
    private String comments;

    public OrdersRealm() {
    }

    public OrdersRealm(Date date, int status, int payment, Float total, String comments) {
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
