package com.sokolua.manager.ui.screens.order;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
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

import java.util.Date;
import java.util.Locale;

import dagger.Provides;
import flow.Flow;
import io.reactivex.Observable;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import mortar.MortarScope;

@Screen(R.layout.screen_order)
public class OrderScreen extends AbstractScreen<RootActivity.RootComponent>{

    private OrderRealm currentOrder;

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerOrderScreen_Component.builder()
                .module(new Module())
                .rootComponent(parentComponent)
                .build();
    }



    public OrderScreen(OrderRealm currentOrder) {
        this.currentOrder = currentOrder;
    }

    @Override
    public String getScopeName() {
        return super.getScopeName()+"_"+this.currentOrder.getId();
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
        private RealmObjectChangeListener<RealmModel> orderChangeListener;

        public Presenter() {
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);


            linesViewHolder = (parent, pViewType) -> {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_line_item, parent, false);
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new OrderLineViewHolder(view)
                );
            };
            linesAdapter = new ReactiveRecyclerAdapter(Observable.empty(), linesViewHolder);
            updateLines();
            getView().setLinesAdapter(linesAdapter);
            lineChangeListener = orderLineRealms -> Presenter.this.updateLines();
            currentOrder.getLines().addChangeListener(lineChangeListener);


            orderChangeListener = (realmModel, changeSet) -> {
                if (changeSet == null) {
                    return;
                }
                if (changeSet.isDeleted()) {
                    getView().viewOnBackPressed();
                }
                if (changeSet.isFieldChanged("comments")) {
                    getView().setComment(currentOrder.getComments());
                }
                if (changeSet.isFieldChanged("currency")) {
                    getView().setCurrency(currentOrder.getCurrency());
                }
                if (changeSet.isFieldChanged("delivery")) {
                    getView().setDeliveryDate(currentOrder.getDelivery());
                }
                if (changeSet.isFieldChanged("date")) {
                    getView().setOrderDate(currentOrder.getDate());
                }
                if (changeSet.isFieldChanged("payment")) {
                    getView().setOrderType(currentOrder.getPayment());
                }
                if (changeSet.isFieldChanged("status")) {
                    getView().setStatus(currentOrder.getStatus());
                    initActionBar();
                }
            };
            currentOrder.addChangeListener(orderChangeListener);
        }

        private void updateLines(){
            linesAdapter.refreshList(mModel.getLinesList(currentOrder));
            getView().setOrderAmount(currentOrder.getTotal());
        }


        @Override
        public void dropView(OrderView view) {
            currentOrder.removeChangeListener(orderChangeListener);
            currentOrder.getLines().removeChangeListener(lineChangeListener);
            super.dropView(view);
        }

        @Override
        protected void initActionBar() {
            RootPresenter.ActionBarBuilder abb = mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .setBackArrow(true)
                    .setTitle(currentOrder == null ? "" : currentOrder.getCustomer().getName());
            if (currentOrder.getStatus() == ConstantManager.ORDER_STATUS_CART) {
                abb.addAction(new MenuItemHolder(App.getStringRes(R.string.action_send_order), R.drawable.ic_send, item -> {
                    if (currentOrder.getLines().isEmpty()){
                        if (getRootView() != null) {
                            getRootView().showMessage(App.getStringRes(R.string.error_empty_order));
                            return false;
                        }
                    }
                    if (currentOrder.getDate().compareTo(currentOrder.getDelivery())>0){
                        if (getRootView() != null) {
                            getRootView().showMessage(App.getStringRes(R.string.error_wrong_delivery));
                            return false;
                        }
                    }
                    mModel.sendOrder(currentOrder);
                    return false;
                }, ConstantManager.MENU_ITEM_TYPE_ACTION))
                .addAction(new MenuItemHolder(App.getStringRes(R.string.action_clear_order), R.drawable.ic_clear_all, item -> {
                    mModel.clearOrderLines(currentOrder);
                    return false;
                }, ConstantManager.MENU_ITEM_TYPE_ITEM));

            }

            abb.build();

        }


        public void updateFields() {

            getView().setComment(currentOrder.getComments());
            getView().setCurrency(currentOrder.getCurrency());
            getView().setDeliveryDate(currentOrder.getDelivery());
            getView().setOrderAmount(currentOrder.getTotal());
            getView().setOrderDate(currentOrder.getDate());
            getView().setOrderType(currentOrder.getPayment());
            getView().setStatus(currentOrder.getStatus());

        }

        public void updateDeliveryDate(Date mDate) {
            mModel.setDeliveryDate(currentOrder, mDate);
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

                alert.setPositiveButton(App.getStringRes(R.string.button_positive_text), (dialog, whichButton) -> {
                    float newValue = Float.parseFloat(input.getText().toString().replace(",","."));
                    //check price
                    if (newValue < line.getItem().getLowPrice()) {
                        if (getRootView() != null) {
                            getRootView().showMessage(App.getStringRes(R.string.error_low_price) + " (" + String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), line.getItem().getLowPrice()) + ")");
                        }
                    } else {
                        mModel.updateOrderItemPrice(currentOrder, line.getItem(), newValue);
                    }
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
                    float newValue = Float.parseFloat(input.getText().toString());
                    if (newValue == 0f) {
                        mModel.removeOrderItem(currentOrder, line.getItem());
                    } else {
                        mModel.updateOrderItemQty(currentOrder, line.getItem(), newValue);
                    }
                });
                alert.setNegativeButton(App.getStringRes(R.string.button_negative_text), (dialog, whichButton) -> {
                });
                alert.show();
            }
        }

        public void removeLine(OrderLineRealm currentItem) {
            if (currentOrder.getStatus() == ConstantManager.ORDER_STATUS_CART) {
                mModel.removeOrderItem(currentOrder, currentItem.getItem());
            }
        }

        public void updatePayment(int payment) {
            mModel.updateOrderPayment(currentOrder, payment);
        }


        public void updateComment(String comment) {
            mModel.updateOrderComment(currentOrder, comment);
        }

        public void addNewItemToOrder() {
            if (getRootView() != null) {
                Flow.get((RootActivity)getRootView()).set(new GoodsScreen(currentOrder.getId()));
            }
        }
    }
    //endregion ================== Presenter =========================


}
