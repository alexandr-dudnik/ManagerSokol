package com.sokolua.manager.data.storage.realm;

import androidx.annotation.NonNull;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Index;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import io.realm.internal.Keep;

@Keep
public class CustomerRealm extends RealmObject implements Serializable{
    @PrimaryKey
    @Required
    private String customerId;
    @Required
    private String name;
    private String contactName = "";
    private String address = "";
    private String email = "";
    private String category = "";
    private PriceListRealm price = null;
    private TradeRealm tradeCash = null;
    private TradeRealm tradeFop = null;
    private TradeRealm tradeOfficial = null;
    @Index
    private String index = "";
    @LinkingObjects("customer")
    private final RealmResults<DebtRealm> debt = null;
    @LinkingObjects("customer")
    private final RealmResults<NoteRealm> notes = null;
    @LinkingObjects("customer")
    private final RealmResults<TaskRealm> tasks = null;
    @LinkingObjects("customer")
    private final RealmResults<OrderPlanRealm> plan = null;
    @LinkingObjects("customer")
    private final RealmResults<CustomerDiscountRealm> discounts = null;
    @LinkingObjects("customer")
    private final RealmResults<VisitRealm> visits = null;
    @LinkingObjects("customer")
    private final RealmResults<OrderRealm> orders = null;
    @LinkingObjects("customer")
    private final RealmResults<CustomerPhoneRealm> phones = null;

    public CustomerRealm() {
    }

    public CustomerRealm(String customerId, @NonNull String name, String contactName, String address, String email, String category, PriceListRealm price, TradeRealm tradeCash, TradeRealm tradeFop, TradeRealm tradeOfficial) {
        this.customerId = customerId;
        this.name = name;
        this.contactName = contactName;
        this.address = address;
        this.email = email;
        this.category = category;
        this.index = name.toLowerCase();
        this.price = price;
        this.tradeCash = tradeCash;
        this.tradeFop = tradeFop;
        this.tradeOfficial = tradeOfficial;
    }

    //region ===================== Getters =========================

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getContactName() {
        return contactName==null?"":contactName;
    }

    public String getAddress() {
        return address==null?"":address;
    }


    public String getEmail() {
        return email==null?"":email;
    }

    public RealmResults<DebtRealm> getDebt() {
        return debt;
    }

    public RealmResults<NoteRealm> getNotes() {
        return notes;
    }

    public RealmResults<TaskRealm> getTasks() {
        return tasks;
    }

    public RealmResults<OrderPlanRealm> getPlan() {
        return plan;
    }

    public RealmResults<CustomerDiscountRealm> getDiscounts() {
        return discounts;
    }

    public RealmResults<VisitRealm> getVisits() {
        return visits;
    }

    public RealmResults<OrderRealm> getOrders() {
        return orders;
    }

    public RealmResults<CustomerPhoneRealm> getPhones() {
        return phones;
    }

    public String getCategory() {
        return category==null?"":category;
    }

    public String getPhone(){
        if (this.isManaged() && this.isValid() && phones!=null && !this.phones.isEmpty()){
            return phones.first().getPhoneNumber();
        }
        return "";
    }

    public PriceListRealm getPrice() {
        return price;
    }

    public TradeRealm getTradeCash() {
        return tradeCash;
    }

    public TradeRealm getTradeOfficial() {
        return tradeOfficial;
    }

    public TradeRealm getTradeFop() {
        return tradeFop;
    }

//endregion ================== Getters =========================
}
