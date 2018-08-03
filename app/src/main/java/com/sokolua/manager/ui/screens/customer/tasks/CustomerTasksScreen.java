package com.sokolua.manager.ui.screens.customer.tasks;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.dto.CustomerDto;
import com.sokolua.manager.data.storage.dto.DebtDto;
import com.sokolua.manager.data.storage.dto.TaskDto;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.CustomerModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.ui.screens.cust_list.CustomerViewHolder;
import com.sokolua.manager.ui.screens.customer.CustomerScreen;
import com.sokolua.manager.ui.screens.customer.info.CustomerNoteViewHolder;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.ReactiveRecyclerAdapter;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.Provides;
import mortar.MortarScope;

@Screen(R.layout.screen_customer_tasks)
public class CustomerTasksScreen extends AbstractScreen<CustomerScreen.Component> {

    @Override
    public Object createScreenComponent(CustomerScreen.Component parentComponent) {
        return DaggerCustomerTasksScreen_Component
                .builder()
                .module(new Module())
                .component(parentComponent)
                .build();
    }

    public CustomerTasksScreen() {
    }

    //region ===================== DI =========================

    @dagger.Module
    class Module {

        @Provides
        @DaggerScope(CustomerTasksScreen.class)
        CustomerModel provideCustomerModel() {
            return new CustomerModel();
        }

        @Provides
        @DaggerScope(CustomerTasksScreen.class)
        Presenter providePresenter() {
            return new Presenter();
        }

    }

    @dagger.Component(dependencies = CustomerScreen.Component.class, modules = Module.class)
    @DaggerScope(CustomerTasksScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(CustomerTasksView view);

        void inject(CustomerDebtViewHolder viewHolder);

        void inject(CustomerTaskViewHolder viewHolder);
    }
    //endregion ================== DI =========================

    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<CustomerTasksView, CustomerModel> {
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



            //Debt realm adapter
            ReactiveRecyclerAdapter.ReactiveViewHolderFactory<CustomerDebtItem> debtViewAndHolderFactory = (parent, pViewType) -> {
                View view;
                if (pViewType == ConstantManager.RECYCLER_VIEW_TYPE_HEADER) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_debt_header, parent, false);
                }else{
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_debt_item, parent, false);
                }
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new CustomerDebtViewHolder(view)
                );
            };
            ReactiveRecyclerAdapter mDebtAdapter = new ReactiveRecyclerAdapter(mModel.getCustomerDebt(mCustomer.getCustomerId()), debtViewAndHolderFactory);
            getView().setDebtAdapter(mDebtAdapter);


            //Task Realm adapter
            ReactiveRecyclerAdapter.ReactiveViewHolderFactory<CustomerTaskItem> taskViewAndHolderFactory = (parent, pViewType) -> {
                View view;
                if (pViewType == ConstantManager.RECYCLER_VIEW_TYPE_HEADER) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_task_header, parent, false);
                }else{
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_task_item, parent, false);
                }
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new CustomerTaskViewHolder(view)
                );
            };
            ReactiveRecyclerAdapter mTaskAdapter = new ReactiveRecyclerAdapter(mModel.getCustomerTasks(mCustomer.getCustomerId()), taskViewAndHolderFactory);
            getView().setTaskAdapter(mTaskAdapter);


        }

        @Override
        protected void initActionBar() {

        }

    }
}
