package com.sokolua.manager.data.storage.realm;

import com.sokolua.manager.data.managers.ConstantManager;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class OrderRealm extends RealmObject implements Serializable{
    @Required
    @PrimaryKey
    private String id;
    private String external_id;
    @Required
    private Date date;
    private Date delivery;
    private CustomerRealm customer;
    private int status;
    private int payment;
    @LinkingObjects("order")
    private final RealmResults<OrderLineRealm> lines = null;
    private String currency;
    private String comments;

    public OrderRealm() {
    }

    public OrderRealm(String id, CustomerRealm customer, Date date, Date delivery, int status, int payment, String currency, String comments) {
        this.id = id;
        this.external_id = id;
        this.customer = customer;
        this.date = date;
        this.delivery = delivery;
        this.status = status;
        this.payment = payment;
        this.currency = currency;
        this.comments = comments;
    }

    public OrderRealm(CustomerRealm customer) {
        Calendar cal = Calendar.getInstance();
        this.id = "cart_"+customer.getCustomerId();
        this.external_id = "";
        this.customer = customer;
        this.date = cal.getTime();
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 0);
        this.delivery = cal.getTime();
        this.status = ConstantManager.ORDER_STATUS_CART;
        this.payment = ConstantManager.ORDER_PAYMENT_CASH;
        this.currency = ConstantManager.MAIN_CURRENCY;
        this.comments = "";
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
        float total = 0f;
        for (OrderLineRealm line : getLines()){
            total += line.getPrice()*line.getQuantity();
        }
        return total;
    }

    public String getComments() {
        return comments;
    }

    public String getId() {
        return id;
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

    public RealmResults<OrderLineRealm> getLines() {
        return lines;
    }

    //endregion ============================= Getters ==================================

    //region ===================== Setters =========================

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDelivery(Date delivery) {
        this.delivery = delivery;
    }

    public void setCustomer(CustomerRealm customer) {
        this.customer = customer;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    //endregion ================== Setters =========================
}
