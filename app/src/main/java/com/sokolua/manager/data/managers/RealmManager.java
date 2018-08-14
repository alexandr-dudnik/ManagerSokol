package com.sokolua.manager.data.managers;

import android.support.annotation.Nullable;

import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderPlanRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;

import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observable;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
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

    public void updateCustomerTask(String taskId, boolean checked, String result) {
        TaskRealm task = getQueryRealmInstance().where(TaskRealm.class).equalTo("taskId", taskId).findFirst();
        if (task != null && task.isLoaded() && task.isValid()) {
            TaskRealm temp = getQueryRealmInstance().copyFromRealm(task);
            temp.setDone(checked);
            temp.setResult(result);
            getQueryRealmInstance().executeTransaction(db -> db.insertOrUpdate(temp));
        }
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

    public Observable<OrderRealm> getAllOrders() {
        RealmResults<OrderRealm> res = getQueryRealmInstance().where(OrderRealm.class).sort("status", Sort.ASCENDING, "date", Sort.DESCENDING).findAll();
        return Observable.fromIterable(res);
    }

    public Observable<GoodsGroupRealm> getGroupList(GoodsGroupRealm parent) {
        RealmResults<GoodsGroupRealm> res;
        if (parent == null) {
             res = getQueryRealmInstance().where(GoodsGroupRealm.class).isNull("parent").sort("name", Sort.ASCENDING).findAll();
        }else{
            res = getQueryRealmInstance().where(GoodsGroupRealm.class).equalTo("parent.groupId", parent.getGroupId()).sort("name", Sort.ASCENDING).findAll();
        }
        return Observable.fromIterable(res);
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
        return Observable.fromIterable(res.sort("name", Sort.ASCENDING).findAll());
    }

    public void setDeliveryDate(OrderRealm currentOrder, Date mDate) {
        OrderRealm temp = getQueryRealmInstance().copyFromRealm(currentOrder);
        temp.setDelivery(mDate);
        getQueryRealmInstance().executeTransaction(db -> db.insertOrUpdate(temp));
    }

    public void updateOrderItemPrice(OrderRealm order, ItemRealm item, Float value) {
        OrderLineRealm line = order.getLines().where().equalTo("item.itemId", item.getItemId()).findFirst();
        if (line != null) {
            OrderLineRealm temp = getQueryRealmInstance().copyFromRealm(line);
            temp.setPrice(value);
            getQueryRealmInstance().executeTransaction(db -> db.insertOrUpdate(temp));
        }
    }

    public void updateOrderItemQty(OrderRealm order, ItemRealm item, Float value) {
        OrderLineRealm line = order.getLines().where().equalTo("item.itemId", item.getItemId()).findFirst();
        if (line != null) {
            OrderLineRealm temp = getQueryRealmInstance().copyFromRealm(line);
            temp.setQuantity(value);
            getQueryRealmInstance().executeTransaction(db -> db.insertOrUpdate(temp));
        }
    }

    public void removeOrderItem(OrderRealm order, ItemRealm item) {
        OrderLineRealm line = order.getLines().where().equalTo("item.itemId", item.getItemId()).findFirst();
        if (line != null) {
            getQueryRealmInstance().executeTransaction(db -> line.deleteFromRealm());
        }
    }

    public Observable<OrderLineRealm> getOrderLinesList(OrderRealm order) {
        RealmResults<OrderLineRealm> res = getQueryRealmInstance().where(OrderLineRealm.class).equalTo("order.id", order.getId()).sort("item.artNumber").findAll();
        return Observable.fromIterable(res);
    }

    public void updateOrderStatus(OrderRealm order, int orderStatus) {
        OrderRealm temp = getQueryRealmInstance().copyFromRealm(order);
        temp.setStatus(orderStatus);
        getQueryRealmInstance().executeTransaction(db -> db.insertOrUpdate(temp));
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
        OrderRealm temp = getQueryRealmInstance().copyFromRealm(order);
        temp.setComments(comment);
        getQueryRealmInstance().executeTransaction(db -> db.insertOrUpdate(temp));
    }

    public void updateOrderPayment(OrderRealm order, int payment) {
        OrderRealm temp = getQueryRealmInstance().copyFromRealm(order);
        temp.setPayment(payment);
        getQueryRealmInstance().executeTransaction(db -> db.insertOrUpdate(temp));
    }

    public void addItemToCart(OrderRealm order, ItemRealm item, float newQty, float newPrice) {
        OrderRealm temp = getQueryRealmInstance().copyFromRealm(order);
        OrderLineRealm line = order.getLines().where().equalTo("item.itemId", item.getItemId()).findFirst();
        if (line == null){
            OrderLineRealm tmpLine = new OrderLineRealm(order, item, newQty, newPrice);
            temp.getLines().add(tmpLine);
            getQueryRealmInstance().executeTransaction(db->db.insertOrUpdate(temp));
        }else{
            OrderLineRealm tmpLine = getQueryRealmInstance().copyFromRealm(line);
            tmpLine.setQuantity(tmpLine.getQuantity()+newQty);
            tmpLine.setPrice(newPrice);
            getQueryRealmInstance().executeTransaction(db->db.insertOrUpdate(tmpLine));
        }

    }

    public OrderRealm getOrderById(String orderId) {
        return getQueryRealmInstance().where(OrderRealm.class).equalTo("id", orderId).findFirst();
    }
}


