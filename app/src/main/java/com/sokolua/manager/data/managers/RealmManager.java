package com.sokolua.manager.data.managers;

import android.support.annotation.Nullable;

import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;

import java.util.List;

import io.reactivex.Observable;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
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

    public Observable<NoteRealm> getCustomerNotes(String customerId) {
        CustomerRealm customer = getCustomerById(customerId);
        if (customer == null || customer.getNotes().size() == 0) {
            return Observable.empty();
        }
        return Observable.fromIterable(customer.getNotes().sort("date", Sort.DESCENDING));
    }

    public Observable<DebtRealm> getCustomerDebt(String customerId) {
        CustomerRealm customer = getCustomerById(customerId);
        if (customer == null || customer.getNotes().size() == 0) {
            return Observable.empty();
        }
        return Observable.fromIterable(customer.getDebt().sort("currency"));
    }

    public Observable<TaskRealm> getCustomerTasks(String customerId) {
        CustomerRealm customer = getCustomerById(customerId);
        if (customer == null || customer.getNotes().size() == 0) {
            return Observable.empty();
        }
        return Observable.fromIterable(customer.getTasks().sort("date"));
    }

    public Observable<DebtRealm> getCustomerDebtByType(String customerId, int debtType) {
        CustomerRealm customer = getCustomerById(customerId);
        if (customer == null || customer.getDebt().size() == 0) {
            return Observable.empty();
        }
        switch(debtType){
            case ConstantManager.DEBT_TYPE_NORMAL:
                return Observable.fromIterable(customer.getDebt().where().equalTo("outdated", false).sort("currency").findAll());
            case ConstantManager.DEBT_TYPE_OUTDATED:
                return Observable.fromIterable(customer.getDebt().where().equalTo("outdated", true).sort("currency").findAll());
            case ConstantManager.DEBT_TYPE_WHOLE:
                return Observable.fromIterable(customer.getDebt())
                        .groupBy(DebtRealm::getCurrency)
                        .map(grp->{
                            DebtRealm res = new DebtRealm(customer, grp.getKey(), 0f, 0f, false);
                            grp.forEach(r->{
                                res.setAmount(res.getAmount()+r.getAmount());
                                res.setAmountUSD(res.getAmountUSD()+r.getAmountUSD());
                               });
                            return res;
                        });
        }
        return Observable.empty();
    }
}
