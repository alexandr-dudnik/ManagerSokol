package com.sokolua.manager.data.network.res;


import androidx.annotation.Keep;

import java.util.List;


@Keep
public class OrderRes {
    private String id;
    private String date;
    private String delivery;
    private String customer_id;
    private String price_id;
    private String trade_id;
    private String delivered;
    private String payment;
    private List<OrderLineRes> lines;
    private String currency_id;
    private String comments;
    private Boolean active;

    public OrderRes(String id, String date, String delivery, String customer_id, String price_id, String trade_id, String delivered, String payment, List<OrderLineRes> lines, String currency_id, String comments, Boolean active) {
        this.id = id;
        this.date = date;
        this.delivery = delivery;
        this.customer_id = customer_id;
        this.price_id = price_id;
        this.trade_id = trade_id;
        this.delivered = delivered;
        this.payment = payment;
        this.lines = lines;
        this.currency_id = currency_id;
        this.comments = comments;
        this.active = active;
    }


//region ============================== Getters =================

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
        return currency_id;
    }

    public String getComments() {
        return comments;
    }

    public Boolean isActive() {
        return active==null?false:active;
    }

    public String getPriceId() {
        return price_id;
    }

    public String getTradeId() {
        return trade_id;
    }

//endregion =========================== Getters =================

    //region ============================== Class OrderLinesRes =================

    @Keep
    public static class OrderLineRes {
        private String item_id;
        private String item_name;
        private String item_article;
        private Float quantity;
        private Float price;

        public OrderLineRes(String item_id, String item_name, String item_article, Float quantity, Float price) {
            this.item_id = item_id;
            this.item_name = item_name;
            this.item_article = item_article;
            this.quantity = quantity;
            this.price = price;
        }

        public String getItemId() {
            return item_id;
        }

        public String getItemName() {
            return item_name;
        }

        public Float getQuantity() {
            return quantity;
        }

        public Float getPrice() {
            return price;
        }

        public String getItemArticle() {
            return item_article;
        }
    }

    //endregion =========================== Class OrderLinesRes =================
}
