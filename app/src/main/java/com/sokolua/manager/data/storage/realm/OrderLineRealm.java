package com.sokolua.manager.data.storage.realm;


import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import io.realm.internal.Keep;

@Keep
public class OrderLineRealm extends RealmObject implements Serializable {
    @PrimaryKey
    @Required
    private String lineId;
    private OrderRealm order;
    private ItemRealm item;
    private Float quantity = 0f;
    private Float price = 0f;
    private Float priceRequest = 0f;

    public OrderLineRealm() {
    }

    public OrderLineRealm(OrderRealm order, ItemRealm item, Float quantity, Float price, Float priceRequest) {
        this.order = order;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.price = priceRequest;
        this.lineId = order.getId() + "#" + item.getItemId();
    }

    //region ===================== Getters =========================

    public ItemRealm getItem() {
        return item;
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

    public OrderRealm getOrder() {
        return order;
    }

    //endregion ================== Getters =========================

    //region ===================== Setters =========================

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public void setPriceRequest(Float price) {
        this.priceRequest = price;
    }

    //endregion ================== Getters =========================
}
