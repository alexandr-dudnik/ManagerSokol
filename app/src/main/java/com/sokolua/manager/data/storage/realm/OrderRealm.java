package com.sokolua.manager.data.storage.realm;

import com.sokolua.manager.data.managers.ConstantManager;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class OrderRealm extends RealmObject implements Serializable{
    @Required
    @PrimaryKey
    private String Id;
    @Required
    private Date date;
    private Date delivery;
    private CustomerRealm customer;
    private int status;
    private int payment;
    private RealmList<OrderLineRealm> lines = new RealmList<>();
    private String currency;
    private Float total;
    private String comments;

    public OrderRealm() {
    }

    public OrderRealm(String id, CustomerRealm customer, Date date, Date delivery, int status, int payment, Float total, String currency, String comments) {
        this.Id = id;
        this.customer = customer;
        this.date = date;
        this.delivery = delivery;
        this.status = status;
        this.payment = payment;
        this.total = total;
        this.currency = currency;
        this.comments = comments;
    }

    //region ================================ Getters ==================================

    public Date getDate() {
        if (this.status == ConstantManager.ORDER_STATUS_CART){
            return Calendar.getInstance().getTime();
        }
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

    public String getId() {
        return Id;
    }

    public Date getDelivery() {
        return delivery;
    }

    public String getCurrency() {
        return currency;
    }

    public CustomerRealm getCustomer() {
        return customer;
    }

    public RealmList<OrderLineRealm> getLines() {
        return lines;
    }

    //endregion ============================= Getters ==================================
}
