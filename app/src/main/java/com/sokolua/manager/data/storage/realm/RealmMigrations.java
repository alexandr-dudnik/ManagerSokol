package com.sokolua.manager.data.storage.realm;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.sokolua.manager.R;
import com.sokolua.manager.utils.App;

import java.util.Date;
import java.util.Locale;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

@Keep
public class RealmMigrations implements RealmMigration {
    @Override
    public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {
        final RealmSchema schema = realm.getSchema();

        //update from 1 to 2
        if (oldVersion == 1 && newVersion > 1) {
            final RealmObjectSchema customerRealm = schema.get("CustomerRealm");
            final RealmObjectSchema itemRealm = schema.get("ItemRealm");

            //add flag to task that needs to sync
            final RealmObjectSchema taskRealm = schema.get("TaskRealm");
            if (taskRealm != null){
                taskRealm.addField("toSync", boolean.class);
            }

            //add flag to task that needs to sync, add geolocation and date and link to photo
            final RealmObjectSchema visitRealm = schema.get("VisitRealm");
            if (visitRealm != null){
                visitRealm
                        .addField("toSync", boolean.class)
                        .addField("latitude", float.class)
                        .addField("longitude", float.class)
                        .addField("visited", Date.class)
                        .addField("imageURI", String.class);
            }

            //add currency
            final RealmObjectSchema currencyRealm = schema.create("CurrencyRealm")
                    .addField("currency", String.class)
                    .setRequired("currency", true)
                    .addPrimaryKey("currency")
                    .addField("rate", float.class);

            if (itemRealm != null) {
                //add price list
                final RealmObjectSchema priceListRealm = schema.create("PriceListItemRealm")
                        .addRealmObjectField("item", itemRealm)
                        .addField("priceId", String.class)
                        .setRequired("priceId", true)
                        .addField("currency", String.class)
                        .setRequired("currency", true)
                        .addField("price", float.class)
                        .setRequired("price", true);

                //Remove price
                itemRealm
                        .removeField("basePrice")
                        .removeField("lowPrice");
            }

            if (customerRealm != null) {
                //Create customer phones list
                final RealmObjectSchema phonesRealm = schema.create("CustomerPhoneRealm")
                        .addRealmObjectField("customer", customerRealm)
                        .addField("phoneNumber", String.class)
                        .setRequired("phoneNumber", true);

                customerRealm
                        .transform(obj -> {
                            if (!obj.getString("phone").isEmpty()) {
                                DynamicRealmObject phone = realm.createObject(CustomerPhoneRealm.class.getSimpleName());
                                phone.setObject("customer", obj);
                                phone.setString("phoneNumber", obj.getString("phone"));
                            }
                        })
                        .removeField("phone");

            }


            oldVersion++;
        }


        if (oldVersion < newVersion) {
            throw new IllegalStateException(String.format(Locale.getDefault(), App.getStringRes(R.string.illegal_database_update), oldVersion, newVersion));
        }
    }
}
