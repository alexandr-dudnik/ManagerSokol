package com.sokolua.manager.data.storage.realm;

import androidx.annotation.Keep;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

@Keep
public class CurrencyRealm extends RealmObject implements Serializable {
    @PrimaryKey
    @Required
    private String currencyId;
    @Required
    private String name;
    private float rate;

    public CurrencyRealm() {
    }

    public CurrencyRealm(String currencyId, String name, float rate) {
        this.currencyId = currencyId;
        this.name = name;
        this.rate = rate;
    }

    //region ================================ Getters ==================================

    public String getName() {
        return name;
    }

    public float getRate() {
        return rate;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    //endregion ============================= Getters ==================================



    //region ================================ Setters ==================================


    //endregion ============================= Setters ==================================

}
