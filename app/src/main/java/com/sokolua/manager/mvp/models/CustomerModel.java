package com.sokolua.manager.mvp.models;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.OrderPlanRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;
import com.sokolua.manager.ui.screens.customer.tasks.CustomerDebtItem;
import com.sokolua.manager.ui.screens.customer.tasks.CustomerTaskItem;
import com.sokolua.manager.utils.App;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class CustomerModel extends AbstractModel {

    public Observable<List<NoteRealm>> getCustomerNotes(String customerId) {
        return mDataManager.getCustomerNotes(customerId);
    }


    public Observable<List<CustomerDebtItem>> getCustomerDebtHeadered(String customerId) {
        Observable<List<DebtRealm>> obsNorm = mDataManager.getCustomerDebtByType(customerId, ConstantManager.DEBT_TYPE_NORMAL);
        Observable<List<DebtRealm>> obsOutdated = mDataManager.getCustomerDebtByType(customerId, ConstantManager.DEBT_TYPE_OUTDATED);
        Observable<List<DebtRealm>> obsWhole = mDataManager.getCustomerDebtByType(customerId, ConstantManager.DEBT_TYPE_WHOLE);

        return Observable.zip(
                obsOutdated,
                obsNorm,
                obsWhole,
                (listOutdated, listNorm, listWhole)->{
                    List<CustomerDebtItem> list = new ArrayList<>();
                    if (!listOutdated.isEmpty()){
                        list.add(new CustomerDebtItem(App.getStringRes(R.string.customer_debt_outdated), ConstantManager.DEBT_TYPE_OUTDATED));
                    }
                    for (DebtRealm item:listOutdated){
                        list.add(new CustomerDebtItem(item));
                    }

                    if (!listNorm.isEmpty()){
                        list.add(new CustomerDebtItem(App.getStringRes(R.string.customer_debt_normal), ConstantManager.DEBT_TYPE_NORMAL));
                    }
                    for (DebtRealm item:listNorm){
                        list.add(new CustomerDebtItem(item));
                    }

                    if (!listWhole.isEmpty()){
                        list.add(new CustomerDebtItem(App.getStringRes(R.string.customer_debt_whole), ConstantManager.DEBT_TYPE_WHOLE));
                    }
                    for (DebtRealm item:listWhole){
                        list.add(new CustomerDebtItem(item));
                    }

                    return list;
                }
        )
        ;

    }

    public Observable<List<CustomerTaskItem>> getCustomerTasksHeadered(String customerId) {
        Observable<List<TaskRealm>> obsRes = mDataManager.getCustomerTasksByType(customerId, ConstantManager.TASK_TYPE_RESEARCH);
        Observable<List<TaskRealm>> obsInd = mDataManager.getCustomerTasksByType(customerId, ConstantManager.TASK_TYPE_INDIVIDUAL);

        return Observable.zip(
                obsRes,
                obsInd,
                (listRes, listInd)->{
                    List<CustomerTaskItem> list = new ArrayList<>();
                    if (!listInd.isEmpty()){
                        list.add(new CustomerTaskItem(App.getStringRes(R.string.customer_task_individual)));
                    }
                    for (TaskRealm item:listInd){
                        list.add(new CustomerTaskItem(item));
                    }

                    if (!listRes.isEmpty()){
                        list.add(new CustomerTaskItem(App.getStringRes(R.string.customer_task_research)));
                    }
                    for (TaskRealm item:listRes){
                        list.add(new CustomerTaskItem(item));
                    }

                    return list;
                },
                true
        )
        ;

    }

    public Observable<List<OrderPlanRealm>> getCustomerPlan(String customerId) {
        return mDataManager.getCustomerPlan(customerId);
    }

    public Observable<List<OrderRealm>> getCustomerOrders(String customerId) {
        return mDataManager.getCustomerOrders(customerId);
    }

    public void updateTask(String taskId, boolean checked, String result) {
        mDataManager.updateCustomerTask(taskId, checked, result);
    }

    public OrderRealm getCartForCustomer(String customerId) {
        return mDataManager.getCartForCustomer(customerId);
    }

    public void addNewNote(String customerId, String note) {
        mDataManager.addNewNote(customerId, note);
    }

    public void deleteNote(String noteId) {
        mDataManager.deleteNote(noteId);
    }

    public void updateCustomerFromRemote(String customerId) {
        Observable.just(customerId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .doOnNext(id -> {
                    mDataManager.sendAllNotes(id);
                    mDataManager.sendAllTasks(id);
                    mDataManager.sendAllVisits(id);
                    mDataManager.updateCustomerFromRemote(id);
                })
                .subscribe();
    }
}
