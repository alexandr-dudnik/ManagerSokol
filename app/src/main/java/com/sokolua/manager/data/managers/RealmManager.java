package com.sokolua.manager.data.managers;

import android.support.annotation.Nullable;

import com.sokolua.manager.data.storage.realm.CustomerRealm;

import java.util.List;

import io.reactivex.Observable;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.internal.ManagableObject;

public class RealmManager {

    private Realm mRealmInstance;

    private Realm getQueryRealmInstance() {
        if (mRealmInstance == null || mRealmInstance.isClosed()) {
            mRealmInstance = Realm.getDefaultInstance();
        }
        return mRealmInstance;
    }


    public Observable<CustomerRealm> getCustomersFromRealm(String filter){
        RealmResults<CustomerRealm> managedCustomers = getQueryRealmInstance()
                .where(CustomerRealm.class)
                .contains("name", filter == null ? "": filter, Case.INSENSITIVE)
                .sort("name")
                .findAll();

        return Observable.fromIterable(managedCustomers)
                .filter(item -> item.isLoaded()) //получаем только загруженные
                .filter(ManagableObject::isValid)
                ;
    }



    @Nullable
    public CustomerRealm getCustomerById(String id) {
        Realm realm = Realm.getDefaultInstance();

        return realm.where(CustomerRealm.class).equalTo("customerId", id).findFirst();
    }

    public int getCustomerDebtType(String customerId) {
        CustomerRealm customer = getCustomerById(customerId);
        if (customer == null || customer.getDebt().size() == 0) {
            return ConstantManager.DEBT_TYPE_NO_DEBT;
        }
        return customer.getDebt().where().equalTo("outdated", true).findFirst() == null ? ConstantManager.DEBT_TYPE_NORMAL : ConstantManager.DEBT_TYPE_OUTDATED;
    }

}
