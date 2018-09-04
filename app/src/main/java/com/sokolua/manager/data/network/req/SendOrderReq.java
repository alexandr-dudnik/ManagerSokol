package com.sokolua.manager.data.network.req;

import java.util.List;

public class SendOrderReq {
    private String date;
    private String delivery;
    private String customer_id;
    private int payment;
    private List<OrderLineReq> lines;
    private String currency;
    private String comments;

    class OrderLineReq{
        private String item_id;
        private Float quantity;
        private Float price;
    }
}
