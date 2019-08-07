package com.sokolua.manager.mvp.models;

import androidx.annotation.Nullable;

import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.CurrencyRealm;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.data.storage.realm.TradeRealm;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class OrderModel extends AbstractModel {
    public void setDeliveryDate(String currentOrderId, Date mDate) {
        mDataManager.setDeliveryDate(currentOrderId, mDate);
    }

    public void updateOrderItemPrice(String order, String item, Float value) {
        mDataManager.updateOrderItemPrice(order, item, value);
    }

    public void updateOrderItemQty(String order, String item, Float value) {
        mDataManager.updateOrderItemQty(order, item, value);
    }

    public void removeOrderItem(String order, String item) {
        mDataManager.removeOrderItem(order, item);
    }

    public Observable<List<OrderLineRealm>> getLinesList(String orderId) {
        return mDataManager.getOrderLines(orderId);
    }

    public boolean sendOrder(String order) {
        mDataManager.updateOrderStatus(order, ConstantManager.ORDER_STATUS_IN_PROGRESS);
        return false;
    }

    public void clearOrderLines(String order) {
        mDataManager.clearOrderLines(order);
    }

    public void updateOrderComment(String order, String comment) {
        mDataManager.updateOrderComment(order, comment);
    }

    public void updateOrderPayment(String orderId, int payment) {
        OrderRealm mOrder = mDataManager.getOrderById(orderId);
        if (mOrder == null) return;
        TradeRealm custTrade;
        if (payment == ConstantManager.ORDER_PAYMENT_OFFICIAL){
            custTrade = mOrder.getCustomer().getTradeOfficial();
            updateOrderCurrency(orderId, ConstantManager.MAIN_CURRENCY_CODE);
        }else{
            custTrade = mOrder.getCustomer().getTradeCash();
        }
        if (custTrade != null) {
            if (mOrder.isPayOnFact()){
                custTrade = mDataManager.getFactTradeForTrade(custTrade.getTradeId());
            }
            updateOrderTrade(orderId, custTrade.getTradeId());
        }
        mDataManager.updateOrderPayment(orderId, payment);
    }

    public void updateOrderTrade(String orderId, String tradeId) {

        Observable.fromIterable(mDataManager.getCopyOfOrderLines(orderId))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(line -> {
                    OrderRealm mOrder = mDataManager.getOrderById(orderId);
                    TradeRealm trade = mDataManager.getTradeById(tradeId);
                    float price = mDataManager.getItemPrice(
                                        line.getItem().getItemId(),
                                        mOrder.getPriceList().getPriceId(),
                                        tradeId,
                                        mOrder.getCurrency().getCurrencyId(),
                                        mOrder.getCustomer().getCustomerId(),
                                        trade != null && !trade.isCash()
                                );
                    updateOrderItemPrice(orderId, line.getItem().getItemId(), price);
                    return Observable.empty();
                })
                .doOnComplete(() -> mDataManager.updateOrderTrade(orderId, tradeId))
                .subscribe();

    }

    public void updateOrderFactFlag(String orderId, boolean fact){
        OrderRealm mOrder = mDataManager.getOrderById(orderId);
        if (mOrder == null) return;
        if (fact) updateOrderTrade(orderId, mDataManager.getFactTradeForTrade(mOrder.getTrade().getTradeId()).getTradeId());
        mDataManager.updateOrderFactFlag(orderId, fact);
    }

    public void updateOrderCurrency(String orderId, String currencyCode) {
        OrderRealm mOrder = mDataManager.getOrderById(orderId);
        if (mOrder == null) return;

        CurrencyRealm oldCurrency = mOrder.getCurrency();
        float oldRate = oldCurrency.getRate();
        if (oldCurrency.getCurrencyId().equals(currencyCode)) return;

        CurrencyRealm newCurrency = mDataManager.getCurrencyByCode(currencyCode);
        if (oldCurrency.getRate() == newCurrency.getRate() || newCurrency.getRate()==0f) return;
        float newRate = newCurrency.getRate();




        Observable.fromIterable(mDataManager.getCopyOfOrderLines(orderId))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(line -> {
                    updateOrderItemPrice(orderId, line.getItem().getItemId(), line.getPrice() * oldRate / newRate);
                    return Observable.empty();
                })
                .doOnComplete(() -> mDataManager.updateOrderCurrency(orderId, currencyCode))
                .subscribe();


    }

    public CurrencyRealm getCurrencyByName(String currencyName){
        return mDataManager.getCurrencyByName(currencyName);
    }

    public void updateOrderFromRemote(String orderId) {
        Observable.just(orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(id -> mDataManager.updateOrderFromRemote(orderId))
                .subscribe();
    }

    public OrderRealm getOrderById(String orderId) {
        return mDataManager.getOrderById(orderId);
    }

    public float getItemLowPrice(String itemId) {
        //TODO: get price from price list
        return 0f;
    }

    public Observable<List<CurrencyRealm>> getCurrencies() {
        return mDataManager.getCurrencyList();
    }

    public Observable<List<TradeRealm>> getTrades(@Nullable Boolean cash, @Nullable Boolean fact, @Nullable Boolean remote) {
        return mDataManager.getTrades(cash, fact, remote);
    }

    public TradeRealm getTradeByName(String tradeName) {
        return mDataManager.getTradeByName(tradeName);
    }
}
