package com.sokolua.manager.data.storage.realm;

import android.support.annotation.NonNull;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

public class RealmMigrations implements RealmMigration {
    @Override
    public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {
//        final RealmSchema schema = realm.getSchema();

//        switch (oldVersion) {
//            case 1:
//                final RealmObjectSchema userSchema = schema.get("UserData");
//                userSchema.addField("age", int.class);
//        }
    }
}
