package com.sokolua.manager.data.network.res;

import androidx.annotation.Keep;

import java.util.List;


@Keep
public class TradesRes {
    private String id;
    private String name;
    private boolean cash;
    private boolean fact;
    private boolean remote;
    private List<CategoryPercent> percents;

    public TradesRes(String id, String name, List<CategoryPercent> percents, boolean cash, boolean fact, boolean remote) {
        this.id = id;
        this.name = name;
        this.cash = cash;
        this.fact= fact;
        this.remote= remote;
        this.percents = percents;
    }

    //region =======================  Getters  =========================

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isCash() {
        return cash;
    }

    public boolean isFact() {
        return fact;
    }

    public boolean isRemote() {
        return remote;
    }

    public List<CategoryPercent> getPercents() {
        return percents;
    }


    //endregion =======================  Getters  =========================

    //region ============================== CategoryPercent =================

    @Keep
    public static class CategoryPercent {
        private String categoryId;
        private float percent;

        public CategoryPercent(String categoryId, float percent) {
            this.categoryId = categoryId;
            this.percent = percent;
        }

        //region =======================  Getters  =========================

        public String getCategoryId() {
            return categoryId;
        }

        public float getPercent() {
            return percent;
        }

        //endregion =======================  Getters  =========================
    }
    //endregion =========================== CategoryPercent =================

}
