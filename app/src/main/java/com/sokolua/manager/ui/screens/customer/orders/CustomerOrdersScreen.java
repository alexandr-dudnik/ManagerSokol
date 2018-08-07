package com.sokolua.manager.ui.screens.customer.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.OrderPlanRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.CustomerModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.ui.screens.customer.CustomerScreen;
import com.sokolua.manager.utils.ReactiveRecyclerAdapter;

import javax.inject.Inject;

import dagger.Provides;
import mortar.MortarScope;

@Screen(R.layout.screen_customer_orders)
public class CustomerOrdersScreen extends AbstractScreen<CustomerScreen.Component> {

    @Override
    public Object createScreenComponent(CustomerScreen.Component parentComponent) {
        return DaggerCustomerOrdersScreen_Component
                .builder()
                .module(new Module())
                .component(parentComponent)
                .build();
    }

    public CustomerOrdersScreen(){
    }

    //region ===================== DI =========================

    @dagger.Module
    class Module {

        @Provides
        @DaggerScope(CustomerOrdersScreen.class)
        CustomerModel provideCustomerModel() {
            return new CustomerModel();
        }

        @Provides
        @DaggerScope(CustomerOrdersScreen.class)
        Presenter providePresenter() {
            return new Presenter();
        }

    }

    @dagger.Component(dependencies = CustomerScreen.Component.class, modules = Module.class)
    @DaggerScope(CustomerOrdersScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(CustomerOrdersView view);

        void inject(CustomerPlanViewHolder viewHolder);

        void inject(CustomerOrderViewHolder viewHolder);


    }
    //endregion ================== DI =========================

    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<CustomerOrdersView, CustomerModel> {
        @Inject
        protected CustomerRealm mCustomer;


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

            //Plan realm adapter
            ReactiveRecyclerAdapter.ReactiveViewHolderFactory<OrderPlanRealm> planViewAndHolderFactory = (parent, pViewType) -> {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_plan_item, parent, false);
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new CustomerPlanViewHolder(view)
                );
            };
            ReactiveRecyclerAdapter mPlanAdapter = new ReactiveRecyclerAdapter(mModel.getCustomerPlan(mCustomer.getCustomerId()), planViewAndHolderFactory);
            getView().setPlanAdapter(mPlanAdapter);

            //Orders realm adapter
            ReactiveRecyclerAdapter.ReactiveViewHolderFactory<OrderRealm> orderViewAndHolderFactory = (parent, pViewType) -> {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_order_item, parent, false);
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new CustomerOrderViewHolder(view)
                );
            };
            ReactiveRecyclerAdapter mOrderAdapter = new ReactiveRecyclerAdapter(mModel.getCustomerOrders(mCustomer.getCustomerId()), orderViewAndHolderFactory);
            getView().setOrdersAdapter(mOrderAdapter);
        }

        @Override
        protected void initActionBar() {

        }


    }
}
