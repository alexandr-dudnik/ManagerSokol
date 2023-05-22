package com.sokolua.manager.ui.screens.order;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.CurrencyRealm;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.data.storage.realm.TradeRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.OrderModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.mvp.presenters.MenuItemHolder;
import com.sokolua.manager.mvp.presenters.RootPresenter;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.ui.screens.goods.GoodsScreen;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.MiscUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import dagger.Provides;
import flow.Flow;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmChangeListener;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import mortar.MortarScope;

@Screen(R.layout.screen_order)
public class OrderScreen extends AbstractScreen<RootActivity.RootComponent> {

    private String currentOrderId;

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerOrderScreen_Component.builder()
                .module(new Module())
                .rootComponent(parentComponent)
                .build();
    }


    public OrderScreen(String currentOrderId) {
        this.currentOrderId = currentOrderId;
    }

    @Override
    public String getScopeName() {
        return super.getScopeName() + "_" + this.currentOrderId;
    }

    //region ===================== DI =========================

    @dagger.Module
    class Module {

        @Provides
        @DaggerScope(OrderScreen.class)
        OrderModel provideOrderModel() {
            return new OrderModel();
        }

        @Provides
        @DaggerScope(OrderScreen.class)
        Presenter providePresenter() {
            return new Presenter();
        }

    }


    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(OrderScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(OrderView view);

        void inject(OrderLineViewHolder viewHolder);
    }
    //endregion ================== DI =========================


    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<OrderView, OrderModel> {

        ReactiveRecyclerAdapter.ReactiveViewHolderFactory<OrderLineRealm> linesViewHolder;
        ReactiveRecyclerAdapter linesAdapter;
        private RealmChangeListener<RealmResults<OrderLineRealm>> lineChangeListener;
        private RealmObjectChangeListener<OrderRealm> orderChangeListener;

        OrderRealm currentOrder;
        private Disposable tradesSub;

        public Presenter() {
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);

            currentOrder = mModel.getOrderById(currentOrderId);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            if (currentOrder == null || !currentOrder.isValid() || !currentOrder.isLoaded()) {
                Flow.get(getView()).goBack();
            }
            super.onLoad(savedInstanceState);


            linesViewHolder = (parent, pViewType) -> {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_line_item, parent, false);
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new OrderLineViewHolder(view)
                );
            };
            linesAdapter = new ReactiveRecyclerAdapter<>(
                    Observable.empty(),
                    linesViewHolder,
                    false
            );
            updateLines();
            getView().setLinesAdapter(linesAdapter);
            lineChangeListener = orderLineRealms -> {
                if (!orderLineRealms.isValid() || !orderLineRealms.isLoaded()) {
                    orderLineRealms.removeAllChangeListeners();
                } else {
                    updateLines();
                }
            };
            currentOrder.getLines().addChangeListener(lineChangeListener);

            orderChangeListener = (realmModel, changeSet) -> {
                OrderView mView = getView();
                if (!realmModel.isLoaded() || !realmModel.isValid()) {
                    realmModel.removeAllChangeListeners();
                    mView.viewOnBackPressed();
                }
                if (changeSet == null) {
                    return;
                }
                if (changeSet.isDeleted()) {
                    realmModel.removeAllChangeListeners();
                    mView.viewOnBackPressed();
                }
                if (changeSet.isFieldChanged("comments")) {
                    mView.setComment(currentOrder.getComments());
                }
                if (changeSet.isFieldChanged("currency")) {
                    mView.setCurrency(currentOrder.getCurrency().getName());
                }
                if (changeSet.isFieldChanged("delivery")) {
                    mView.setDeliveryDate(currentOrder.getDelivery());
                }
                if (changeSet.isFieldChanged("date")) {
                    mView.setOrderDate(currentOrder.getDate());
                }
                if (changeSet.isFieldChanged("payment")) {
                    mView.setOrderType(currentOrder.getPayment());
                    refreshTrades();
                }
                if (changeSet.isFieldChanged("payOnFact")) {
                    mView.setFact(currentOrder.isPayOnFact());
                    refreshTrades();
                }
                if (changeSet.isFieldChanged("trade")) {
                    mView.setTrade(currentOrder.getTrade() == null ? null : currentOrder.getTrade().getName());
                }
                if (changeSet.isFieldChanged("currency")) {
                    mView.setCurrency(currentOrder.getCurrency().getName());
                }
                if (changeSet.isFieldChanged("priceList")) {
                    mView.setPriceList(currentOrder.getPriceList() == null ? null : currentOrder.getPriceList().getName());
                }
                if (changeSet.isFieldChanged("status")) {
                    mView.setStatus(currentOrder.getStatus());
                    initActionBar();
                }
            };
            currentOrder.addChangeListener(orderChangeListener);

            mModel.getCurrencies()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(currencies -> {
                        if (getView() != null) {
                            ArrayList<String> arrCurrencies = new ArrayList<>();
                            for (CurrencyRealm cur : currencies) {
                                arrCurrencies.add(cur.getName());
                            }
                            getView().setCurrencyList(arrCurrencies);
                        }
                    })
                    .doOnError(throwable -> Log.e("ERROR","Currencies list", throwable) )
                    .subscribe()
            ;

            refreshTrades();
        }

        private void refreshTrades() {
            if (tradesSub != null && !tradesSub.isDisposed()) {
                tradesSub.dispose();
            }
            if (currentOrder.getStatus() == ConstantManager.ORDER_STATUS_CART) {
                CustomerRealm cust = currentOrder.getCustomer();
                TradeRealm custTrade = cust.getTradeCash() == null? (cust.getTradeFop() == null? cust.getTradeFop() : cust.getTradeOfficial()) : cust.getTradeCash();
                boolean isRemote = custTrade != null && custTrade.isRemote();
                tradesSub = mModel.getTrades(
                        currentOrder.getPayment() == ConstantManager.ORDER_PAYMENT_FOP,
                        currentOrder.getPayment() == ConstantManager.ORDER_PAYMENT_CASH,
                        currentOrder.isPayOnFact(),
                        isRemote
                        )
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(trades -> {
                            if (getView() != null) {
                                ArrayList<String> arrTrades = new ArrayList<>();
                                for (TradeRealm trade : trades) {
                                    arrTrades.add(trade.getName());
                                }
                                getView().setTradeList(arrTrades);
                            }
                        })
                        .doOnError(throwable -> Log.e("ERROR","Trades list", throwable) )
                        .subscribe();
            }
        }

        private void updateLines() {
            linesAdapter.refreshList(mModel.getLinesList(currentOrderId));
            getView().setOrderAmount(currentOrder.getTotal());
        }


        @Override
        public void dropView(OrderView view) {
            if (currentOrder != null && orderChangeListener != null) {
                currentOrder.removeChangeListener(orderChangeListener);
                if (currentOrder.getLines() != null && lineChangeListener != null) {
                    currentOrder.getLines().removeChangeListener(lineChangeListener);
                }
            }
            super.dropView(view);
        }

        @Override
        protected void initActionBar() {
            RootPresenter.ActionBarBuilder abb = mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .setBackArrow(true)
                    .setTitle(currentOrder == null ? "" : currentOrder.getCustomer().getName())
                    .addAction(new MenuItemHolder(App.getStringRes(R.string.menu_synchronize), R.drawable.ic_sync, item -> {
                        if (currentOrder != null) {
                            if (currentOrder.getStatus() == ConstantManager.ORDER_STATUS_IN_PROGRESS) {
                                currentOrder.removeChangeListener(orderChangeListener);
                                mModel.sendOrder(currentOrder.getId());
                                Flow.get(getView()).goBack();
                            } else {
                                mModel.updateOrderFromRemote(currentOrder.getId());
                            }
                        }
                        return false;
                    }, ConstantManager.MENU_ITEM_TYPE_ITEM));
            if (currentOrder != null && currentOrder.getStatus() == ConstantManager.ORDER_STATUS_CART) {
                abb.addAction(new MenuItemHolder(App.getStringRes(R.string.action_send_order), R.drawable.ic_send, item -> {
                    if (currentOrder.getLines().isEmpty()) {
                        if (getRootView() != null) {
                            getRootView().showMessage(App.getStringRes(R.string.error_empty_order));
                            return false;
                        }
                    }
                    if (currentOrder.getDate().after(currentOrder.getDelivery())) {
                        if (getRootView() != null) {
                            getRootView().showMessage(App.getStringRes(R.string.error_wrong_delivery));
                            return false;
                        }
                    }
                    currentOrder.removeChangeListener(orderChangeListener);
                    mModel.sendOrder(currentOrderId);
                    Flow.get(getView()).goBack();
                    return false;
                }, ConstantManager.MENU_ITEM_TYPE_ACTION))
                        .addAction(new MenuItemHolder(App.getStringRes(R.string.action_clear_order), R.drawable.ic_clear_all, item -> {
                            mModel.clearOrderLines(currentOrderId);
                            return false;
                        }, ConstantManager.MENU_ITEM_TYPE_ITEM))
                ;

            }

            abb.build();

        }


        public void updateAllFields() {
            OrderView mView = getView();
            mView.setComment(currentOrder.getComments());
            mView.setDeliveryDate(currentOrder.getDelivery());
            mView.setOrderAmount(currentOrder.getTotal());
            mView.setOrderDate(currentOrder.getDate());
            mView.setOrderType(currentOrder.getPayment());
            mView.setStatus(currentOrder.getStatus());
            mView.setFact(currentOrder.isPayOnFact());
            mView.setCurrency(currentOrder.getCurrency() == null ? App.getStringRes(R.string.national_currency) : currentOrder.getCurrency().getName());
            mView.setTrade(currentOrder.getTrade() == null ? "" : currentOrder.getTrade().getName());
            mView.setPriceList(currentOrder.getPriceList() == null ? "" : currentOrder.getPriceList().getName());

        }

        public void updateDeliveryDate(Date mDate) {
            mModel.setDeliveryDate(currentOrderId, mDate);
        }

        public void updatePrice(OrderLineRealm line) {
            if (currentOrder.getStatus() == ConstantManager.ORDER_STATUS_CART) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getView().getContext());
                alert.setTitle(App.getStringRes(R.string.order_items_header_price));
                alert.setMessage(line.getItem().getName());

                final EditText input = new EditText(getView().getContext());
                alert.setView(input);
                input.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), line.getPrice()));
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                input.setRawInputType(Configuration.KEYBOARD_12KEY);
                input.setEnabled(line.getPrice() < 0.01f);

                alert.setPositiveButton(App.getStringRes(R.string.button_positive_text), (dialog, whichButton) -> {
                    float newValue = 0f;
                    try {
                        newValue = Float.parseFloat(input.getText().toString().replace(",", "."));
                    } catch (Throwable ignore) {
                    }

                    if (currentOrder.getTrade() != null && !currentOrder.getTrade().isCash()) {
                        newValue = MiscUtils.roundPrice(newValue);
                    }


                    //check price
                    final double itemLowPrice = mModel.getItemLowPrice(line.getItem().getItemId());
                    if (newValue < itemLowPrice) {
                        if (getRootView() != null) {
                            getRootView().showMessage(App.getStringRes(R.string.error_low_price) + " (" + String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), itemLowPrice) + ")");
                        }
//                    } else {
                    }
                    mModel.updateOrderItemPrice(currentOrderId, line.getItem().getItemId(), newValue);
                });
                alert.setNegativeButton(App.getStringRes(R.string.button_negative_text), (dialog, whichButton) -> {
                });
                alert.show();
            }
        }

        public void updateQuantity(OrderLineRealm line) {
            if (currentOrder.getStatus() == ConstantManager.ORDER_STATUS_CART) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getView().getContext());
                alert.setTitle(App.getStringRes(R.string.order_items_header_quantity));

                final EditText input = new EditText(getView().getContext());
                alert.setView(input);
                input.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int), line.getQuantity()));

                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setRawInputType(Configuration.KEYBOARD_12KEY);

                alert.setPositiveButton(App.getStringRes(R.string.button_positive_text), (dialog, whichButton) -> {
                    float newValue = 0f;
                    try {
                        newValue = Float.parseFloat(input.getText().toString());
                    } catch (Throwable ignore) {
                    }
                    if (newValue == 0f) {
                        mModel.removeOrderItem(currentOrderId, line.getItem().getItemId());
                    } else {
                        mModel.updateOrderItemQty(currentOrderId, line.getItem().getItemId(), newValue);
                    }
                });
                alert.setNegativeButton(App.getStringRes(R.string.button_negative_text), (dialog, whichButton) -> {
                });
                alert.show();
            }
        }

        public void removeLine(OrderLineRealm currentItem) {
            if (currentOrder.getStatus() == ConstantManager.ORDER_STATUS_CART) {
                if (currentItem.getItem().isValid()) {
                    mModel.removeOrderItem(currentOrderId, currentItem.getItem().getItemId());
                }
            }
        }

        public void updatePayment(int payment) {
            int mPayment = currentOrder != null && currentOrder.isValid() ? currentOrder.getPayment() : -1;
            if (mPayment != payment) {
                mModel.updateOrderPayment(currentOrderId, payment);
            }
        }


        public void updateComment(String comment) {
            mModel.updateOrderComment(currentOrderId, comment);
        }

        public void addNewItemToOrder() {
            if (getRootView() != null) {
                Flow.get((RootActivity) getRootView()).set(new GoodsScreen(currentOrderId));
            }
        }

        public void updateCurrency(String currency) {
            CurrencyRealm mCurrency = currentOrder != null && currentOrder.isValid() ? currentOrder.getCurrency() : null;
            if (mCurrency != null && mCurrency.isValid() && (!mCurrency.getName().equals(currency))) {
                mModel.updateOrderCurrency(currentOrderId, mModel.getCurrencyByName(currency).getCurrencyId());
            }
        }

        public void updateTrade(String tradeName) {
            TradeRealm mTrade = currentOrder != null && currentOrder.isValid() ? currentOrder.getTrade() : null;
            if (mTrade != null && mTrade.isValid() && (!mTrade.getName().equals(tradeName))) {
                mModel.updateOrderTrade(currentOrderId, mModel.getTradeByName(tradeName).getTradeId());
            }
        }

        public void updateFactFlag(boolean checked) {
            TradeRealm mTrade = currentOrder != null && currentOrder.isValid() ? currentOrder.getTrade() : null;
            if (mTrade != null && mTrade.isValid() && (mTrade.isFact() != checked)) {
                mModel.updateOrderFactFlag(currentOrderId, checked);
            }
        }

        public void openCommentDialog() {
            if (getView() == null) return;

            AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
            builder.setTitle(App.getStringRes(R.string.order_comment_title));

            final EditText input = new EditText(getView().getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
            input.setText(currentOrder.getComments());
            builder.setView(input);

            builder.setPositiveButton(App.getStringRes(R.string.button_positive_text), (dialog, whichButton) -> mModel.updateOrderComment(currentOrderId, input.getText().toString()));
            builder.setNegativeButton(App.getStringRes(R.string.button_negative_text), (dialog, whichButton) -> {
            });

            builder.show();
        }
    }
    //endregion ================== Presenter =========================


}
