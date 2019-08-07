package com.sokolua.manager.data.network.res;

import java.util.List;

import io.realm.internal.Keep;

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
    private List<ItemPrice> price_list;


    public GoodItemRes(String id, String article, String name, GoodItemPrice price, GoodItemRest rest, CategoryRes category, String group_id, BrandRes brand, Boolean active, List<ItemPrice> price_list) {
        this.id = id;
        this.article = article;
        this.name = name;
        this.price = price;
        this.rest = rest;
        this.category = category;
        this.group_id = group_id;
        this.brand = brand;
        this.active = active;
        this.price_list = price_list;
    }

    //region ============================== Getters =================

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

    public List<ItemPrice> getPriceList() {
        return price_list;
    }

    //endregion =========================== Getters =================



    //region ============================== GoodItemPrice =================

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

    //endregion =========================== GoodItemPrice =================

    //region ============================== GoodItemRest =================

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

    //endregion =========================== GoodItemRest =================

    //region ============================== ItemPrice =================

    public static class ItemPrice {
        private String price_id;
        private String price_name;
        private String currency;
        private float price;

        public ItemPrice(String price_id, String price_name, String currency, float price) {
            this.price_id = price_id;
            this.price_name = price_name;
            this.currency = currency;
            this.price = price;
        }

        //region =======================  Getters  =========================
        public String getPriceId() {
            return price_id;
        }

        public String getCurrency() {
            return currency;
        }

        public float getPrice() {
            return price;
        }

        public String getPriceName() {
            return price_name;
        }

        //endregion =======================  Getters  =========================
    }

    //endregion =========================== ItemPrice =================


    //region ============================== BrandRes =================

    @Keep
    public static class BrandRes {
        private String id;
        private String name;

        public BrandRes(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    //endregion =========================== BrandRes =================

    //region ============================== CategoryRes =================

    @Keep
    public static class CategoryRes {
        private String id;
        private String name;

        public CategoryRes(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    //endregion =========================== CategoryRes =================
}

