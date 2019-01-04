package com.sokolua.manager.data.managers;

import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;

import com.sokolua.manager.data.network.res.CustomerDiscountRes;
import com.sokolua.manager.data.network.res.CustomerRes;
import com.sokolua.manager.data.network.res.DebtRes;
import com.sokolua.manager.data.network.res.GoodGroupRes;
import com.sokolua.manager.data.network.res.GoodItemRes;
import com.sokolua.manager.data.network.res.NoteRes;
import com.sokolua.manager.data.network.res.OrderLineRes;
import com.sokolua.manager.data.network.res.OrderPlanRes;
import com.sokolua.manager.data.network.res.OrderRes;
import com.sokolua.manager.data.network.res.TaskRes;
import com.sokolua.manager.data.network.res.VisitRes;
import com.sokolua.manager.data.storage.realm.BrandsRealm;
import com.sokolua.manager.data.storage.realm.CustomerDiscountRealm;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.GoodsCategoryRealm;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderPlanRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;
import com.sokolua.manager.data.storage.realm.VisitRealm;
import com.sokolua.manager.utils.UiHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

public class RealmManager {

    //region =======================  Realm instance  =========================


    private LongSparseArray<Realm> mRealmInstance = new LongSparseArray<>();

    private Realm getQueryRealmInstance() {
        Realm currentRealm = mRealmInstance.get(Thread.currentThread().getId());
        if (currentRealm == null || currentRealm.isClosed()) {
            currentRealm = Realm.getDefaultInstance();
            Realm.compactRealm(currentRealm.getConfiguration());
        }
        currentRealm.refresh();
        mRealmInstance.put(Thread.currentThread().getId(), currentRealm);

        return currentRealm;
    }

    private void closeQueryInstance(Realm instance){
        if (!isUIThread()){
            instance.close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        for (int i = 0; i < mRealmInstance.size(); i++) {
            Realm inst = mRealmInstance.valueAt(i);
            if (inst != null && inst.isClosed()) {
                inst.close();
            }
        }
        if (Realm.getDefaultConfiguration() != null) {
            Realm.compactRealm(Realm.getDefaultConfiguration());
        }

        super.finalize();
    }

    //endregion ====================  Realm instance  =========================


    //region =======================  Service  =========================


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
        inst.removeAllChangeListeners();
        inst.executeTransaction(db-> db.deleteAll());
        closeQueryInstance(inst);
    }


    //endregion ====================  Service  =========================


    //region =======================  Customers  =========================

    Observable<List<CustomerRealm>> getCustomersList(String filter){

        Realm curInstance = getQueryRealmInstance();
        RealmQuery<CustomerRealm> customersQuery = curInstance
                .where(CustomerRealm.class);
        if (filter != null && !filter.isEmpty()) {
            customersQuery.contains("index", filter.toLowerCase(), Case.INSENSITIVE); //Ищем по индексному полю - пока индекс = наименование
        }
        customersQuery.sort("name");


        return getListObservable(curInstance, customersQuery);
    }

    @Nullable
    CustomerRealm getCustomerById(String id) {
        Realm curInstance = getQueryRealmInstance();
        CustomerRealm result = curInstance
                .where(CustomerRealm.class)
                .equalTo("customerId", id)
                .findFirst();
        if (result != null && !isUIThread()) {
            result = curInstance.copyFromRealm(result);
        }
        closeQueryInstance(curInstance);
        return result;
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
        curInstance.refresh();

        CustomerRealm newCust =  new CustomerRealm(
                customerRes.getId(),
                customerRes.getName(),
                customerRes.getContactName(),
                customerRes.getAddress(),
                customerRes.getPhone(),
                customerRes.getEmail(),
                customerRes.getCategory()
        );

        if (createOnly){
            if (curInstance.where(CustomerRealm.class).equalTo("customerId", customerRes.getId()).findFirst() == null) {
                curInstance.executeTransaction(db->db.insertOrUpdate(newCust));
            }
            closeQueryInstance(curInstance);
            return;
        }



        RealmList<DebtRealm> mDebt = new RealmList<>();
        if (customerRes.getDebt() != null) {
            for (DebtRes debt : customerRes.getDebt()){
                mDebt.add(new DebtRealm(newCust, debt.getCurrency(), debt.getAmount(), debt.getAmountUSD(), debt.isOutdated()));
            }
        }

        RealmList<NoteRealm> mNotes = new RealmList<>();
        if (customerRes.getNotes() != null) {
            for (NoteRes note : customerRes.getNotes()){
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
            for (TaskRes task : customerRes.getTasks()){
                mTasks.add(new TaskRealm(newCust, task.getId(), task.getText(), task.getType()));
            }
        }

        RealmList<OrderPlanRealm> mPlan = new RealmList<>();
        RealmList<GoodsCategoryRealm> mCats = new RealmList<>();
        if (customerRes.getPlan() != null) {
            for (OrderPlanRes plan : customerRes.getPlan()){
                GoodsCategoryRealm cat = curInstance.where(GoodsCategoryRealm.class).equalTo("categoryId", plan.getCategoryId()).findFirst();
                if (cat == null){
                    cat = new GoodsCategoryRealm(plan.getCategoryId(), plan.getCategoryName(), "");
                    mCats.add(cat);
                }
                mPlan.add(new OrderPlanRealm(newCust, cat, plan.getAmount()));
            }
        }

        RealmList<CustomerDiscountRealm> mDisc = new RealmList<>();
        RealmList<ItemRealm> mItems = new RealmList<>();
        if (customerRes.getDiscounts() != null) {
            for (CustomerDiscountRes disc : customerRes.getDiscounts()){
                if (disc.getType() == ConstantManager.DISCOUNT_TYPE_ITEM){
                    ItemRealm item = curInstance.where(ItemRealm.class).equalTo("itemId", disc.getTargetId()).findFirst();
                    if (item == null){
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

                }else{
                    GoodsCategoryRealm cat = curInstance.where(GoodsCategoryRealm.class).equalTo("categoryId", disc.getTargetId()).findFirst();
                    if (cat == null){
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
            for (VisitRes visit : customerRes.getVisits()){
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

        curInstance.executeTransaction(db -> {
            db.insertOrUpdate(newCust);
            oldTasks.deleteAllFromRealm();
            oldDebt.deleteAllFromRealm();
            oldPlans.deleteAllFromRealm();
            oldNotes.deleteAllFromRealm();
            oldDisc.deleteAllFromRealm();
            oldVisits.deleteAllFromRealm();

            db.insertOrUpdate(mDebt);
            db.insertOrUpdate(mNotes);
            db.insertOrUpdate(mTasks);
            db.insertOrUpdate(mPlan);
            db.insertOrUpdate(mDisc);
            db.insertOrUpdate(mVisits);
        });
        closeQueryInstance(curInstance);
    }


    void deleteCustomerFromRealm(String id) {
        Realm curInstance = getQueryRealmInstance();

        CustomerRealm mCustomer = curInstance
                .where(CustomerRealm.class)
                .equalTo("customerId", id)
                .findFirst();
        if (mCustomer == null) {
            closeQueryInstance(curInstance);
            return;
        }

        List<OrderRealm> orders = curInstance.copyFromRealm(curInstance.where(OrderRealm.class).equalTo("customer.customerId", id).findAll());

        for (OrderRealm order : orders){
            deleteOrderFromRealm(order.getId());
        }

        curInstance = getQueryRealmInstance();
        RealmResults<TaskRealm> oldTasks = curInstance.where(TaskRealm.class).equalTo("customer.customerId", id).findAll();
        RealmResults<OrderPlanRealm> oldPlans = curInstance.where(OrderPlanRealm.class).equalTo("customer.customerId", id).findAll();
        RealmResults<DebtRealm> oldDebt = curInstance.where(DebtRealm.class).equalTo("customer.customerId", id).findAll();
        RealmResults<NoteRealm> oldNotes = curInstance.where(NoteRealm.class).equalTo("customer.customerId", id).findAll();
        RealmResults<CustomerDiscountRealm> oldDisc = curInstance.where(CustomerDiscountRealm.class).equalTo("customer.customerId", id).findAll();
        RealmResults<VisitRealm> oldVisits = curInstance.where(VisitRealm.class).equalTo("customer.customerId", id).findAll();
        curInstance.executeTransaction(db -> {
            mCustomer.deleteFromRealm();
            oldTasks.deleteAllFromRealm();
            oldDebt.deleteAllFromRealm();
            oldPlans.deleteAllFromRealm();
            oldNotes.deleteAllFromRealm();
            oldDisc.deleteAllFromRealm();
            oldVisits.deleteAllFromRealm();
        });

        closeQueryInstance(curInstance);

    }


    //endregion ====================  Customers  =========================



    //region =======================  Customer Debt  =========================


    int getCustomerDebtType(String customerId) {
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
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<DebtRealm> qAll = curInstance
                .where(DebtRealm.class)
                .equalTo("customer.customerId", customerId);
        qAll.sort("currency");

        return getListObservable(curInstance, qAll);
    }



    Observable<List<DebtRealm>> getCustomerDebtByType(String customerId, int debtType) {
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
                .where(NoteRealm.class)
                .equalTo("customer.customerId", customerId);
        qAll.sort("date", Sort.DESCENDING);
        return getListObservable(curInstance, qAll);
    }

    void addNewNote(String customerId, String note) {
        Realm curInstance = getQueryRealmInstance();
        CustomerRealm cust = getCustomerById(customerId);
        if (cust != null) {
            curInstance.executeTransaction(db->db.insertOrUpdate(new NoteRealm(cust, note)));
        }
        closeQueryInstance(curInstance);
    }

    void deleteNote(String noteId) {
        Realm curInstance = getQueryRealmInstance();
        NoteRealm tmp = curInstance
                .where(NoteRealm.class)
                .equalTo("noteId", noteId)
                .findFirst();
        if (tmp != null) {
            tmp.removeAllChangeListeners();
            curInstance.executeTransaction(db->tmp.deleteFromRealm());
        }
        closeQueryInstance(curInstance);
    }

    @Nullable
    NoteRealm getCustomerNoteById(String mId) {
        return getQueryRealmInstance().where(NoteRealm.class).equalTo("noteId", mId).findFirst();
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
        Realm curInstance = getQueryRealmInstance();
        NoteRealm note = curInstance.where(NoteRealm.class).equalTo("noteId", noteId).findFirst();
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
                .where(TaskRealm.class)
                .equalTo("customer.customerId", customerId);
        qAll.sort("taskType");

        return getListObservable(curInstance, qAll);
    }

    Observable<List<TaskRealm>> getCustomerTaskByType(String customerId, int taskType) {
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<TaskRealm> qAll = curInstance
                .where(TaskRealm.class)
                .equalTo("customer.customerId", customerId)
                .equalTo("taskType", taskType);
        qAll.sort("done",Sort.ASCENDING, "text", Sort.ASCENDING);
        return getListObservable(curInstance, qAll);
    }

    void updateCustomerTask(String taskId, boolean checked, String result) {
        Realm curInstance = getQueryRealmInstance();
        TaskRealm task = curInstance
                .where(TaskRealm.class)
                .equalTo("taskId", taskId).findFirst();
        if (task != null && task.isLoaded() && task.isValid()) {
            curInstance.executeTransaction(db -> {
                task.setDone(checked);
                task.setResult(result);
            });
        }
        closeQueryInstance(curInstance);
    }

    //endregion ====================  Customer Tasks  =========================


    //region =======================  Customer Plan  =========================

    Observable<List<OrderPlanRealm>> getCustomerPlan(String customerId) {
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

    //endregion ====================  Customer Visits  =========================


    //region =======================  Customer Discounts  =========================

    Float getCustomerDiscount(String customerId, String itemId) {
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
        Realm curInstance = getQueryRealmInstance();
        OrderRealm order = getOrderById(orderId);
        curInstance.executeTransaction(db ->{
                order.setExternalId(newId);
                order.setStatus(ConstantManager.ORDER_STATUS_SENT);
            }
        );
        closeQueryInstance(curInstance);
    }


    void updateOrderItemPrice(String orderId, String itemId, Float value) {
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
        OrderLineRealm line = getQueryRealmInstance()
                .where(OrderLineRealm.class)
                .equalTo("order.id", orderId)
                .equalTo("item.itemId", itemId)
                .findFirst();
        if (line != null) {
            line.removeAllChangeListeners();
            getQueryRealmInstance().executeTransaction(db -> line.deleteFromRealm());
        }
    }

    Observable<List<OrderLineRealm>> getOrderLinesList(String orderId) {
        Realm curInstance = getQueryRealmInstance();
        RealmQuery<OrderLineRealm> qAll = curInstance
                .where(OrderLineRealm.class)
                .equalTo("order.id", orderId)
                .sort("item.artNumber");
        return getListObservable(curInstance, qAll);
    }

    void updateOrderStatus(String orderId, int orderStatus) {
        Realm curInstance = getQueryRealmInstance();
        OrderRealm order = getOrderById(orderId);
        curInstance.executeTransaction(db -> order.setStatus(orderStatus));
        closeQueryInstance(curInstance);
    }

    void clearOrderLines(String orderId) {
        Realm curInstance = getQueryRealmInstance();
        OrderRealm order = getOrderById(orderId);
        if (order!=null && !order.getLines().isEmpty()){
            //order.getLines().removeAllChangeListeners();
            curInstance.executeTransaction(db -> order.getLines().deleteAllFromRealm());
        }
        closeQueryInstance(curInstance);
    }

    OrderRealm getCartForCustomer(String customerId) {
        Realm curInstance = getQueryRealmInstance();
        OrderRealm result = curInstance.where(OrderRealm.class)
                .equalTo("customer.customerId", customerId)
                .equalTo("status", ConstantManager.ORDER_STATUS_CART)
                .findFirst();
        if (result == null){
            CustomerRealm customer = getCustomerById(customerId);
            if (customer != null) {
                OrderRealm tmp = new OrderRealm(customer);
                curInstance.executeTransaction(db -> db.insertOrUpdate(tmp));
                result = getCartForCustomer(customerId);
            }
        }
        return result;
    }

    void updateOrderComment(String orderId, String comment) {
        Realm curInstance = getQueryRealmInstance();
        OrderRealm order = getOrderById(orderId);
        curInstance.executeTransaction(db -> order.setComments(comment));
        closeQueryInstance(curInstance);
    }

    void updateOrderPayment(String orderId, int payment) {
        Realm curInstance = getQueryRealmInstance();
        OrderRealm order = getOrderById(orderId);
        curInstance.executeTransaction(db -> order.setPayment(payment));
        closeQueryInstance(curInstance);
    }

    void addItemToCart(String orderId, String itemId, float newQty, float newPrice) {
        Realm curInstance = getQueryRealmInstance();
        OrderLineRealm line = curInstance
                .where(OrderLineRealm.class)
                .equalTo("order.id", orderId)
                .equalTo("item.itemId", itemId)
                .findFirst();
        if (line == null){
            OrderRealm order = getOrderById(orderId);
            ItemRealm item = getItemById(itemId);
            if (order != null && item != null) {
                OrderLineRealm tmpLine = new OrderLineRealm(order, item, newQty, newPrice);
                curInstance.executeTransaction(db->db.insertOrUpdate(tmpLine));
            }
        }else{
            curInstance.executeTransaction(db->{
                line.setQuantity(line.getQuantity()+newQty);
                line.setPrice(newPrice);
            });
        }
        closeQueryInstance(curInstance);
    }

    OrderRealm getOrderById(String orderId) {
        Realm curInstance = getQueryRealmInstance();

        OrderRealm res = curInstance
                .where(OrderRealm.class)
                .equalTo("id", orderId)
                .findFirst();
        if (!isUIThread() && res!=null){
            res = curInstance.copyFromRealm(res);
            closeQueryInstance(curInstance);
        }

        return res;
    }


    void saveOrderToRealm(OrderRes orderRes, boolean createOnly) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Realm curInstance = getQueryRealmInstance();
        curInstance.refresh();


        RealmResults<OrderRealm> diffOrders = curInstance.where(OrderRealm.class).equalTo("external_id", orderRes.getId()).notEqualTo("id", orderRes.getId()).findAll();
        RealmResults<OrderLineRealm> diffOrderLines = curInstance.where(OrderLineRealm.class).equalTo("order.external_id", orderRes.getId()).notEqualTo("order.id", orderRes.getId()).findAll();

        CustomerRealm mCustomer = curInstance.where(CustomerRealm.class).equalTo("customerId", orderRes.getCustomerId()).findFirst();
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
                orderRes.getCurrency(),
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
            for (OrderLineRes line : orderRes.getLines()){
                ItemRealm mItem = curInstance.where(ItemRealm.class).equalTo("itemId", line.getItemId()).findFirst();
                if (mItem == null) {
                    mItem = new ItemRealm(line.getItemId(),line.getItemName(),line.getItemArticle(), null);
                }
                lines.add(new OrderLineRealm(newOrder, mItem, line.getQuantity(), line.getPrice()));
            }
        }

        diffOrders.removeAllChangeListeners();
        diffOrderLines.removeAllChangeListeners();
        curInstance.executeTransaction(db->{
            diffOrders.deleteAllFromRealm();
            diffOrderLines.deleteAllFromRealm();
            db.insertOrUpdate(newOrder);
            db.insertOrUpdate(lines);
        });

        closeQueryInstance(curInstance);
    }



    void deleteOrderFromRealm(String orderId) {
        Realm curInstance = getQueryRealmInstance();
        OrderRealm tmpOrder = curInstance.where(OrderRealm.class).equalTo("id",orderId).findFirst();
        if (tmpOrder == null) {
            return;
        }

        RealmResults<OrderLineRealm> lines = curInstance.where(OrderLineRealm.class).equalTo("order.id", orderId).findAll();

        lines.removeAllChangeListeners();
        tmpOrder.removeAllChangeListeners();

        curInstance.executeTransaction(db->{
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
        GoodsGroupRealm result = curInstance.where(GoodsGroupRealm.class).equalTo("groupId", groupId).findFirst();
        if (result != null) {
            result = curInstance.copyFromRealm(result);
        }
        closeQueryInstance(curInstance);
        return result;
    }

    void deleteGoodsGroupFromRealm(String id) {
        Realm curInstance = getQueryRealmInstance();

        List<GoodsGroupRealm> subs = curInstance.copyFromRealm(curInstance.where(GoodsGroupRealm.class).equalTo("parent.groupId", id).findAll());
        for (GoodsGroupRealm sub : subs){
            deleteGoodsGroupFromRealm(sub.getGroupId());
        }

        curInstance = getQueryRealmInstance();
        List<ItemRealm> items = curInstance.copyFromRealm(curInstance.where(ItemRealm.class).equalTo("group.groupId", id).findAll());
        for (ItemRealm item : items){
            deleteGoodItemFromRealm(item.getItemId());
        }

        curInstance = getQueryRealmInstance();

        GoodsGroupRealm group = curInstance.where(GoodsGroupRealm.class).equalTo("groupId", id).findFirst();
        if (group != null && group.isValid()) {
            group.removeAllChangeListeners();
            curInstance.executeTransaction(db->group.deleteFromRealm());
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
                goodItemRes.getPrice() != null ? goodItemRes.getPrice().getBase() : 0f,
                goodItemRes.getPrice() != null ? goodItemRes.getPrice().getMin() : 0f,
                goodItemRes.getRest() != null ? goodItemRes.getRest().getStore() : 0f,
                goodItemRes.getRest() != null ? goodItemRes.getRest().getDistribution() : 0f,
                goodItemRes.getRest() != null ? goodItemRes.getRest().getOfficial() : 0f,
                mCat,
                mParent,
                mBrand
        );
        curInstance.executeTransaction(db -> db.insertOrUpdate(newItem));
        closeQueryInstance(curInstance);

    }


    ItemRealm getItemById(String itemId) {
        Realm curInstance = getQueryRealmInstance();
        ItemRealm result = curInstance.where(ItemRealm.class).equalTo("itemId", itemId).findFirst();
        if (result != null) {
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
        Realm curInstance = getQueryRealmInstance();

        boolean safeDelete = (curInstance.where(CustomerDiscountRealm.class).equalTo("item.itemId", id).findFirst() == null)
                           && (curInstance.where(OrderLineRealm.class).equalTo("item.itemId", id).findFirst() == null);

        ItemRealm item = curInstance.where(ItemRealm.class).equalTo("itemId", id).findFirst();
        if (item!=null && item.isValid()) {
            if (safeDelete) {
                item.removeAllChangeListeners();
                curInstance.executeTransaction(db -> item.deleteFromRealm());
            } else {
                item.setGroup(null);
            }
        }
    }

    //endregion ====================  Items  =========================








}



