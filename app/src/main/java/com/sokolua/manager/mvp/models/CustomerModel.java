package com.sokolua.manager.mvp.models;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.OrderPlanRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;
import com.sokolua.manager.ui.screens.customer.tasks.CustomerDebtItem;
import com.sokolua.manager.ui.screens.customer.tasks.CustomerTaskItem;
import com.sokolua.manager.utils.App;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public class CustomerModel extends AbstractModel {

    public Observable<List<NoteRealm>> getCustomerNotes(String customerId) {
        Observable<NoteRealm> obs = mDataManager.getCustomerNotes(customerId);
        return obs.toList().toObservable();
    }


    public Observable<List<CustomerDebtItem>> getCustomerDebt(String customerId) {
        Observable<DebtRealm> obs = mDataManager.getCustomerDebt(customerId);
        return obs.map(CustomerDebtItem::new).toList().toObservable();
    }

    public Observable<List<CustomerDebtItem>> getCustomerDebtHeadered(String customerId) {
        Observable<DebtRealm> obsNorm = mDataManager.getCustomerDebtByType(customerId, ConstantManager.DEBT_TYPE_NORMAL);
        Observable<DebtRealm> obsOutd = mDataManager.getCustomerDebtByType(customerId, ConstantManager.DEBT_TYPE_OUTDATED);
        Observable<DebtRealm> obsWhole = mDataManager.getCustomerDebtByType(customerId, ConstantManager.DEBT_TYPE_WHOLE);
        return
                Observable.just(new CustomerDebtItem(App.getStringRes(R.string.customer_debt_outdated), ConstantManager.DEBT_TYPE_OUTDATED)).filter(item -> !obsOutd.isEmpty().blockingGet())
                        .concatWith(obsOutd.map(CustomerDebtItem::new))
                        .concatWith(Observable.just(new CustomerDebtItem(App.getStringRes(R.string.customer_debt_normal), ConstantManager.DEBT_TYPE_NORMAL)).filter(item -> !obsNorm.isEmpty().blockingGet()))
                        .concatWith(obsNorm.map(CustomerDebtItem::new))
                        .concatWith(Observable.just(new CustomerDebtItem(App.getStringRes(R.string.customer_debt_whole), ConstantManager.DEBT_TYPE_WHOLE)).filter(item -> !obsWhole.isEmpty().blockingGet()))
                        .concatWith(obsWhole.map(CustomerDebtItem::new))
                        .concatWith(Observable.just(new CustomerDebtItem(App.getStringRes(R.string.customer_debt_no_debt), ConstantManager.DEBT_TYPE_WHOLE)).filter(item -> obsWhole.isEmpty().blockingGet()))
                        .toList()
                        .toObservable();
    }

    public Observable<List<CustomerTaskItem>> getCustomerTasks(String customerId) {
        Observable<TaskRealm> obs = mDataManager.getCustomerTasks(customerId);
        return obs.map(CustomerTaskItem::new).toList().toObservable();
    }

    public Observable<List<CustomerTaskItem>> getCustomerTasksHeadered(String customerId) {
        Observable<TaskRealm> obsRes = mDataManager.getCustomerTasksByType(customerId, ConstantManager.TASK_TYPE_RESEARCH);
        Observable<TaskRealm> obsInd = mDataManager.getCustomerTasksByType(customerId, ConstantManager.TASK_TYPE_INDIVIDUAL);
        return
                Observable.just(new CustomerTaskItem(App.getStringRes(R.string.customer_task_research))).filter(item -> !obsRes.isEmpty().blockingGet())
                        .concatWith(obsRes.map(CustomerTaskItem::new))
                        .concatWith(Observable.just(new CustomerTaskItem(App.getStringRes(R.string.customer_task_individual))).filter(item -> !obsInd.isEmpty().blockingGet()))
                        .concatWith(obsInd.map(CustomerTaskItem::new))
                        .concatWith(Observable.just(new CustomerTaskItem(App.getStringRes(R.string.customer_task_no_tasks))).filter(item -> Observable.concat(obsRes, obsInd).isEmpty().blockingGet()))
                        .toList()
                        .toObservable();
    }

    public Observable<List<OrderPlanRealm>> getCustomerPlan(String customerId) {
        Observable<OrderPlanRealm> obs = mDataManager.getCustomerPlan(customerId);
        return obs.toList().toObservable();
    }

    public Observable<List<OrderRealm>> getCustomerOrders(String customerId) {
        Observable<OrderRealm> obs = mDataManager.getCustomerOrders(customerId);
        return obs.toList().toObservable();
    }

    public void updateTask(String taskId, boolean checked, String result) {
        mDataManager.updateCustomerTask(taskId, checked, result);
    }
}
