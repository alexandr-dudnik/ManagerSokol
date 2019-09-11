package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import io.realm.internal.Keep;

@Keep
public class PriceListRealm extends RealmObject implements Serializable {
    @Required
    @PrimaryKey
    private String priceId;
    @Required
    private String name;

    public PriceListRealm() {
    }

    public PriceListRealm(String priceId, String name) {
        this.priceId = priceId;
        this.name = name;
    }

    //region ================================ Getters ==================================

    public String getPriceId() {
        return priceId;
    }

    public String getName() {
        return name;
    }

    //endregion ============================= Getters ==================================



    //region ================================ Setters ==================================


    //endregion ============================= Setters ==================================

}
