package com.sokolua.manager.data.storage.realm;

import android.support.annotation.Keep;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

@Keep
public class OrderLineRealm extends RealmObject implements Serializable {
    @PrimaryKey
    @Required
    private String lineId;
    private OrderRealm order;
    private ItemRealm item;
    private Float quantity=0f;
    private Float price=0f;

    public OrderLineRealm() {
    }

    public OrderLineRealm(OrderRealm order, ItemRealm item, Float quantity, Float price) {
        this.order = order;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.lineId = order.getId()+"#"+item.getItemId();
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

    //endregion ================== Getters =========================
}
