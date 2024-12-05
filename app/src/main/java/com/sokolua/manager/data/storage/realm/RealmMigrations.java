package com.sokolua.manager.data.storage.realm;

import androidx.annotation.NonNull;

import com.sokolua.manager.R;
import com.sokolua.manager.utils.App;

import java.util.Locale;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class RealmMigrations implements RealmMigration {
    @Override
    public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {
        final RealmSchema schema = realm.getSchema();

        //update from 1 to 2
        if (oldVersion == 1 && newVersion > 1) {
            final RealmObjectSchema tradeRealm = schema.get("TradeRealm");
            if (tradeRealm != null) {
                tradeRealm
                        .addField("fop", boolean.class)
                        .transform(obj -> obj.setBoolean("fop", false));
            }
            final RealmObjectSchema customerRealm = schema.get("CustomerRealm");
            if (customerRealm != null && tradeRealm != null){
                customerRealm.addRealmObjectField("tradeFop", tradeRealm);
            }
            oldVersion++;
        }

        //update from 2 to 3
        if (oldVersion == 2 && newVersion > 2) {
            final RealmObjectSchema orderLineRealm = schema.get("OrderLineRealm");
            if (orderLineRealm != null) {
                orderLineRealm
                        .addField("priceRequest", Float.class)
                        .transform(obj -> obj.setFloat("priceRequest", 0f));
            }
            oldVersion++;
        }

        if (oldVersion < newVersion) {
            throw new IllegalStateException(String.format(Locale.getDefault(), App.getStringRes(R.string.illegal_database_update), oldVersion, newVersion));
        }
    }
}
