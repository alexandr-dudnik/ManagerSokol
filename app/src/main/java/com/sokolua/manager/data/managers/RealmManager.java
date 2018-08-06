package com.sokolua.manager.data.managers;

import android.support.annotation.Nullable;
import android.util.Log;

import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.OrderPlanRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;

import java.util.List;

import io.reactivex.Observable;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.internal.ManagableObject;

import static com.sokolua.manager.ui.activities.RootActivity.TAG;

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
                .contains("index", filter == null ? "": filter, Case.INSENSITIVE) //Ищем по индексному полю - пока индекс = наименование
                .sort("name")
                .findAll();

        return Observable.fromIterable(managedCustomers)
                .filter(item -> item.isLoaded()) //получаем только загруженные
                .filter(ManagableObject::isValid)
                ;
    }



    @Nullable
    public CustomerRealm getCustomerById(String id) {
        return getQueryRealmInstance().where(CustomerRealm.class).equalTo("customerId", id).findFirst();
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
        return Observable.fromIterable(customer.getTasks().sort("taskType"));
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
                return Observable.fromIterable(customer.getDebt().sort("currency"))
                        .groupBy(DebtRealm::getCurrency)
                        .map(grp->{
                            DebtRealm res = new DebtRealm(customer, grp.getKey(), 0f, 0f, false);
                            grp.forEach (item ->{
                                res.setAmount(res.getAmount()+item.getAmount());
                                res.setAmountUSD(res.getAmountUSD()+item.getAmountUSD());
                            });
                            return res;
                        })
                        ;
        }
        return Observable.empty();
    }

    public Observable<TaskRealm> getCustomerTaskByType(String customerId, int taskType) {
        CustomerRealm customer = getCustomerById(customerId);
        if (customer == null || customer.getDebt().size() == 0) {
            return Observable.empty();
        }
        return Observable.fromIterable(
                customer.getTasks()
                        .where().equalTo("taskType", taskType)
                        .sort("done",Sort.ASCENDING, "text", Sort.ASCENDING)
                        .findAll()
               );
    }

    public Observable<OrderPlanRealm> getCustomerPlan(String customerId) {
        CustomerRealm customer = getCustomerById(customerId);
        if (customer == null || customer.getDebt().size() == 0) {
            return Observable.empty();
        }
        return Observable.fromIterable(
                customer.getPlan()
                        .sort("category.name",Sort.ASCENDING)
        );
    }

    public Observable<OrderRealm> getCustomerOrders(String customerId) {
        RealmResults<OrderRealm> res = getQueryRealmInstance().where(OrderRealm.class).equalTo("customer.customerId", customerId).sort("status", Sort.ASCENDING, "date", Sort.DESCENDING).findAll();
        return Observable.fromIterable(res);
    }

    public void updateCustomerTask(String taskId, boolean checked, String result) {
        TaskRealm task = getQueryRealmInstance().where(TaskRealm.class).equalTo("taskId", taskId).findFirst();
        if (task != null && task.isLoaded() && task.isValid()) {
            TaskRealm temp = getQueryRealmInstance().copyFromRealm(task);
            temp.setDone(checked);
            temp.setResult(result);
            getQueryRealmInstance().executeTransaction(db -> db.insertOrUpdate(temp));
        }
    }
}

