package com.sokolua.manager.data.network.res;

public class GoodItemRes {
    public String id;
    public String article;
    public String name;
    public GoodItemPrice price;
    public GoodItemRest rest;
    public CategoryRes category;
    public String group_id;
    public BrandRes brand;


    public static class GoodItemPrice {
        public Float base;
        public Float min;

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
        public Float store;
        public Float distribution;
        public Float official;

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

    public GoodItemRes(String id, String article, String name, GoodItemPrice price, GoodItemRest rest, CategoryRes category, String group_id, BrandRes brand) {
        this.id = id;
        this.article = article;
        this.name = name;
        this.price = price;
        this.rest = rest;
        this.category = category;
        this.group_id = group_id;
        this.brand = brand;
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
}

