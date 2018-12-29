package com.sokolua.manager.data.network.res;

import android.support.annotation.Keep;

@Keep
public class GoodItemRes {
    private String id;
    private String article;
    private String name;
    private GoodItemPrice price;
    private GoodItemRest rest;
    private CategoryRes category;
    private String group_id;
    private BrandRes brand;
    private Boolean active;


    public static class GoodItemPrice {
        Float base;
        Float min;

        public GoodItemPrice(Float base, Float min) {
            this.base = base;
            this.min = min;
        }

        public Float getBase() {
            return base;
        }

        public Float getMin() {
            return min;
        }
    }


    public static class GoodItemRest {
        Float store;
        Float distribution;
        Float official;

        public GoodItemRest(Float store, Float distribution, Float official) {
            this.store = store;
            this.distribution = distribution;
            this.official = official;
        }

        public Float getStore() {
            return store;
        }

        public Float getDistribution() {
            return distribution;
        }

        public Float getOfficial() {
            return official;
        }
    }

    public GoodItemRes(String id, String article, String name, GoodItemPrice price, GoodItemRest rest, CategoryRes category, String group_id, BrandRes brand, Boolean active) {
        this.id = id;
        this.article = article;
        this.name = name;
        this.price = price;
        this.rest = rest;
        this.category = category;
        this.group_id = group_id;
        this.brand = brand;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getArticle() {
        return article;
    }

    public String getName() {
        return name;
    }

    public GoodItemPrice getPrice() {
        return price;
    }

    public GoodItemRest getRest() {
        return rest;
    }

    public CategoryRes getCategory() {
        return category;
    }

    public String getGroupId() {
        return group_id;
    }

    public BrandRes getBrand() {
        return brand;
    }

    public Boolean isActive() {
        return active==null?false:active;
    }

}

