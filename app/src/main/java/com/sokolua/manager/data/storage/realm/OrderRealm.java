package com.sokolua.manager.data.storage.realm;


import com.sokolua.manager.data.managers.ConstantManager;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import io.realm.internal.Keep;

@Keep
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
    private PriceListRealm priceList;
    private TradeRealm trade;
    @LinkingObjects("order")
    private final RealmResults<OrderLineRealm> lines=null;
    private CurrencyRealm currency;
    private String comments;
    private Boolean payOnFact;

    public OrderRealm() {
    }

    public OrderRealm(String id, CustomerRealm customer, Date date, Date delivery, int status, int payment, CurrencyRealm currency, TradeRealm trade, PriceListRealm price, String comments) {
        this.id = id;
        this.external_id = id;
        this.customer = customer;
        this.date = date;
        this.delivery = delivery;
        this.status = status;
        this.payment = payment;
        this.currency = currency;
        this.comments = comments;
        this.trade = trade;
        this.priceList = price;
        this.payOnFact = (trade != null && trade.isFact());
    }

    public OrderRealm(CustomerRealm customer, CurrencyRealm currency) {
        Calendar cal = Calendar.getInstance();
        this.id = UUID.randomUUID().toString();
        this.external_id = "";
        this.customer = customer;
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        this.date = cal.getTime();
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 0);
        this.delivery = cal.getTime();
        this.status = ConstantManager.ORDER_STATUS_CART;
        this.payment = ConstantManager.ORDER_PAYMENT_CASH;
        this.priceList = customer.getPrice();
        this.trade = customer.getTradeCash();
        this.payOnFact = (this.trade != null && this.trade.isFact());
        this.currency = currency;
        this.comments = "";
    }

    //region ================================ Getters ==================================

    public Date getDate() {
        if (this.status == ConstantManager.ORDER_STATUS_CART){
            return Calendar.getInstance().getTime();
        }
        return this.date;
    }

    public int getStatus() {
        return status;
    }

    public int getPayment() {
        return payment;
    }

    public Float getTotal() {
        float total = 0f;
        if (lines!=null) {
            for (OrderLineRealm line : lines) {
                if (line.isValid()) {
                    total += line.getPrice() * line.getQuantity();
                }
            }
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

    public CurrencyRealm getCurrency() {
        return currency;
    }

    public CustomerRealm getCustomer() {
        return customer;
    }

    public RealmResults<OrderLineRealm> getLines() {
        return lines!=null && lines.isValid()?lines:null;
    }

    public PriceListRealm getPriceList() {
        return priceList;
    }

    public TradeRealm getTrade() {
        return trade;
    }

    public Boolean isPayOnFact() {
        return payOnFact;
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

    public void setExternalId(String externalId) {
        this.external_id = externalId;
    }

    public void setTrade(TradeRealm trade) {
        this.trade = trade;
    }

    public void setPayOnFact(Boolean payOnFact) {
        this.payOnFact = payOnFact;
    }

    public void setCurrency(CurrencyRealm currency) {
        this.currency = currency;
    }

    public void setPriceList(PriceListRealm priceList) {
        this.priceList = priceList;
    }

    //endregion ================== Setters =========================
}
