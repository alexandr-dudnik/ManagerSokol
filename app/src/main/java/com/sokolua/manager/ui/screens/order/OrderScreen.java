package com.sokolua.manager.ui.screens.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.OrderModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;

import java.util.Date;

import javax.annotation.Nullable;

import dagger.Provides;
import io.reactivex.Observable;
import io.realm.ObjectChangeSet;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
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

            getView().setLinesAdapter(linesAdapter);

            currentOrder.addChangeListener(new RealmObjectChangeListener<RealmModel>() {
                @Override
                public void onChange(RealmModel realmModel, @Nullable ObjectChangeSet changeSet) {
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
                    if (changeSet.isFieldChanged("total")) {
                        getView().setOrderAmount(currentOrder.getTotal());
                    }
                    if (changeSet.isFieldChanged("date")) {
                        getView().setOrderDate(currentOrder.getDate());
                    }
                    if (changeSet.isFieldChanged("payment")) {
                        getView().setOrderType(currentOrder.getPayment());
                    }
                    if (changeSet.isFieldChanged("status")) {
                        getView().setStatus(currentOrder.getStatus());
                    }
                }
            });
        }


        @Override
        public void dropView(OrderView view) {
            currentOrder.removeAllChangeListeners();
            super.dropView(view);
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .setBackArrow(true)
                    .setTitle(currentOrder==null?"":currentOrder.getCustomer().getName())
                    .build();

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
    }
    //endregion ================== Presenter =========================

}
