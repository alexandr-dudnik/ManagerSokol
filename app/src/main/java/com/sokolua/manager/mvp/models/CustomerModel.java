package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;
import com.sokolua.manager.ui.screens.cust_list.CustomerListItem;
import com.sokolua.manager.ui.screens.customer.tasks.CustomerDebtItem;
import com.sokolua.manager.ui.screens.customer.tasks.CustomerTaskItem;

import java.util.List;

import io.reactivex.Observable;

public class CustomerModel extends AbstractModel {

    public Observable<List<NoteRealm>> getCustomerNotes(String customerId) {
        Observable<NoteRealm> obs = mDataManager.getCustomerNotes(customerId);
        return obs.toList().toObservable();
    }

    public Observable<List<CustomerDebtItem>> getCustomerDebtByType(String customerId, int debtType) {
        Observable<DebtRealm> obs = mDataManager.getCustomerDebtByType(customerId, debtType);
        return obs.map(CustomerDebtItem::new).toList().toObservable();
    }

    public Observable<List<CustomerDebtItem>> getCustomerDebt(String customerId) {
        Observable<DebtRealm> obs = mDataManager.getCustomerDebt(customerId);
        return obs.map(CustomerDebtItem::new).toList().toObservable();
    }

    public Observable<List<CustomerDebtItem>> getCustomerDebtHeadered(String customerId) {
        Observable<DebtRealm> obsNorm = mDataManager.getCustomerDebtByType(customerId, ConstantManager.DEBT_TYPE_NORMAL);
        Observable<DebtRealm> obsOutd = mDataManager.getCustomerDebtByType(customerId, ConstantManager.DEBT_TYPE_OUTDATED);
        Observable<DebtRealm> obsWhole = mDataManager.getCustomerDebtByType(customerId, ConstantManager.DEBT_TYPE_WHOLE);
        return Observable.empty();
    }

    public Observable<List<CustomerTaskItem>> getCustomerTasks(String customerId) {
        Observable<TaskRealm> obs = mDataManager.getCustomerTasks(customerId);
        return Observable.empty();
    }
}
