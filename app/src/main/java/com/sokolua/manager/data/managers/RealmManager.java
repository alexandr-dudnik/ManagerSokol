package com.sokolua.manager.data.managers;

import android.support.annotation.Nullable;

import com.sokolua.manager.data.network.res.GoodGroupRes;
import com.sokolua.manager.data.network.res.GoodItemRes;
import com.sokolua.manager.data.storage.realm.BrandsRealm;
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

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import io.reactivex.Observable;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.internal.ManagableObject;

public class RealmManager {

    private Realm mRealmInstance;

    public void clearDataBase() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Set<Class<? extends RealmModel>> schemaClasses = realmConfiguration.getRealmObjectClasses();

        getQueryRealmInstance().executeTransaction(db->{
//            for (Class<? extends RealmModel> model : schemaClasses) {
//                db.delete(model);
//            }
            db.deleteAll();
        });
    }

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
        return getQueryRealmInstance()
                .where(CustomerRealm.class)
                .equalTo("customerId", id)
                .findFirst();
    }

    public int getCustomerDebtType(String customerId) {
        RealmQuery<DebtRealm> qAll = getQueryRealmInstance()
                .where(DebtRealm.class)
                .equalTo("customer.customerId", customerId);
        if (qAll.findFirst() == null) {
            return ConstantManager.DEBT_TYPE_NO_DEBT;
        }
        return qAll.equalTo("outdated", true).findFirst() == null ? ConstantManager.DEBT_TYPE_NORMAL : ConstantManager.DEBT_TYPE_OUTDATED;
    }

    public Observable<NoteRealm> getCustomerNotes(String customerId) {
        RealmQuery<NoteRealm> qAll = getQueryRealmInstance()
                .where(NoteRealm.class)
                .equalTo("customer.customerId", customerId);
        if (qAll.findFirst() == null) {
            return Observable.empty();
        }
        return Observable.fromIterable(qAll.sort("date", Sort.DESCENDING).findAll())
                .filter(item -> item.isLoaded()) //получаем только загруженные
                .filter(ManagableObject::isValid)
                ;
    }

    public Observable<DebtRealm> getCustomerDebt(String customerId) {
        RealmQuery<DebtRealm> qAll = getQueryRealmInstance()
                .where(DebtRealm.class)
                .equalTo("customer.customerId", customerId);
        if (qAll.findFirst() == null) {
            return Observable.empty();
        }
        return Observable.fromIterable(qAll.sort("currency").findAll())
                .filter(item -> item.isLoaded()) //получаем только загруженные
                .filter(ManagableObject::isValid)
                ;
    }

    public Observable<TaskRealm> getCustomerTasks(String customerId) {
        RealmQuery<TaskRealm> qAll = getQueryRealmInstance()
                .where(TaskRealm.class)
                .equalTo("customer.customerId", customerId);
        if (qAll.findFirst() == null) {
            return Observable.empty();
        }
        return Observable.fromIterable(qAll.sort("taskType").findAll())
                .filter(item -> item.isLoaded()) //получаем только загруженные
                .filter(ManagableObject::isValid)
                ;
    }

    public Observable<DebtRealm> getCustomerDebtByType(String customerId, int debtType) {
        RealmQuery<DebtRealm> qAll = getQueryRealmInstance()
                .where(DebtRealm.class)
                .equalTo("customer.customerId", customerId);
        CustomerRealm customer = getCustomerById(customerId);
        if (qAll.findFirst() == null || customer == null) {
            return Observable.empty();
        }
        switch(debtType){
            case ConstantManager.DEBT_TYPE_NORMAL:
                return Observable.fromIterable(qAll.equalTo("outdated", false).sort("currency").findAll())
                        .filter(item -> item.isLoaded()) //получаем только загруженные
                        .filter(ManagableObject::isValid)
                        ;
            case ConstantManager.DEBT_TYPE_OUTDATED:
                return Observable.fromIterable(qAll.equalTo("outdated", true).sort("currency").findAll())
                        .filter(item -> item.isLoaded()) //получаем только загруженные
                        .filter(ManagableObject::isValid)
                        ;
            case ConstantManager.DEBT_TYPE_WHOLE:
                return Observable.fromIterable(qAll.sort("currency").findAll())
                        .groupBy(DebtRealm::getCurrency)
                        .map(grp->{
                            DebtRealm res = new DebtRealm(customer, grp.getKey(), 0f, 0f, false);
                            grp.forEach (item ->{
                                res.setAmount(res.getAmount()+item.getAmount());
                                res.setAmountUSD(res.getAmountUSD()+item.getAmountUSD());
                            });
                            return res;
                        })
                        .filter(item -> item.isLoaded()) //получаем только загруженные
                        .filter(ManagableObject::isValid)

                        ;
        }
        return Observable.empty();
    }

    public Observable<TaskRealm> getCustomerTaskByType(String customerId, int taskType) {
        RealmQuery<TaskRealm> qAll = getQueryRealmInstance()
                .where(TaskRealm.class)
                .equalTo("customer.customerId", customerId)
                .equalTo("taskType", taskType);
        if (qAll.findFirst() == null ) {
            return Observable.empty();
        }
        return Observable.fromIterable(qAll.sort("done",Sort.ASCENDING, "text", Sort.ASCENDING).findAll())
                .filter(item -> item.isLoaded()) //получаем только загруженные
                .filter(ManagableObject::isValid)
                ;
    }

    public void updateCustomerTask(String taskId, boolean checked, String result) {
        TaskRealm task = getQueryRealmInstance()
                .where(TaskRealm.class)
                .equalTo("taskId", taskId).findFirst();
        if (task != null && task.isLoaded() && task.isValid()) {
            getQueryRealmInstance().executeTransaction(db -> {
                task.setDone(checked);
                task.setResult(result);
            });
        }
    }

    public Observable<OrderPlanRealm> getCustomerPlan(String customerId) {
        RealmQuery<OrderPlanRealm> qAll = getQueryRealmInstance()
                .where(OrderPlanRealm.class)
                .equalTo("customer.customerId", customerId);
        if (qAll.findFirst() == null ) {
            return Observable.empty();
        }
        return Observable.fromIterable(qAll.sort("category.name",Sort.ASCENDING).findAll())
                .filter(item -> item.isLoaded()) //получаем только загруженные
                .filter(ManagableObject::isValid)
                ;
    }

    public Observable<OrderRealm> getCustomerOrders(String customerId) {
        RealmResults<OrderRealm> res = getQueryRealmInstance()
                .where(OrderRealm.class)
                .equalTo("customer.customerId", customerId)
                .sort("status", Sort.ASCENDING, "date", Sort.DESCENDING)
                .findAll();
        return Observable.fromIterable(res)
                .filter(item -> item.isLoaded()) //получаем только загруженные
                .filter(ManagableObject::isValid)
                ;
    }

    public Observable<OrderRealm> getAllOrders() {
        RealmResults<OrderRealm> res = getQueryRealmInstance()
                .where(OrderRealm.class)
                .sort("status", Sort.ASCENDING, "date", Sort.DESCENDING)
                .findAll();
        return Observable.fromIterable(res)
                .filter(item -> item.isLoaded()) //получаем только загруженные
                .filter(ManagableObject::isValid)
                ;
    }

    public Observable<GoodsGroupRealm> getGroupList(GoodsGroupRealm parent) {
        RealmResults<GoodsGroupRealm> res;
        if (parent == null) {
             res = getQueryRealmInstance()
                     .where(GoodsGroupRealm.class)
                     .isNull("parent")
                     .sort("name", Sort.ASCENDING)
                     .findAll();
        }else{
            res = getQueryRealmInstance()
                    .where(GoodsGroupRealm.class)
                    .equalTo("parent.groupId", parent.getGroupId())
                    .sort("name", Sort.ASCENDING)
                    .findAll();
        }
        return Observable.fromIterable(res)
                .filter(item -> item.isLoaded()) //получаем только загруженные
                .filter(ManagableObject::isValid)
                ;
    }

    public Observable<ItemRealm> getItemList(GoodsGroupRealm parent, String filter) {
        RealmQuery<ItemRealm> res;
        if (parent == null) {
            res = getQueryRealmInstance().where(ItemRealm.class).alwaysTrue();
        }else {
            res = getQueryRealmInstance().where(ItemRealm.class).equalTo("group.groupId", parent.getGroupId());
        }
        if (filter != null && !filter.isEmpty()){
            res.contains("index", filter);
        }
        return Observable.fromIterable(res.sort("name", Sort.ASCENDING).findAll())
                .filter(item -> item.isLoaded()) //получаем только загруженные
                .filter(ManagableObject::isValid)
                ;
    }

    public void setDeliveryDate(OrderRealm order, Date mDate) {
        getQueryRealmInstance().executeTransaction(db -> order.setDelivery(mDate));
    }

    public void updateOrderItemPrice(OrderRealm order, ItemRealm item, Float value) {
        OrderLineRealm line = getQueryRealmInstance()
                .where(OrderLineRealm.class)
                .equalTo("order.id", order.getId())
                .equalTo("item.itemId", item.getItemId())
                .findFirst();
        if (line != null) {
            getQueryRealmInstance().executeTransaction(db -> line.setPrice(value));
        }
    }

    public void updateOrderItemQty(OrderRealm order, ItemRealm item, Float value) {
        OrderLineRealm line = getQueryRealmInstance()
                .where(OrderLineRealm.class)
                .equalTo("order.id", order.getId())
                .equalTo("item.itemId", item.getItemId())
                .findFirst();
        if (line != null) {
            getQueryRealmInstance().executeTransaction(db -> line.setQuantity(value));
        }
    }

    public void removeOrderItem(OrderRealm order, ItemRealm item) {
        OrderLineRealm line = getQueryRealmInstance()
                .where(OrderLineRealm.class)
                .equalTo("order.id", order.getId())
                .equalTo("item.itemId", item.getItemId())
                .findFirst();
        if (line != null) {
            getQueryRealmInstance().executeTransaction(db -> line.deleteFromRealm());
        }
    }

    public Observable<OrderLineRealm> getOrderLinesList(OrderRealm order) {
        RealmResults<OrderLineRealm> res = getQueryRealmInstance()
                .where(OrderLineRealm.class)
                .equalTo("order.id", order.getId())
                .sort("item.artNumber").findAll();
        return Observable.fromIterable(res);
    }

    public void updateOrderStatus(OrderRealm order, int orderStatus) {
        getQueryRealmInstance().executeTransaction(db -> order.setStatus(orderStatus));
    }

    public void clearOrderLines(OrderRealm order) {
        if (!order.getLines().isEmpty()){
            getQueryRealmInstance().executeTransaction(db -> order.getLines().deleteAllFromRealm());
        }
    }

    public OrderRealm getCartForCustomer(CustomerRealm customer) {
        OrderRealm result = getQueryRealmInstance().where(OrderRealm.class)
                .equalTo("customer.customerId", customer.getCustomerId())
                .equalTo("status", ConstantManager.ORDER_STATUS_CART)
                .findFirst();
        if (result == null){
            OrderRealm tmp = new OrderRealm("cart_"+customer.getCustomerId(), customer, Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), ConstantManager.ORDER_STATUS_CART, ConstantManager.ORDER_PAYMENT_CASH, ConstantManager.MAIN_CURRENCY, "");
            getQueryRealmInstance().executeTransaction(db -> db.insertOrUpdate(tmp));
            result = getCartForCustomer(customer);
        }
        return result;
    }

    public void updateOrderComment(OrderRealm order, String comment) {
        getQueryRealmInstance().executeTransaction(db -> order.setComments(comment));
    }

    public void updateOrderPayment(OrderRealm order, int payment) {
        getQueryRealmInstance().executeTransaction(db -> order.setPayment(payment));
    }

    public void addItemToCart(OrderRealm order, ItemRealm item, float newQty, float newPrice) {
        OrderLineRealm line = getQueryRealmInstance()
                .where(OrderLineRealm.class)
                .equalTo("order.id", order.getId())
                .equalTo("item.itemId", item.getItemId())
                .findFirst();
        if (line == null){
            OrderLineRealm tmpLine = new OrderLineRealm(order, item, newQty, newPrice);
            getQueryRealmInstance().executeTransaction(db->db.insertOrUpdate(tmpLine));
        }else{
            getQueryRealmInstance().executeTransaction(db->{
                line.setQuantity(line.getQuantity()+newQty);
                line.setPrice(newPrice);
            });
        }

    }

    public OrderRealm getOrderById(String orderId) {
        return getQueryRealmInstance()
                .where(OrderRealm.class)
                .equalTo("id", orderId)
                .findFirst();
    }

    public Observable<CustomerRealm> getCustomersByVisitDate(Date day) {
        return Observable.fromIterable(
                    getQueryRealmInstance()
                    .where(VisitRealm.class)
                    .equalTo("date", day)
                    .sort("customer.name", Sort.ASCENDING)
                    .findAll()
        ).map(VisitRealm::getCustomer);
    }

    public void addNewNote(CustomerRealm customer, String note) {
        getQueryRealmInstance().executeTransaction(db->db.insertOrUpdate(new NoteRealm(customer, note)));
    }

    public void deleteNote(NoteRealm note) {
        NoteRealm tmp = getQueryRealmInstance()
                .where(NoteRealm.class)
                .equalTo("noteId", note.getNoteId())
                .findFirst();
        if (tmp != null) {
            getQueryRealmInstance().executeTransaction(db->tmp.deleteFromRealm());
        }
    }



    public void saveGoodGroupToRealm(GoodGroupRes groupRes) {
        Realm curInstance = Realm.getDefaultInstance();
        GoodsGroupRealm mParent=null;
        if (groupRes.getParent() != null && !groupRes.getParent().isEmpty()) {
            mParent = curInstance
                    .where(GoodsGroupRealm.class)
                    .equalTo("groupId", groupRes.getParent())
                    .findFirst();
            if (mParent == null){
                curInstance.executeTransaction(db->db.insertOrUpdate(new GoodsGroupRealm(groupRes.getParent(),"no_name",null,null)));
                mParent = curInstance
                        .where(GoodsGroupRealm.class)
                        .equalTo("groupId", groupRes.getParent())
                        .findFirst();
            }
        }

        String mImageURL ="";
        if (groupRes.getImage() != null) {
            //TODO: parse image from base64 string groupRes.getImage()
            mImageURL ="";
        }

        GoodsGroupRealm newGroup = new GoodsGroupRealm(groupRes.getId(), groupRes.getName(), mParent, mImageURL);
        curInstance.executeTransaction(db -> db.insertOrUpdate(newGroup));
        curInstance.close();
    }

    public void saveGoodItemToRealm(GoodItemRes goodItem) {
        Realm curInstance = Realm.getDefaultInstance();
        GoodsGroupRealm mParent=null;
        if (goodItem.getGroupId() != null && !goodItem.getGroupId().isEmpty()) {
            mParent = curInstance
                    .where(GoodsGroupRealm.class)
                    .equalTo("groupId", goodItem.getGroupId())
                    .findFirst();
            if (mParent == null){
                curInstance.executeTransaction(db->db.insertOrUpdate(new GoodsGroupRealm(goodItem.getGroupId(),"no_name",null,null)));
                mParent = curInstance
                        .where(GoodsGroupRealm.class)
                        .equalTo("groupId", goodItem.getGroupId())
                        .findFirst();
            }
        }

        BrandsRealm mBrand = null;
        if (goodItem.getBrand() != null && goodItem.getBrand().getId() != null && !goodItem.getBrand().getId().isEmpty()) {
            mBrand = curInstance
                    .where(BrandsRealm.class)
                    .equalTo("brandId", goodItem.getBrand().getId())
                    .findFirst();
            if (mBrand == null){
                curInstance.executeTransaction(db->db.insertOrUpdate(new BrandsRealm(goodItem.getBrand().getId(),goodItem.getBrand().getName(),null)));
                mBrand = curInstance
                        .where(BrandsRealm.class)
                        .equalTo("brandId", goodItem.getBrand().getId())
                        .findFirst();
            }
        }

        GoodsCategoryRealm mCat = null;
        if (goodItem.getBrand() != null && goodItem.getCategory().getId() != null && !goodItem.getCategory().getId().isEmpty()) {
            mCat = curInstance
                    .where(GoodsCategoryRealm.class)
                    .equalTo("categoryId", goodItem.getCategory().getId())
                    .findFirst();
            if (mParent == null){
                curInstance.executeTransaction(db->db.insertOrUpdate(new GoodsCategoryRealm(goodItem.getCategory().getId(),goodItem.getCategory().getName(),null)));
                mCat = curInstance
                        .where(GoodsCategoryRealm.class)
                        .equalTo("categoryId", goodItem.getCategory().getId())
                        .findFirst();
            }
        }

        ItemRealm newItem = new ItemRealm(
                goodItem.getId(),
                goodItem.getName(),
                goodItem.getArticle(),
                goodItem.getPrice()!=null?goodItem.getPrice().getBase():0f,
                goodItem.getPrice()!=null?goodItem.getPrice().getMin():0f,
                goodItem.getRest()!=null?goodItem.getRest().getStore():0f,
                goodItem.getRest()!=null?goodItem.getRest().getDistribution():0f,
                goodItem.getRest()!=null?goodItem.getRest().getOfficial():0f,
                mCat,
                mParent,
                mBrand
                );
        curInstance.executeTransaction(db -> db.insertOrUpdate(newItem));
        curInstance.close();

    }


    public void clearGoodsGroups() {
        Realm.getDefaultInstance().executeTransaction(db -> db.delete(GoodsGroupRealm.class));
    }

    public void clearGoods() {
        Realm.getDefaultInstance().executeTransaction(db -> db.delete(ItemRealm.class));
    }
}


