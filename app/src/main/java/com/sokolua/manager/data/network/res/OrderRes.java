package com.sokolua.manager.data.network.res;

import android.support.annotation.Keep;

import java.util.List;

@Keep
public class OrderRes {
    private String id;
    private String date;
    private String delivery;
    private String customer_id;
    private String delivered;
    private String payment;
    private List<OrderLineRes> lines;
    private String currency;
    private String comments;
    private Boolean active;


    public OrderRes(String id, String date, String delivery, String customer_id, String delivered, String payment, List<OrderLineRes> lines, String currency, String comments, Boolean active) {
        this.id = id;
        this.date = date;
        this.delivery = delivery;
        this.customer_id = customer_id;
        this.delivered = delivered;
        this.payment = payment;
        this.lines = lines;
        this.currency = currency;
        this.comments = comments;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getDelivery() {
        return delivery;
    }

    public String getCustomerId() {
        return customer_id;
    }

    public String getDelivered() {
        return delivered;
    }

    public String getPayment() {
        return payment;
    }

    public List<OrderLineRes> getLines() {
        return lines;
    }

    public String getCurrency() {
        return currency;
    }

    public String getComments() {
        return comments;
    }

    public Boolean isActive() {
        return active==null?false:active;
    }
}
