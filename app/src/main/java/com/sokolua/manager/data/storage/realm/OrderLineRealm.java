package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

public class OrderLineRealm extends RealmObject implements Serializable {
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
}
