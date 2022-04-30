package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import io.realm.internal.Keep;

@Keep
public class TradeRealm extends RealmObject implements Serializable {
    @PrimaryKey
    @Required
    private String tradeId;
    @Required
    private String name;
    private boolean fop = false;
    private boolean cash = false;
    private boolean fact = false;
    private boolean remote = false;
    @LinkingObjects("trade")
    private final RealmResults<TradeCategoryRealm> conditions = null;

    public TradeRealm() {
    }

    public TradeRealm(String tradeId, String name, boolean fop, boolean cash, boolean fact, boolean remote) {
        this.tradeId = tradeId;
        this.name = name;
        this.fop = fop;
        this.cash = cash;
        this.fact= fact;
        this.remote= remote;
    }

    //region ================================ Getters ==================================

    public String getTradeId() {
        return tradeId;
    }

    public String getName() {
        return name;
    }

    public boolean isFop() {
        return fop;
    }

    public boolean isCash() {
        return cash;
    }

    public boolean isLTD() {
        return !cash && !fop;
    }

    public boolean isFact() {
        return fact;
    }

    public boolean isRemote() {
        return remote;
    }

    public RealmResults<TradeCategoryRealm> getConditions() {
        return conditions;
    }


    //endregion ============================= Getters ==================================



    //region ================================ Setters ==================================


    //endregion ============================= Setters ==================================

}
