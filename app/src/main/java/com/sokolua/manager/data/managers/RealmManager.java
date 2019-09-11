package com.sokolua.manager.data.managers;

import android.os.Looper;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.core.util.Pair;

import com.sokolua.manager.data.network.res.CurrencyRes;
import com.sokolua.manager.data.network.res.CustomerRes;
import com.sokolua.manager.data.network.res.GoodGroupRes;
import com.sokolua.manager.data.network.res.GoodItemRes;
import com.sokolua.manager.data.network.res.OrderRes;
import com.sokolua.manager.data.network.res.TradesRes;
import com.sokolua.manager.data.storage.realm.BrandsRealm;
import com.sokolua.manager.data.storage.realm.CurrencyRealm;
import com.sokolua.manager.data.storage.realm.CustomerDiscountRealm;
import com.sokolua.manager.data.storage.realm.CustomerPhoneRealm;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.GoodsCategoryRealm;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderPlanRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.data.storage.realm.PriceListItemRealm;
import com.sokolua.manager.data.storage.realm.PriceListRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;
import com.sokolua.manager.data.storage.realm.TradeCategoryRealm;
import com.sokolua.manager.data.storage.realm.TradeRealm;
import com.sokolua.manager.data.storage.realm.VisitRealm;
import com.sokolua.manager.utils.UiHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposables;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.internal.ManagableObject;

@Keep
public class RealmManager {

    //region =======================  Realm instance  =========================


    //private LongSparseArray<Realm> mRealmInstance = new LongSparseArray<>();
    private Realm mUIRealm = null;

    private Realm getQueryRealmInstance() {
        try {
            if (Realm.getDefaultConfiguration() != null) {
                Realm.compactRealm(Realm.getDefaultConfiguration());
            }
        } catch(Throwable ignore){}

        if (isUIThread()){
            if (mUIRealm == null) {
                mUIRealm = Realm.getDefaultInstance();
            }
            if (!mUIRealm.isInTransaction()) {
                mUIRealm.refresh();
            }
            return mUIRealm;
        }

        Realm currentRealm = Realm.getDefaultInstance();
        currentRealm.refresh();
        return currentRealm;
    }

    private void closeQueryInstance(Realm instance){
        if (!isUIThread()){
            instance.close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (mUIRealm != null && !mUIRealm.isClosed()){
            mUIRealm.close();
        }

        if (Realm.getDefaultConfiguration() != null) {
            Realm.compactRealm(Realm.getDefaultConfiguration());
        }

        super.finalize();
    }

    //endregion ====================  Realm instance  =========================


    //region =======================  Service  =========================

    void compactDatabase() {
        if (Realm.getDefaultConfiguration() != null) {
            Realm.compactRealm(Realm.getDefaultConfiguration());
        }
    }


    private boolean isUIThread(){
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }


    private <T extends RealmObject> Observable<List<T>> getListObservable(Realm realmInstance, RealmQuery<T> realmQuery) {
        Observable<List<T>> obs;
        if (isUIThread()) {
            obs = Observable.create((ObservableOnSubscribe<List<T>>) emitter -> {
                final RealmResults<T> res = realmQuery.findAllAsync();
                final RealmChangeListener<RealmResults<T>> realmChangeListener = element -> {
                    if (element.isLoaded() && element.isValid() && !emitter.isDisposed()) {
                        emitter.onNext(element);
                    }
                };
                emitter.setDisposable(Disposables.fromAction(() -> {
                    if (res.isValid()) {
                        res.removeChangeListener(realmChangeListener);
                    }
                    closeQueryInstance(realmInstance);
                }));
                res.addChangeListener(realmChangeListener);
            })
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(AndroidSchedulers.mainThread())
            ;
        }else{
            obs = Observable.fromIterable(realmInstance.copyFromRealm(realmQuery.findAll()))
                    .filter(item -> item.isLoaded()) //получаем только загруженные
                    .filter(ManagableObject::isValid)
                    .toList()
                    .toObservable();
            closeQueryInstance(realmInstance);
        }
        return obs;
    }




    void clearDataBase() {
        Realm inst = getQueryRealmInstance();
        try {
            inst.removeAllChangeListeners();
            inst.executeTransaction(db -> db.deleteAll());
        }finally {
            closeQueryInstance(inst);
        }
    }


    //endregion ====================  Service  =========================


    //region =======================  Customers  =========================

    Observable<List<CustomerRealm>> getCustomersList(String filter){
        Realm curInstance = getQueryRealmInstance();
        try {
            RealmQuery<CustomerRealm> customersQuery = curInstance
                    .where(CustomerRealm.class);
            if (filter != null && !filter.isEmpty()) {
                customersQuery.contains("index", filter.toLowerCase(), Case.INSENSITIVE); //Ищем по индексному полю - пока индекс = наименование
            }
            customersQuery.sort("name");


            return getListObservable(curInstance, customersQuery);
        }finally {
            closeQueryInstance(curInstance);
        }
    }

    @Nullable
    CustomerRealm getCustomerById(String id) {
        if (id == null || id.isEmpty()) return null;
        Realm curInstance = getQueryRealmInstance();
        try {
            CustomerRealm result = curInstance
                    .where(CustomerRealm.class)
                    .equalTo("customerId", id)
                    .findFirst();
            if (result != null && !isUIThread()) {
                result = curInstance.copyFromRealm(result);
            }

            closeQueryInstance(curInstance);
            return result;
        }finally {
            closeQueryInstance(curInstance);
        }
    }

    void setDeliveryDate(String orderId, Date mDate) {
        OrderRealm order = getOrderById(orderId);
        if (order != null) {
            Realm curInstance = getQueryRealmInstance();
            curInstance.executeTransaction(db -> order.setDelivery(mDate));
            closeQueryInstance(curInstance);
        }
    }

    void saveCustomerToRealm(CustomerRes customerRes, boolean createOnly){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Realm curInstance = getQueryRealmInstance();
        try {

            final CustomerRes.CustomerConditionRes tradeCondition = customerRes.getTradeCondition();

            PriceListRealm price = null;
            if (tradeCondition != null && tradeCondition.getPrice() != null && !tradeCondition.getPrice().isEmpty()) {
                price = getPriceListById(tradeCondition.getPrice());
            }

            TradeRealm cond_cash = null;
            if (tradeCondition != null && tradeCondition.getCash() != null && !tradeCondition.getCash().isEmpty()) {
                cond_cash = getTradeById(tradeCondition.getCash());
            }
            TradeRealm cond_off = null;
            if (tradeCondition != null && tradeCondition.getOfficial() != null && !tradeCondition.getOfficial().isEmpty()) {
                cond_off = getTradeById(tradeCondition.getOfficial());
            }


            CustomerRealm newCust = new CustomerRealm(
                    customerRes.getId(),
                    customerRes.getName(),
                    customerRes.getContactName(),
                    customerRes.getAddress(),
                    customerRes.getEmail(),
                    customerRes.getCategory(),
                    price,
                    cond_cash,
                    cond_off
            );

            if (createOnly) {
                if (curInstance.where(CustomerRealm.class).equalTo("customerId", customerRes.getId()).findFirst() == null) {
                    curInstance.executeTransaction(db -> db.insertOrUpdate(newCust));
                }
                closeQueryInstance(curInstance);
                return;
            }


            RealmList<DebtRealm> mDebt = new RealmList<>();
            if (customerRes.getDebt() != null) {
                for (CustomerRes.DebtRes debt : customerRes.getDebt()) {
                    mDebt.add(new DebtRealm(newCust, debt.getCurrency(), debt.getAmount(), debt.getAmountUSD(), debt.isOutdated()));
                }
            }

            RealmList<NoteRealm> mNotes = new RealmList<>();
            if (customerRes.getNotes() != null) {
                for (CustomerRes.NoteRes note : customerRes.getNotes()) {
                    Date noteDate;
                    try {
                        noteDate = sdf.parse(note.getDate());
                    } catch (ParseException e) {
                        noteDate = Calendar.getInstance().getTime();
                    }
                    mNotes.add(new NoteRealm(newCust, note.getId(), noteDate, note.getText()));
                }
            }

            RealmList<TaskRealm> mTasks = new RealmList<>();
            if (customerRes.getTasks() != null) {
                Calendar cal = Calendar.getInstance();
                for (CustomerRes.TaskRes task : customerRes.getTasks()) {
                    Date mTaskDate = cal.getTime();
                    try {
                        mTaskDate = sdf.parse(task.getDate());
                    } catch (Exception ignore) {
                    }
                    mTasks.add(new TaskRealm(newCust, task.getId(), task.getText(), task.getType(), mTaskDate, task.isDone(), task.getResult()));
                }
            }

            RealmList<CustomerPhoneRealm> mPhones = new RealmList<>();
            if (customerRes.getPhones() != null) {
                for (String phone : customerRes.getPhones()) {
                    mPhones.add(new CustomerPhoneRealm(newCust, phone));
                }
            }

            RealmList<OrderPlanRealm> mPlan = new RealmList<>();
            RealmList<GoodsCategoryRealm> mCats = new RealmList<>();
            if (customerRes.getPlan() != null) {
                for (CustomerRes.OrderPlanRes plan : customerRes.getPlan()) {
                    GoodsCategoryRealm cat = curInstance.where(GoodsCategoryRealm.class).equalTo("categoryId", plan.getCategoryId()).findFirst();
                    if (cat == null) {
                        cat = new GoodsCategoryRealm(plan.getCategoryId(), plan.getCategoryName(), "");
                        mCats.add(cat);
                    }
                    mPlan.add(new OrderPlanRealm(newCust, cat, plan.getAmount()));
                }
            }

            RealmList<CustomerDiscountRealm> mDisc = new RealmList<>();
            RealmList<ItemRealm> mItems = new RealmList<>();
            if (customerRes.getDiscounts() != null) {
                for (CustomerRes.CustomerDiscountRes disc : customerRes.getDiscounts()) {
                    if (disc.getType() == ConstantManager.DISCOUNT_TYPE_ITEM) {
                        ItemRealm item = curInstance.where(ItemRealm.class).equalTo("itemId", disc.getTargetId()).findFirst();
                        if (item == null) {
                            for (ItemRealm itemIter : mItems) {
                                if (itemIter.getItemId().equals(disc.getTargetId())) {
                                    item = itemIter;
                                    break;
                                }
                            }
                            if (item == null) {
                                item = new ItemRealm(disc.getTargetId(), disc.getTargetName(), "", null);
                                mItems.add(item);
                            }
                        }
                        mDisc.add(new CustomerDiscountRealm(newCust, item, disc.getPercent()));

                    } else {
                        GoodsCategoryRealm cat = curInstance.where(GoodsCategoryRealm.class).equalTo("categoryId", disc.getTargetId()).findFirst();
                        if (cat == null) {
                            for (GoodsCategoryRealm catIter : mCats) {
                                if (catIter.getCategoryId().equals(disc.getTargetId())) {
                                    cat = catIter;
                                    break;
                                }
                            }
                            if (cat == null) {
                                cat = new GoodsCategoryRealm(disc.getTargetId(), disc.getTargetName(), "");
                                mCats.add(cat);
                            }
                        }
                        mDisc.add(new CustomerDiscountRealm(newCust, cat, disc.getPercent()));
                    }

                }
            }

            RealmList<VisitRealm> mVisits = new RealmList<>();
            if (customerRes.getVisits() != null) {
                for (CustomerRes.VisitRes visit : customerRes.getVisits()) {
                    Date visitDate;
                    try {
                        visitDate = sdf.parse(visit.getDate());
                    } catch (ParseException e) {
                        visitDate = Calendar.getInstance().getTime();
                    }
                    mVisits.add(new VisitRealm(newCust, visit.getId(), visitDate, visit.isDone()));
                }
            }

            RealmResults<TaskRealm> oldTasks = curInstance.where(TaskRealm.class).equalTo("customer.customerId", customerRes.getId()).findAll();
            RealmResults<OrderPlanRealm> oldPlans = curInstance.where(OrderPlanRealm.class).equalTo("customer.customerId", customerRes.getId()).findAll();
            RealmResults<DebtRealm> oldDebt = curInstance.where(DebtRealm.class).equalTo("customer.customerId", customerRes.getId()).findAll();
            RealmResults<NoteRealm> oldNotes = curInstance.where(NoteRealm.class).equalTo("customer.customerId", customerRes.getId()).findAll();
            RealmResults<CustomerDiscountRealm> oldDisc = curInstance.where(CustomerDiscountRealm.class).equalTo("customer.customerId", customerRes.getId()).findAll();
            RealmResults<VisitRealm> oldVisits = curInstance.where(VisitRealm.class).equalTo("customer.customerId", customerRes.getId()).findAll();
            RealmResults<CustomerPhoneRealm> oldPhones = curInstance.where(CustomerPhoneRealm.class).equalTo("customer.customerId", customerRes.getId()).findAll();

            Log.i("DEBUG", mPhones.toString());

            curInstance.executeTransaction(db -> {
                db.insertOrUpdate(newCust);
                oldTasks.deleteAllFromRealm();
                oldDebt.deleteAllFromRealm();
                oldPlans.deleteAllFromRealm();
                oldNotes.deleteAllFromRealm();
                oldDisc.deleteAllFromRealm();
                oldVisits.deleteAllFromRealm();
                oldPhones.deleteAllFromRealm();

                db.insertOrUpdate(mDebt);
                db.insertOrUpdate(mNotes);
                db.insertOrUpdate(mTasks);
                db.insertOrUpdate(mPlan);
                db.insertOrUpdate(mDisc);
                db.insertOrUpdate(mVisits);
                db.insertOrUpdate(mPhones);

            });
        }finally {
            closeQueryInstance(curInstance);
        }
    }


    void deleteCustomerFromRealm(String id) {
        if (id == null || id.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();

        try {
            CustomerRealm mCustomer = curInstance
                    .where(CustomerRealm.class)
                    .equalTo("customerId", id)
                    .findFirst();
            if (mCustomer == null) {
                closeQueryInstance(curInstance);
                return;
            }

            List<OrderRealm> orders = curInstance.copyFromRealm(curInstance.where(OrderRealm.class).equalTo("customer.customerId", id).findAll());

            for (OrderRealm order : orders) {
                deleteOrderFromRealm(order.getId());
            }

            RealmResults<TaskRealm> oldTasks = curInstance.where(TaskRealm.class).equalTo("customer.customerId", id).findAll();
            RealmResults<OrderPlanRealm> oldPlans = curInstance.where(OrderPlanRealm.class).equalTo("customer.customerId", id).findAll();
            RealmResults<DebtRealm> oldDebt = curInstance.where(DebtRealm.class).equalTo("customer.customerId", id).findAll();
            RealmResults<NoteRealm> oldNotes = curInstance.where(NoteRealm.class).equalTo("customer.customerId", id).findAll();
            RealmResults<CustomerDiscountRealm> oldDisc = curInstance.where(CustomerDiscountRealm.class).equalTo("customer.customerId", id).findAll();
            RealmResults<VisitRealm> oldVisits = curInstance.where(VisitRealm.class).equalTo("customer.customerId", id).findAll();
            RealmResults<CustomerPhoneRealm> oldPhones = curInstance.where(CustomerPhoneRealm.class).equalTo("customer.customerId", id).findAll();
            curInstance.executeTransaction(db -> {
                mCustomer.deleteFromRealm();
                oldTasks.deleteAllFromRealm();
                oldDebt.deleteAllFromRealm();
                oldPlans.deleteAllFromRealm();
                oldNotes.deleteAllFromRealm();
                oldDisc.deleteAllFromRealm();
                oldVisits.deleteAllFromRealm();
                oldPhones.deleteAllFromRealm();
            });
        }finally {
            closeQueryInstance(curInstance);
        }

    }


    //endregion ====================  Customers  =========================



    //region =======================  Customer Debt  =========================


    int getCustomerDebtType(String customerId) {
        if (customerId == null || customerId.isEmpty()) return ConstantManager.DEBT_TYPE_NO_DEBT;
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<DebtRealm> qAll = curInstance
                .where(DebtRealm.class)
                .equalTo("customer.customerId", customerId);
        if (qAll.findFirst() == null) {
            closeQueryInstance(curInstance);
            return ConstantManager.DEBT_TYPE_NO_DEBT;
        }
        DebtRealm outdated = qAll.equalTo("outdated", true).findFirst();
        closeQueryInstance(curInstance);
        return outdated == null ? ConstantManager.DEBT_TYPE_NORMAL : ConstantManager.DEBT_TYPE_OUTDATED;
    }


    Observable<List<DebtRealm>> getCustomerDebt(String customerId) {
        if (customerId == null || customerId.isEmpty()) return Observable.empty();
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<DebtRealm> qAll = curInstance
                .where(DebtRealm.class)
                .equalTo("customer.customerId", customerId);
        qAll.sort("currency");

        return getListObservable(curInstance, qAll);
    }



    Observable<List<DebtRealm>> getCustomerDebtByType(String customerId, int debtType) {
        if (customerId == null || customerId.isEmpty()) return Observable.empty();
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<DebtRealm> qAll = curInstance
                .where(DebtRealm.class)
                .equalTo("customer.customerId", customerId);

        switch(debtType){
            case ConstantManager.DEBT_TYPE_NORMAL:
                qAll.equalTo("outdated", false).sort("currency");
                return getListObservable(curInstance, qAll);
            case ConstantManager.DEBT_TYPE_OUTDATED:
                qAll.equalTo("outdated", true).sort("currency");
                return getListObservable(curInstance, qAll);
            case ConstantManager.DEBT_TYPE_WHOLE:
                List<DebtRealm> all = curInstance.copyFromRealm(qAll.sort("currency").findAll());
                closeQueryInstance(curInstance);
                return Observable.fromIterable(all)
                        .groupBy(DebtRealm::getCurrency)
                        .map(grp->{
                            CustomerRealm customer = getCustomerById(customerId);
                            DebtRealm res = new DebtRealm(customer, grp.getKey(), 0f, 0f, false);
                            grp.forEach (item ->{
                                res.setAmount(res.getAmount()+item.getAmount());
                                res.setAmountUSD(res.getAmountUSD()+item.getAmountUSD());
                            });
                            return res;
                        })
                        .filter(item -> item.isLoaded()) //получаем только загруженные
                        .filter(ManagableObject::isValid)
                        .toList()
                        .toObservable()
                        ;
        }
        return Observable.empty();
    }

    //endregion ====================  Customer Debt  =========================


    //region =======================  Customer Notes  =========================

    Observable<List<NoteRealm>> getCustomerNotes(String customerId) {
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<NoteRealm> qAll = curInstance
                .where(NoteRealm.class);
        if (customerId != null && !customerId.isEmpty()){
            qAll.equalTo("customer.customerId", customerId);
        }
        qAll.sort("date", Sort.DESCENDING);
        return getListObservable(curInstance, qAll);
    }

    void addNewNote(String customerId, String note) {
        if (customerId == null || customerId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        CustomerRealm cust = curInstance
                .where(CustomerRealm.class)
                .equalTo("customerId", customerId)
                .findFirst();
        if (cust != null) {
            curInstance.executeTransaction(db -> db.insertOrUpdate(new NoteRealm(cust, note)));
        }
        closeQueryInstance(curInstance);
    }

    void deleteNote(String noteId) {
        if (noteId == null || noteId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        NoteRealm tmp = curInstance
                .where(NoteRealm.class)
                .equalTo("noteId", noteId)
                .findFirst();
        if (tmp != null) {
            if (isUIThread()) {
                tmp.removeAllChangeListeners();
            }
            curInstance.executeTransaction(db -> tmp.deleteFromRealm());
        }
        closeQueryInstance(curInstance);
    }

    @Nullable
    NoteRealm getCustomerNoteById(String mId) {
        if (mId == null || mId.isEmpty()) return null;
        Realm curInstance = getQueryRealmInstance();
        NoteRealm mNote = curInstance.where(NoteRealm.class).equalTo("noteId", mId).findFirst();

        if (isUIThread() || mNote==null) {
            return mNote;
        }
        mNote = curInstance.copyFromRealm(mNote);
        closeQueryInstance(curInstance);
        return mNote;
    }

    Observable<NoteRealm> getNotesToSend(String filter) {
        Realm curInst = getQueryRealmInstance();
        RealmQuery<NoteRealm> query = curInst
                .where(NoteRealm.class)
                .equalTo("externalId", "");
        if (!filter.isEmpty()) {
            query = query.equalTo("customer.customerId", filter);
        }
        List<NoteRealm> res = curInst.copyFromRealm(query.findAll());
        closeQueryInstance(curInst);
        return Observable.fromIterable(res);
    }

    void updateNoteExternalId(String noteId, String newId) {
        if (noteId == null || noteId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        NoteRealm note = curInstance
                .where(NoteRealm.class)
                .equalTo("noteId", noteId)
                .findFirst();
        if (note != null && note.isValid()) {
            curInstance.executeTransaction(db-> note.setExternalId(newId));
        }
        closeQueryInstance(curInstance);

    }

    //endregion ====================  Customer Notes  =========================



    //region =======================  Customer Tasks  =========================

    Observable<List<TaskRealm>> getCustomerTasks(String customerId) {

        Realm curInstance = getQueryRealmInstance();
        RealmQuery<TaskRealm> qAll = curInstance
                .where(TaskRealm.class);
        if (customerId != null && !customerId.isEmpty()) {
            qAll.equalTo("customer.customerId", customerId);
        }
        qAll.sort("taskType",Sort.ASCENDING, "date", Sort.ASCENDING);

        return getListObservable(curInstance, qAll);
    }

    Observable<List<TaskRealm>> getCustomerTaskByType(String customerId, int taskType) {
        if (customerId == null || customerId.isEmpty()) return Observable.empty();
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<TaskRealm> qAll = curInstance
                .where(TaskRealm.class)
                .equalTo("customer.customerId", customerId)
                .equalTo("taskType", taskType);
        qAll.sort("done",Sort.ASCENDING, "text", Sort.ASCENDING);
        return getListObservable(curInstance, qAll);
    }

    void updateCustomerTask(String taskId, boolean checked, String result) {
        if (taskId == null || taskId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        TaskRealm task = curInstance
                .where(TaskRealm.class)
                .equalTo("taskId", taskId).findFirst();
        if (task != null && task.isLoaded() && task.isValid()) {
            curInstance.executeTransaction(db -> {
                task.setDone(checked);
                task.setResult(result);
                task.setToSync(true);
            });
        }
        closeQueryInstance(curInstance);
    }

    TaskRealm getCustomerTaskByTypeId(String taskId) {
        if (taskId == null || taskId.isEmpty()) return null;
        Realm curInstance = getQueryRealmInstance();
        TaskRealm mTask = curInstance.where(TaskRealm.class).equalTo("taskId", taskId).findFirst();

        if (isUIThread() || mTask==null) {
            return mTask;
        }
        mTask = curInstance.copyFromRealm(mTask);
        closeQueryInstance(curInstance);
        return mTask;
    }

    void setCustomerTaskSynced(String taskId){
        if (taskId == null || taskId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        TaskRealm mTask = curInstance.where(TaskRealm.class).equalTo("taskId", taskId).findFirst();
        if (mTask!=null && mTask.isValid()){
            curInstance.executeTransaction(db-> mTask.setToSync(false));
        }
        closeQueryInstance(curInstance);
    }


    //endregion ====================  Customer Tasks  =========================


    //region =======================  Customer Plan  =========================

    Observable<List<OrderPlanRealm>> getCustomerPlan(String customerId) {
        if (customerId == null || customerId.isEmpty()) return Observable.empty();
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<OrderPlanRealm> qAll = curInstance
                .where(OrderPlanRealm.class)
                .equalTo("customer.customerId", customerId);
        qAll.sort("category.name",Sort.ASCENDING);
        return getListObservable(curInstance, qAll);
    }

    //endregion ====================  Customer Plan  =========================


    //region =======================  Customer Visits  =========================


    Observable<List<CustomerRealm>> getCustomersByVisitDate(Date day) {
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<CustomerRealm> qAll = curInstance
                        .where(CustomerRealm.class)
                        .equalTo("visits.date", day)
                        .sort("name", Sort.ASCENDING)
                        ;
        return getListObservable(curInstance, qAll);
    }

    Observable<List<VisitRealm>> getVisitsByDate(Date day) {
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<VisitRealm> qAll = curInstance
                        .where(VisitRealm.class)
                        .equalTo("date", day)
                        .sort("customer.name", Sort.ASCENDING)
                        ;
        return getListObservable(curInstance, qAll);
    }


    VisitRealm getVisitById(String visitId) {
        if (visitId == null || visitId.isEmpty()) return null;
        Realm curInstance = getQueryRealmInstance();
        VisitRealm mVisit = curInstance.where(VisitRealm.class).equalTo("id", visitId).findFirst();

        if (isUIThread() || mVisit==null) {
            return mVisit;
        }
        mVisit = curInstance.copyFromRealm(mVisit);
        closeQueryInstance(curInstance);
        return mVisit;
    }


    void updateVisitGeolocation(String visitId, float mLat, float mLong) {
        Realm curInstance = getQueryRealmInstance();
        VisitRealm mVisit = curInstance.where(VisitRealm.class).equalTo("id", visitId).findFirst();
        final Date today = new Date();
        if (mVisit!=null && mVisit.isValid()) {
            curInstance.executeTransaction(db -> mVisit.setVisited(today, mLong, mLat));
        }
        closeQueryInstance(curInstance);
    }

    void updateVisitScreenshot(String visitId, String imageURI) {
        Realm curInstance = getQueryRealmInstance();
        VisitRealm mVisit = curInstance.where(VisitRealm.class).equalTo("id", visitId).findFirst();

        if (mVisit!=null && mVisit.isValid()) {
            curInstance.executeTransaction(db -> mVisit.setImageURI(imageURI));
        }

        closeQueryInstance(curInstance);
    }

    void setVisitSynced(String visitId) {
        Realm curInstance = getQueryRealmInstance();
        VisitRealm mVisit = curInstance.where(VisitRealm.class).equalTo("id", visitId).findFirst();

        if (mVisit!=null && mVisit.isValid()) {
            curInstance.executeTransaction(db -> mVisit.setToSync(false));
        }

        closeQueryInstance(curInstance);

    }

    Observable<List<VisitRealm>> getCustomerVisits(String customerId) {
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<VisitRealm> qAll = curInstance
                .where(VisitRealm.class);
        if (customerId != null && !customerId.isEmpty()) {
            qAll.equalTo("customer.customerId", customerId);
        }
        qAll.sort("date", Sort.ASCENDING, "customer.name", Sort.ASCENDING);

        return getListObservable(curInstance, qAll);
    }


    //endregion ====================  Customer Visits  =========================


    //region =======================  Customer Discounts  =========================

    Float getCustomerDiscount(String customerId, String itemId) {
        if (customerId == null || customerId.isEmpty() || itemId == null || itemId.isEmpty()) {
            return 0f;
        }

        //try find discount for item
        Realm curInstance = getQueryRealmInstance();
        CustomerDiscountRealm discRealm = curInstance
                .where(CustomerDiscountRealm.class)
                .equalTo("customer.customerId", customerId)
                .equalTo("discountType", ConstantManager.DISCOUNT_TYPE_ITEM)
                .equalTo("item.itemId", itemId)
                .findFirst();
        ItemRealm item = curInstance.where(ItemRealm.class).equalTo("itemId", itemId).findFirst();
        if (discRealm == null && item != null && item.isValid() && item.getCategory()!= null){
            //no... try find discount for category
            discRealm = curInstance
                    .where(CustomerDiscountRealm.class)
                    .equalTo("customer.customerId", customerId)
                    .equalTo("discountType", ConstantManager.DISCOUNT_TYPE_CATEGORY)
                    .equalTo("category.categoryId", item.getCategory().getCategoryId())
                    .findFirst();
        }

        float discount = (discRealm == null ? 0f : discRealm.getPercent());

        closeQueryInstance(curInstance);

        return discount;
    }


    //endregion ====================  Customer Discounts  =========================

    //region =======================  Orders  =========================

    Observable<List<OrderRealm>> getCustomerOrders(String customerId) {
        if (customerId == null || customerId.isEmpty()) return Observable.empty();
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<OrderRealm> qAll = curInstance
                .where(OrderRealm.class)
                .equalTo("customer.customerId", customerId)
                .sort("status", Sort.ASCENDING, "date", Sort.DESCENDING)
               ;
        return getListObservable(curInstance, qAll);
    }

    Observable<List<OrderRealm>> getAllOrders() {
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<OrderRealm> qAll = curInstance
                .where(OrderRealm.class)
                .notEqualTo("status", ConstantManager.ORDER_STATUS_CART)
                .sort("status", Sort.ASCENDING, "date", Sort.DESCENDING)
                ;
        return getListObservable(curInstance, qAll);
    }

    void updateOrderExternalId(String orderId, String newId) {
        if (orderId == null || orderId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        OrderRealm order = curInstance
                .where(OrderRealm.class)
                .equalTo("id", orderId)
                .findFirst();
        if (order != null) {
            curInstance.executeTransaction(db -> {
                        order.setExternalId(newId);
                        order.setStatus(ConstantManager.ORDER_STATUS_SENT);
                    }
            );
        }
        closeQueryInstance(curInstance);
    }


    void updateOrderItemPrice(String orderId, String itemId, Float value) {
        if (orderId == null || orderId.isEmpty() || itemId == null || itemId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        OrderLineRealm line = curInstance
                .where(OrderLineRealm.class)
                .equalTo("order.id", orderId)
                .equalTo("item.itemId", itemId)
                .findFirst();
        if (line != null) {
            curInstance.executeTransaction(db -> line.setPrice(value));
        }
        closeQueryInstance(curInstance);
    }

    void updateOrderItemQty(String orderId, String itemId, Float value) {
        if (orderId == null || orderId.isEmpty() || itemId == null || itemId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        OrderLineRealm line = curInstance
                .where(OrderLineRealm.class)
                .equalTo("order.id", orderId)
                .equalTo("item.itemId", itemId)
                .findFirst();
        if (line != null) {
            curInstance.executeTransaction(db -> line.setQuantity(value));
        }
        closeQueryInstance(curInstance);
    }


    void removeOrderItem(String orderId, String itemId) {
        if (orderId == null || orderId.isEmpty() || itemId == null || itemId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        OrderLineRealm line = curInstance
                .where(OrderLineRealm.class)
                .equalTo("order.id", orderId)
                .equalTo("item.itemId", itemId)
                .findFirst();
        if (line != null) {
            if (isUIThread()) {
                line.removeAllChangeListeners();
            }
            curInstance.executeTransaction(db -> line.deleteFromRealm());
        }
        closeQueryInstance(curInstance);
    }

    Observable<List<OrderLineRealm>> getOrderLinesList(String orderId) {
        if (orderId == null || orderId.isEmpty()) return Observable.empty();
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<OrderLineRealm> qAll = curInstance
                .where(OrderLineRealm.class)
                .equalTo("order.id", orderId)
                .sort("item.artNumber");
        return getListObservable(curInstance, qAll);
    }

    List<OrderLineRealm> getOrderLines(String orderId) {
        if (orderId == null || orderId.isEmpty()) return new ArrayList<>();
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<OrderLineRealm> qAll = curInstance
                .where(OrderLineRealm.class)
                .equalTo("order.id", orderId)
                .sort("item.artNumber");
        List<OrderLineRealm> linesList = curInstance.copyFromRealm(qAll.findAll());
        closeQueryInstance(curInstance);
        return linesList;
    }

    void updateOrderStatus(String orderId, int orderStatus) {
        if (orderId == null || orderId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        OrderRealm order = getOrderById(orderId);
        curInstance.executeTransaction(db -> {
            order.setDate(order.getDate());
            order.setStatus(orderStatus);
        });
        closeQueryInstance(curInstance);
    }

    void clearOrderLines(String orderId) {
        if (orderId == null || orderId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        RealmResults<OrderLineRealm> orderLines = curInstance
                .where(OrderLineRealm.class)
                .equalTo("order.id", orderId)
                .findAll();
        if (!orderLines.isEmpty()){
            if (isUIThread()) {
                orderLines.removeAllChangeListeners();
            }
            curInstance.executeTransaction(db -> orderLines.deleteAllFromRealm());
        }
        closeQueryInstance(curInstance);
    }

    OrderRealm getCartForCustomer(String customerId) {
        if (customerId == null || customerId.isEmpty()) return null;
        Realm curInstance = getQueryRealmInstance();
        CustomerRealm customer = curInstance
                .where(CustomerRealm.class)
                .equalTo("customerId", customerId)
                .findFirst();
        if (customer == null || !customer.isValid()) {
            closeQueryInstance(curInstance);
            return null;
        }
        OrderRealm result = curInstance.where(OrderRealm.class)
                .equalTo("customer.customerId", customerId)
                .equalTo("status", ConstantManager.ORDER_STATUS_CART)
                .findFirst();
        if (result == null){
            CurrencyRealm defCurrency = getCurrencyById(ConstantManager.MAIN_CURRENCY_CODE);
            OrderRealm tmp = new OrderRealm(customer, defCurrency);
            if (tmp.getPriceList()==null) {
                tmp.setPriceList(getPriceListById(ConstantManager.PRICE_BASE_PRICE_ID));
            }
            curInstance.executeTransaction(db -> db.insertOrUpdate(tmp));
            result = getCartForCustomer(customerId);
        }else{
            if (customer.getPrice()!=null && (result.getPriceList() == null || !customer.getPrice().getPriceId().equals(result.getPriceList().getPriceId()))){
                OrderRealm tmp = curInstance.where(OrderRealm.class)
                        .equalTo("id", result.getId())
                        .findFirst();
                if (tmp!=null) {
                    curInstance.executeTransaction(db -> tmp.setPriceList(customer.getPrice()));
                }
            }
        }
        closeQueryInstance(curInstance);
        return result;
    }

    void updateOrderComment(String orderId, String comment) {
        if (orderId == null || orderId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        OrderRealm order = curInstance
                .where(OrderRealm.class)
                .equalTo("id", orderId)
                .findFirst();
        if (order != null && order.isValid()) {
            curInstance.executeTransaction(db -> order.setComments(comment));
        }
        closeQueryInstance(curInstance);
    }

    void updateOrderPayment(String orderId, int payment) {
        if (orderId == null || orderId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        OrderRealm order = curInstance
                .where(OrderRealm.class)
                .equalTo("id", orderId)
                .findFirst();
        if (order != null && order.isValid()) {
            curInstance.executeTransaction(db -> order.setPayment(payment));
        }

        closeQueryInstance(curInstance);
    }

    void addItemToCart(String orderId, String itemId, float newQty, float newPrice) {
        if (orderId == null || orderId.isEmpty() || itemId == null || itemId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        OrderLineRealm line = curInstance
                .where(OrderLineRealm.class)
                .equalTo("order.id", orderId)
                .equalTo("item.itemId", itemId)
                .findFirst();
        if (line == null){
            OrderRealm order = curInstance
                    .where(OrderRealm.class)
                    .equalTo("id", orderId)
                    .findFirst();
            ItemRealm item = curInstance
                    .where(ItemRealm.class)
                    .equalTo("itemId", itemId)
                    .findFirst();
            if (order != null && order.isValid() && item !=null && item.isValid()) {
                    OrderLineRealm tmpLine = new OrderLineRealm(order, item, newQty, newPrice);
                    curInstance.executeTransaction(db -> db.insertOrUpdate(tmpLine));
            }
        }else{
            curInstance.executeTransaction(db -> {
                line.setQuantity(line.getQuantity() + newQty);
                line.setPrice(newPrice);
            });
        }
        closeQueryInstance(curInstance);
    }

    OrderRealm getOrderById(String orderId) {
        if (orderId == null || orderId.isEmpty()) return null;
        Realm curInstance = getQueryRealmInstance();

        OrderRealm res = curInstance
                .where(OrderRealm.class)
                .equalTo("id", orderId)
                .findFirst();
        if (!isUIThread() && res!=null){
            res = curInstance.copyFromRealm(res);
        }

        closeQueryInstance(curInstance);

        return res;
    }


    void saveOrderToRealm(OrderRes orderRes, boolean createOnly) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Realm curInstance = getQueryRealmInstance();
        curInstance.refresh();


        RealmResults<OrderRealm> diffOrders = curInstance
                .where(OrderRealm.class)
                .equalTo("external_id", orderRes.getId())
                .notEqualTo("id", orderRes.getId())
                .findAll();
        RealmResults<OrderLineRealm> diffOrderLines = curInstance
                .where(OrderLineRealm.class)
                .equalTo("order.external_id", orderRes.getId())
                .notEqualTo("order.id", orderRes.getId())
                .findAll();

        CustomerRealm mCustomer = curInstance
                .where(CustomerRealm.class)
                .equalTo("customerId", orderRes.getCustomerId())
                .findFirst();

        if (mCustomer == null){
            closeQueryInstance(curInstance);
            return;
        }

        Date orderDate;
        Date deliveryDate;
        try{
            orderDate = sdf.parse(orderRes.getDate());
        } catch (ParseException e) {
            orderDate = Calendar.getInstance().getTime();
        }
        try{
            deliveryDate = sdf.parse(orderRes.getDelivery());
        } catch (ParseException e) {
            deliveryDate = orderDate;
        }

        OrderRealm newOrder = new OrderRealm(
                orderRes.getId(),
                mCustomer,
                orderDate,
                deliveryDate,
                (Boolean.parseBoolean(orderRes.getDelivered()) ? ConstantManager.ORDER_STATUS_DELIVERED : ConstantManager.ORDER_STATUS_SENT),
                (orderRes.getPayment().equalsIgnoreCase("cash") ? ConstantManager.ORDER_PAYMENT_CASH : ConstantManager.ORDER_PAYMENT_OFFICIAL),
                getCurrencyById(orderRes.getCurrency()),
                getTradeById(orderRes.getTradeId()),
                getPriceListById(orderRes.getPriceId()),
                orderRes.getComments()
        );


        if (createOnly){
            if (curInstance.where(OrderRealm.class).equalTo("id", orderRes.getId()).or().equalTo("external_id", orderRes.getId()).findFirst() == null) {
                curInstance.executeTransaction(db-> db.insertOrUpdate(newOrder));
            }
            closeQueryInstance(curInstance);
            return;
        }

        RealmList<OrderLineRealm> lines = new RealmList<>();
        if (orderRes.getLines() != null) {
            for (OrderRes.OrderLineRes line : orderRes.getLines()){
                ItemRealm mItem = curInstance
                        .where(ItemRealm.class)
                        .equalTo("itemId", line.getItemId())
                        .findFirst();
                if (mItem == null) {
                    mItem = new ItemRealm(line.getItemId(),line.getItemName(),line.getItemArticle(), null);
                }
                lines.add(new OrderLineRealm(newOrder, mItem, line.getQuantity(), line.getPrice()));
            }
        }

        diffOrders.removeAllChangeListeners();
        diffOrderLines.removeAllChangeListeners();
        curInstance.executeTransaction(db -> {
            diffOrders.deleteAllFromRealm();
            diffOrderLines.deleteAllFromRealm();
            db.insertOrUpdate(newOrder);
            db.insertOrUpdate(lines);
        });

        closeQueryInstance(curInstance);
    }



    void deleteOrderFromRealm(String orderId) {
        if (orderId == null || orderId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        OrderRealm tmpOrder = curInstance
                .where(OrderRealm.class)
                .equalTo("id",orderId)
                .or()
                .equalTo("external_id",orderId)
                .findFirst();
        if (tmpOrder == null) {
            closeQueryInstance(curInstance);
            return;
        }

        RealmResults<OrderLineRealm> lines = curInstance
                .where(OrderLineRealm.class)
                .equalTo("order.id", orderId)
                .or()
                .equalTo("order.external_id", orderId)
                .findAll();

        if (isUIThread()) {
            lines.removeAllChangeListeners();
            tmpOrder.removeAllChangeListeners();

        }
        curInstance.executeTransaction(db -> {
            lines.deleteAllFromRealm();
            tmpOrder.deleteFromRealm();
        });

        closeQueryInstance(curInstance);
    }


    Observable<OrderRealm> getOrdersToSend(String filter) {
        Realm curInst = getQueryRealmInstance();
        RealmQuery<OrderRealm> query = curInst
                .where(OrderRealm.class)
                .equalTo("status", ConstantManager.ORDER_STATUS_IN_PROGRESS);
        if (!filter.isEmpty()) {
            query = query.equalTo("id", filter);
        }
        List<OrderRealm> res = curInst.copyFromRealm(query.findAll());
        closeQueryInstance(curInst);
        return Observable.fromIterable(res);
    }


    void updateOrderCurrency(String orderId, String currencyId) {
        if (orderId == null || orderId.isEmpty() || currencyId == null || currencyId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        OrderRealm order = curInstance
                .where(OrderRealm.class)
                .equalTo("id", orderId)
                .findFirst();
        CurrencyRealm cur = curInstance
                .where(CurrencyRealm.class)
                .equalTo("currencyId", currencyId)
                .findFirst();
        if (order != null && order.isValid() && cur != null && cur.isValid()) {
            curInstance.executeTransaction(db -> order.setCurrency(cur));
        }

        closeQueryInstance(curInstance);
    }

    void updateOrderTrade(String orderId, String tradeId) {
        if (orderId == null || orderId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        OrderRealm order = curInstance
                .where(OrderRealm.class)
                .equalTo("id", orderId)
                .findFirst();
        TradeRealm trd = (tradeId == null || tradeId.isEmpty())?null:
                curInstance
                .where(TradeRealm.class)
                .equalTo("tradeId", tradeId)
                .findFirst();
        if (order != null && order.isValid()) {
            curInstance.executeTransaction(db -> order.setTrade(trd));
        }

        closeQueryInstance(curInstance);
    }

    void updateOrderFactFlag(String orderId, boolean fact) {
        if (orderId == null || orderId.isEmpty()) return;
        Realm curInstance = getQueryRealmInstance();
        OrderRealm order = curInstance
                .where(OrderRealm.class)
                .equalTo("id", orderId)
                .findFirst();
        if (order != null && order.isValid()) {
            curInstance.executeTransaction(db -> order.setPayOnFact(fact));
        }

        closeQueryInstance(curInstance);
    }

    //endregion ====================  Orders  =========================



    //region =======================  Item Groups  =========================

    Observable<List<GoodsGroupRealm>> getGroupList(final String parentId, String brand) {

        Realm curInstance = getQueryRealmInstance();

        RealmQuery<GoodsGroupRealm> resQuery;
        if (parentId == null || parentId.isEmpty()) {
            resQuery = curInstance
                    .where(GoodsGroupRealm.class)
                    .isNull("parent");
        } else {
            resQuery = curInstance
                    .where(GoodsGroupRealm.class)
                    .equalTo("parent.groupId", parentId);
        }

        if (brand != null && !brand.isEmpty()) {
            if (parentId == null || parentId.isEmpty()) {
                resQuery.equalTo("subGroups.items.brand.name", brand);
            }else {
                resQuery.equalTo("items.brand.name", brand);
            }
            resQuery.distinct("groupId");
        }

        resQuery.sort("name", Sort.ASCENDING);

        return getListObservable(curInstance, resQuery);
    }


    void saveGoodGroupToRealm(GoodGroupRes groupRes, boolean createOnly) {
        Realm curInstance = getQueryRealmInstance();

        if (createOnly){
            if (curInstance.where(GoodsGroupRealm.class).equalTo("groupId", groupRes.getId()).findFirst() == null) {
                curInstance.executeTransaction(db -> db.insertOrUpdate(new GoodsGroupRealm(groupRes.getId(), groupRes.getName())));
            }
            closeQueryInstance(curInstance);
            return;
        }

        GoodsGroupRealm mParent=null;
        if (groupRes.getParent() != null && !groupRes.getParent().isEmpty()) {
            mParent = curInstance
                    .where(GoodsGroupRealm.class)
                    .equalTo("groupId", groupRes.getParent())
                    .findFirst();
            if (mParent == null){
                curInstance.executeTransaction(db->db.insertOrUpdate(new GoodsGroupRealm(groupRes.getParent(),"no_name")));
                mParent = curInstance
                        .where(GoodsGroupRealm.class)
                        .equalTo("groupId", groupRes.getParent())
                        .findFirst();
            }
        }

        String mImageURL = "";
        if (groupRes.getImage() != null) {
            mImageURL = UiHelper.saveImageFromBase64(groupRes.getImage(), groupRes.getId());
        }


        GoodsGroupRealm newGroup = new GoodsGroupRealm(groupRes.getId(), groupRes.getName(), mParent, mImageURL);
        curInstance.executeTransaction(db -> db.insertOrUpdate(newGroup));
        closeQueryInstance(curInstance);
    }

    GoodsGroupRealm getGroupById(String groupId) {
        Realm curInstance = getQueryRealmInstance();
        GoodsGroupRealm result = curInstance
                .where(GoodsGroupRealm.class)
                .equalTo("groupId", groupId)
                .findFirst();
        if (result != null) {
            result = curInstance.copyFromRealm(result);
        }
        closeQueryInstance(curInstance);
        return result;
    }

    void deleteGoodsGroupFromRealm(String id) {
        if (id == null || id.isEmpty()) return;

        Realm curInstance = getQueryRealmInstance();

        List<GoodsGroupRealm> subs = curInstance.copyFromRealm(curInstance.where(GoodsGroupRealm.class).equalTo("parent.groupId", id).findAll());
        for (GoodsGroupRealm sub : subs){
            deleteGoodsGroupFromRealm(sub.getGroupId());
        }

        if (curInstance.isClosed()) {
            curInstance = getQueryRealmInstance();
        }

        List<ItemRealm> items = curInstance.copyFromRealm(curInstance.where(ItemRealm.class).equalTo("group.groupId", id).findAll());
        for (ItemRealm item : items){
            deleteGoodItemFromRealm(item.getItemId());
        }

        if (curInstance.isClosed()) {
            curInstance = getQueryRealmInstance();
        }

        GoodsGroupRealm group = curInstance.where(GoodsGroupRealm.class).equalTo("groupId", id).findFirst();
        if (group != null && group.isValid()) {
            if (isUIThread()) {
                group.removeAllChangeListeners();
            }
            curInstance.executeTransaction(db -> group.deleteFromRealm());
        }

        closeQueryInstance(curInstance);
    }



    //endregion ====================  Item Groups  =========================


    //region =======================  Items  =========================

    Observable<List<ItemRealm>> getItemList(String parentId, String filter, String brand, String categoryId) {
        Realm curInstance = getQueryRealmInstance();

        RealmQuery<ItemRealm> resQuery = curInstance.where(ItemRealm.class);
        if (parentId != null && !parentId.isEmpty()) {
            GoodsGroupRealm parent = getGroupById(parentId);
            if(parent.getParent() == null){
                resQuery.equalTo("group.parent.groupId", parentId);
            }else{
                resQuery.equalTo("group.groupId", parentId);
            }
        }
        if (filter != null && !filter.isEmpty()){
            resQuery.contains("index", filter.toLowerCase());
        }
        if (brand != null && !brand.isEmpty()){
            resQuery.equalTo("brand.name", brand);
        }
        if (categoryId != null && !categoryId.isEmpty()){
            resQuery.equalTo("category.categoryId", categoryId);
        }
        resQuery.isNotNull("group");
        resQuery.sort("name", Sort.ASCENDING);


        return getListObservable(curInstance, resQuery);
    }

    void saveGoodItemToRealm(GoodItemRes goodItemRes, boolean createOnly) {
        Realm curInstance = getQueryRealmInstance();
        curInstance.refresh();

        if (createOnly){
            if (curInstance.where(ItemRealm.class).equalTo("itemId", goodItemRes.getId()).findFirst() == null) {
                curInstance.executeTransaction(db -> db.insertOrUpdate(new ItemRealm(goodItemRes.getId(), goodItemRes.getName(), goodItemRes.getName(), null)));
            }
            closeQueryInstance(curInstance);
            return;
        }


        GoodsGroupRealm mParent=null;
        if (goodItemRes.getGroupId() != null && !goodItemRes.getGroupId().isEmpty()) {
            mParent = curInstance
                    .where(GoodsGroupRealm.class)
                    .equalTo("groupId", goodItemRes.getGroupId())
                    .findFirst();
            if (mParent == null){
                curInstance.executeTransaction(db->db.insertOrUpdate(new GoodsGroupRealm(goodItemRes.getGroupId(),"no_name",null,null)));
                mParent = curInstance
                        .where(GoodsGroupRealm.class)
                        .equalTo("groupId", goodItemRes.getGroupId())
                        .findFirst();
            }
        }

        BrandsRealm mBrand = null;
        if (goodItemRes.getBrand() != null && goodItemRes.getBrand().getId() != null && !goodItemRes.getBrand().getId().isEmpty()) {
            mBrand = curInstance
                    .where(BrandsRealm.class)
                    .equalTo("brandId", goodItemRes.getBrand().getId())
                    .findFirst();
            if (mBrand == null){
                curInstance.executeTransaction(db->db.insertOrUpdate(new BrandsRealm(goodItemRes.getBrand().getId(),goodItemRes.getBrand().getName(),null)));
                mBrand = curInstance
                        .where(BrandsRealm.class)
                        .equalTo("brandId", goodItemRes.getBrand().getId())
                        .findFirst();
            }
        }

        GoodsCategoryRealm mCat = null;
        if (goodItemRes.getCategory() != null && goodItemRes.getCategory().getId() != null && !goodItemRes.getCategory().getId().isEmpty()) {
            mCat = curInstance
                    .where(GoodsCategoryRealm.class)
                    .equalTo("categoryId", goodItemRes.getCategory().getId())
                    .findFirst();
            if (mCat == null){
                curInstance.executeTransaction(db->db.insertOrUpdate(new GoodsCategoryRealm(goodItemRes.getCategory().getId(),goodItemRes.getCategory().getName(),null)));
                mCat = curInstance
                        .where(GoodsCategoryRealm.class)
                        .equalTo("categoryId", goodItemRes.getCategory().getId())
                        .findFirst();
            }
        }

        ItemRealm  newItem = new ItemRealm(
                goodItemRes.getId(),
                goodItemRes.getName(),
                goodItemRes.getArticle(),
                goodItemRes.getRest() != null ? goodItemRes.getRest().getStore() : 0f,
                goodItemRes.getRest() != null ? goodItemRes.getRest().getDistribution() : 0f,
                goodItemRes.getRest() != null ? goodItemRes.getRest().getOfficial() : 0f,
                mCat,
                mParent,
                mBrand
        );

        Map<String, PriceListRealm> prices = new ArrayMap<>();
        List<PriceListItemRealm> itemPrices= new ArrayList<>();
        for (GoodItemRes.ItemPrice itemPrice: goodItemRes.getPriceList()) {
            PriceListRealm curPrice = prices.get(itemPrice.getPriceId());
            if (curPrice == null){
                curPrice = new PriceListRealm(itemPrice.getPriceId(), itemPrice.getPriceName());
                prices.put(itemPrice.getPriceId(), curPrice);
            }

            itemPrices.add(new PriceListItemRealm(newItem, curPrice, getCurrencyById(itemPrice.getCurrency()), itemPrice.getPrice()));
        }


        curInstance.executeTransaction(db -> {
            db.insertOrUpdate(prices.values());
            db.insertOrUpdate(itemPrices);
            db.insertOrUpdate(newItem);
        });
        closeQueryInstance(curInstance);

    }


    ItemRealm getItemById(String itemId) {
        if (itemId == null || itemId.isEmpty()) return null;

        Realm curInstance = getQueryRealmInstance();
        ItemRealm result = curInstance.where(ItemRealm.class).equalTo("itemId", itemId).findFirst();
        if (result != null && isUIThread()) {
            result = curInstance.copyFromRealm(result);
        }
        closeQueryInstance(curInstance);
        return result;
    }

    Observable<List<BrandsRealm>> getAllBrands() {
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<BrandsRealm> qAll = curInstance.where(BrandsRealm.class);
        return getListObservable(curInstance, qAll);
    }


    void deleteGoodItemFromRealm(String id) {
        if (id == null || id.isEmpty()) return;

        Realm curInstance = getQueryRealmInstance();

        boolean safeDelete = (curInstance.where(CustomerDiscountRealm.class).equalTo("item.itemId", id).findFirst() == null)
                           && (curInstance.where(OrderLineRealm.class).equalTo("item.itemId", id).findFirst() == null)
                           && (curInstance.where(PriceListItemRealm.class).equalTo("item.itemId", id).findFirst() == null);

        ItemRealm item = curInstance.where(ItemRealm.class).equalTo("itemId", id).findFirst();
        if (item!=null && item.isValid()) {
            if (safeDelete) {
                if (isUIThread()) {
                    item.removeAllChangeListeners();
                    curInstance.executeTransaction(db -> item.deleteFromRealm());
                }else {
                    curInstance.executeTransaction(db -> item.deleteFromRealm());
                }
            } else {
                curInstance.executeTransaction(db -> item.setGroup(null));
            }
        }
        closeQueryInstance(curInstance);
    }


    GoodsCategoryRealm getGoodsCategoryById(String categoryId) {
        if (categoryId == null || categoryId.isEmpty()) return null;

        Realm curInstance = getQueryRealmInstance();
        GoodsCategoryRealm result = curInstance.where(GoodsCategoryRealm.class).equalTo("categoryId", categoryId).findFirst();
        if (result != null && isUIThread()) {
            result = curInstance.copyFromRealm(result);
        }
        closeQueryInstance(curInstance);
        return result;
    }

    //endregion ====================  Items  =========================


    //region =======================  Prices  =========================

    void saveCurrencyToRealm(CurrencyRes currencyRes) {
        Realm curInstance = getQueryRealmInstance();
        curInstance.refresh();

        CurrencyRealm newItem = new CurrencyRealm(
                currencyRes.getId(),
                currencyRes.getCurrency(),
                currencyRes.getRate()
        );
        curInstance.executeTransaction(db -> db.insertOrUpdate(newItem));
        closeQueryInstance(curInstance);
    }

    CurrencyRealm getCurrencyById(String currencyId) {
        if (currencyId == null || currencyId.isEmpty()) return null;

        Realm curInstance = getQueryRealmInstance();
        CurrencyRealm result = curInstance.where(CurrencyRealm.class).equalTo("currencyId", currencyId).findFirst();
        if (result != null && isUIThread()) {
            result = curInstance.copyFromRealm(result);
        }
        closeQueryInstance(curInstance);
        return result;
    }

    CurrencyRealm getCurrencyByName(String name) {
        if (name == null || name.isEmpty()) return null;

        Realm curInstance = getQueryRealmInstance();
        CurrencyRealm result = curInstance.where(CurrencyRealm.class).equalTo("name", name).findFirst();
        if (result != null && isUIThread()) {
            result = curInstance.copyFromRealm(result);
        }
        closeQueryInstance(curInstance);
        return result;
    }

    void saveTradeToRealm(TradesRes tradeRes) {
        Realm curInstance = getQueryRealmInstance();
        curInstance.refresh();

        TradeRealm newItem = new TradeRealm(
                tradeRes.getId(),
                tradeRes.getName(),
                tradeRes.isCash(),
                tradeRes.isFact(),
                tradeRes.isRemote()

        );

        RealmResults<TradeCategoryRealm> oldCategories = curInstance.where(TradeCategoryRealm.class).equalTo("trade.tradeId", tradeRes.getId()).findAll();

        List<TradeCategoryRealm> newCategories = new ArrayList<>();

        for (TradesRes.CategoryPercent tradeCat : tradeRes.getPercents()){
            GoodsCategoryRealm cat = tradeCat.getCategoryId()==null?null:getGoodsCategoryById(tradeCat.getCategoryId());
            newCategories.add(new TradeCategoryRealm(newItem, cat, tradeCat.getPercent()));
        }

        curInstance.executeTransaction(db -> {
            oldCategories.deleteAllFromRealm();
            db.insertOrUpdate(newItem);
            db.insertOrUpdate(newCategories);
        });
        closeQueryInstance(curInstance);
    }

    TradeRealm getTradeById(String tradeId) {
        if (tradeId == null || tradeId.isEmpty()) return null;

        Realm curInstance = getQueryRealmInstance();
        TradeRealm result = curInstance.where(TradeRealm.class).equalTo("tradeId", tradeId).findFirst();
        if (result != null && isUIThread()) {
            result = curInstance.copyFromRealm(result);
        }
        closeQueryInstance(curInstance);
        return result;
    }

    float getTradePercent(String tradeId, String categoryId){

        if (tradeId == null || tradeId.isEmpty()) return 0;

        Realm curInstance = getQueryRealmInstance();

        RealmQuery<TradeCategoryRealm> tradeCategoryQuery = curInstance.where(TradeCategoryRealm.class).equalTo("trade.tradeId", tradeId);
        TradeCategoryRealm tradeCategory;
        if (categoryId == null || categoryId.isEmpty()) {
            tradeCategory = tradeCategoryQuery.isNull("category").findFirst();
        }else{
            tradeCategory = tradeCategoryQuery.equalTo("category.categoryId", categoryId).findFirst();
        }
        float result;
        if (tradeCategory == null){
            if (categoryId == null || categoryId.isEmpty()){
                result = 0f;
            }else{
                result = getTradePercent(tradeId, null);
            }
        }else{
            result = tradeCategory.getPercent();
        }

        closeQueryInstance(curInstance);

        return result;
    }

    PriceListRealm getPriceListById(String priceId){
        if (priceId == null || priceId.isEmpty()) return null;

        Realm curInstance = getQueryRealmInstance();
        PriceListRealm result = curInstance.where(PriceListRealm.class).equalTo("priceId", priceId).findFirst();
        if (result != null && isUIThread()) {
            result = curInstance.copyFromRealm(result);
        }
        closeQueryInstance(curInstance);
        return result;
    }

    Observable<List<PriceListRealm>> getAllPriceLists(){
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<PriceListRealm> qAll = curInstance.where(PriceListRealm.class).not().beginsWith("name", "#").sort("name", Sort.ASCENDING);
        return getListObservable(curInstance, qAll);
    }

    Observable<List<TradeRealm>> getAllTrades(@Nullable Boolean cash, @Nullable Boolean fact, @Nullable Boolean remote){
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<TradeRealm> qAll = curInstance.where(TradeRealm.class).sort("name", Sort.ASCENDING);
        if (cash != null){
            qAll.equalTo("cash", cash);
        }
        if (fact != null){
            qAll.equalTo("fact", fact);
        }
        if (remote!= null){
            qAll.equalTo("remote", remote);
        }
        return getListObservable(curInstance, qAll);
    }

    Observable<List<CurrencyRealm>> getAllCurrencies() {
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<CurrencyRealm> qAll = curInstance.where(CurrencyRealm.class).sort("currencyId", Sort.ASCENDING);
        return getListObservable(curInstance, qAll);
    }

    TradeRealm getTradeByName(String tradeName) {
        if (tradeName == null || tradeName.isEmpty()) return null;

        Realm curInstance = getQueryRealmInstance();
        TradeRealm result = curInstance.where(TradeRealm.class).equalTo("name", tradeName).findFirst();
        if (result != null && isUIThread()) {
            result = curInstance.copyFromRealm(result);
        }
        closeQueryInstance(curInstance);
        return result;
    }

    PriceListRealm getPriceByName(String priceName) {
        if (priceName == null || priceName.isEmpty()) return null;

        Realm curInstance = getQueryRealmInstance();
        PriceListRealm result = curInstance.where(PriceListRealm.class).equalTo("name", priceName).findFirst();
        if (result != null && isUIThread()) {
            result = curInstance.copyFromRealm(result);
        }
        closeQueryInstance(curInstance);
        return result;
    }

    PriceListItemRealm getPriceListItem(String itemId, String priceId) {
        if (itemId == null || itemId.isEmpty() || priceId == null || priceId.isEmpty()) return null;

        Realm curInstance = getQueryRealmInstance();
        PriceListItemRealm result = curInstance
                .where(PriceListItemRealm.class)
                .equalTo("item.itemId", itemId)
                .equalTo("priceList.priceId", priceId)
                .findFirst();
        if (result != null && isUIThread()) {
            result = curInstance.copyFromRealm(result);
        }
        closeQueryInstance(curInstance);
        return result;
    }

    TradeRealm getFactTradeForTrade(String tradeId) {
        if (tradeId == null || tradeId.isEmpty()) return null;

        Realm curInstance = getQueryRealmInstance();
        TradeRealm curTrade = curInstance
                .where(TradeRealm.class)
                .equalTo("tradeId", tradeId)
                .findFirst();
        TradeRealm result;
        if (curTrade != null){
            if (curTrade.isFact()) {
                result = curTrade;
            } else {
                result = curInstance
                        .where(TradeRealm.class)
                        .equalTo("cash", curTrade.isCash())
                        .equalTo("fact", true)
                        .equalTo("remote", curTrade.isRemote())
                        .findFirst();
            }
            if (result != null && isUIThread()) {
                result = curInstance.copyFromRealm(result);
            }

        }else{
            result = null;
        }
        closeQueryInstance(curInstance);
        return result;
    }

    Pair<CurrencyRealm, Float> getCustomerPrice(String customerId, String itemId) {
        if (customerId == null || customerId.isEmpty() || itemId == null || itemId.isEmpty()) return null;

        Realm curInstance = getQueryRealmInstance();
        PriceListItemRealm item = curInstance
                .where(PriceListItemRealm.class)
                .equalTo("item.itemId", itemId)
                .equalTo("priceList.priceId", customerId)
                .findFirst();
        float price = 0f;
        CurrencyRealm currency = null;
        if (item != null && item.isValid() && item.getPrice()>0) {
            price = item.getPrice();
            currency = item.getCurrency();
        }
        closeQueryInstance(curInstance);
        return new Pair<>(currency, price);
    }


    //endregion ====================  Prices  =========================



}



