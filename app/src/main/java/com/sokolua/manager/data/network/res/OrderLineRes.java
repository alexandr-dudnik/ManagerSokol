package com.sokolua.manager.data.network.res;

public class OrderLineRes {
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
