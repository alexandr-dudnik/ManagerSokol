package com.sokolua.manager.data.network.req;

import androidx.annotation.Keep;

import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Keep
public class SendOrderReq {
    private final String id;
    private final String date;
    private final String delivery;
    private final String customer_id;
    private final String payment;
    private final List<OrderLineReq> lines = new ArrayList<>();
    private final String currency_id;
    private final String trade_id;
    private final String price_id;
    private final String comments;

    public SendOrderReq(OrderRealm order, List<OrderLineRealm> orderLines) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.id = order.getId();
        this.date = sdf.format(order.getDate())+"T00:00:00";
        this.delivery = sdf.format(order.getDelivery())+"T00:00:00";
        this.customer_id = order.getCustomer().getCustomerId();
        switch (order.getPayment()){
            case ConstantManager.ORDER_PAYMENT_OFFICIAL:
                this.payment = "official";
                break;
            case ConstantManager.ORDER_PAYMENT_FOP:
                this.payment = "fop";
                break;
            default:
                this.payment = "cash";
        }
        this.currency_id = order.getCurrency().getCurrencyId();
        this.comments = order.getComments();
        this.trade_id = order.getTrade()==null?null:order.getTrade().getTradeId();
        this.price_id = order.getPriceList() == null?"BASE":order.getPriceList().getPriceId();

        for (OrderLineRealm line : orderLines){
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
        return currency_id;
    }
    public String getComments() {
        return comments;
    }
    public String getTrade_id() {
        return trade_id;
    }
    public String getPrice_id() {
        return price_id;
    }
    public String getCurrency_id() {
        return currency_id;
    }
    //endregion ====================  Getters  =========================

    //region =======================  OrderLine  =========================

    @Keep
    public static class OrderLineReq{
        private final String item_id;
        private final Float quantity;
        private final Float price;
        private final Float priceRequest;


        OrderLineReq(OrderLineRealm line) {
            this.item_id = line.getItem().getItemId();
            this.price = line.getPrice();
            this.priceRequest = line.getPriceRequest();
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
        public Float getPriceRequest() {
            return priceRequest;
        }

        //endregion ====================  Getters  =========================
    }

    //endregion ====================  OrderLine  =========================
}
