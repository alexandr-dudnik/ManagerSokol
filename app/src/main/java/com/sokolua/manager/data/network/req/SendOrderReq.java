package com.sokolua.manager.data.network.req;

import android.support.annotation.Keep;

import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Keep
public class SendOrderReq {
    private String id;
    private String date;
    private String delivery;
    private String customer_id;
    private String payment;
    private List<OrderLineReq> lines = new ArrayList<>();
    private String currency;
    private String comments;

    public SendOrderReq(OrderRealm order) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.id = order.getId();
        this.date = sdf.format(order.getDate())+"T00:00:00";
        this.delivery = sdf.format(order.getDelivery())+"T00:00:00";
        this.customer_id = order.getCustomer().getCustomerId();
        this.payment = order.getPayment() == ConstantManager.ORDER_PAYMENT_CASH?"cash":"official";
        this.currency = order.getCurrency();
        this.comments = order.getComments();

        for (OrderLineRealm line : order.getLines()){
            lines.add(new OrderLineReq(line));
        }
    }

    //region =======================  Getters  =========================


    public String getId() {
        return id;
    }
    public String getDate() {
        return date;
    }
    public String getDelivery() {
        return delivery;
    }
    public String getCustomer_id() {
        return customer_id;
    }
    public String getPayment() {
        return payment;
    }
    public List<OrderLineReq> getLines() {
        return lines;
    }
    public String getCurrency() {
        return currency;
    }
    public String getComments() {
        return comments;
    }

    //endregion ====================  Getters  =========================

    //region =======================  OrderLine  =========================

    static class OrderLineReq{
        private String item_id;
        private Float quantity;
        private Float price;


        public OrderLineReq(OrderLineRealm line) {
            this.item_id = line.getItem().getItemId();
            this.price = line.getPrice();
            this.quantity = line.getQuantity();
        }

        //region =======================  Getters  =========================

        public String getItem_id() {
            return item_id;
        }
        public Float getQuantity() {
            return quantity;
        }
        public Float getPrice() {
            return price;
        }

        //endregion ====================  Getters  =========================
    }

    //endregion ====================  OrderLine  =========================
}
