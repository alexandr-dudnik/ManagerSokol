package com.sokolua.manager.data.storage.realm;

import androidx.annotation.Keep;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

@Keep
public class TradeCategoryRealm extends RealmObject implements Serializable {
    @PrimaryKey
    @Required
    private String id;
    private TradeRealm trade;
    private GoodsCategoryRealm category;
    private float percent;

    public TradeCategoryRealm() {
    }

    public TradeCategoryRealm(TradeRealm trade, GoodsCategoryRealm category, float percent) {
        this.id = trade.getTradeId()+"#"+(category == null ? "":category.getCategoryId());
        this.trade = trade;
        this.category = category;
        this.percent = percent;
    }

    //region ================================ Getters ==================================

    public String getId() {
        return id;
    }

    public TradeRealm getTrade() {
        return trade;
    }

    public GoodsCategoryRealm getCategory() {
        return category;
    }

    public float getPercent() {
        return percent;
    }


    //endregion ============================= Getters ==================================



    //region ================================ Setters ==================================


    //endregion ============================= Setters ==================================

}
