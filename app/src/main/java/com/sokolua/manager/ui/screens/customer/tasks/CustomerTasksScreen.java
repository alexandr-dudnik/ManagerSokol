package com.sokolua.manager.ui.screens.customer.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.CustomerModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.ui.screens.customer.CustomerScreen;

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
//        private RealmChangeListener<RealmResults<DebtRealm>> mDebtListener;
//        private RealmChangeListener<RealmResults<TaskRealm>> mTasksListener;


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



            if (mCustomer!= null && mCustomer.isValid()) {
                //Debt realm adapter
                ReactiveRecyclerAdapter.ReactiveViewHolderFactory<CustomerDebtItem> debtViewAndHolderFactory = (parent, pViewType) -> {
                    View view;
                    switch (pViewType) {
                        case ConstantManager.RECYCLER_VIEW_TYPE_HEADER:
                            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_debt_header, parent, false);
                            break;
                        case ConstantManager.RECYCLER_VIEW_TYPE_ITEM:
                            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_debt_item, parent, false);
                            break;
                        default:
                            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_list_item, parent, false);
                    }
                    return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                            view,
                            new CustomerDebtViewHolder(view)
                    );
                };

                ReactiveRecyclerAdapter<CustomerDebtItem> mDebtAdapter = new ReactiveRecyclerAdapter<>(
                        mModel.getCustomerDebtHeadered(mCustomer.getCustomerId()),
                        debtViewAndHolderFactory,
                        true
                );
                getView().setDebtAdapter(mDebtAdapter);


                //Task Realm adapter
                ReactiveRecyclerAdapter.ReactiveViewHolderFactory<CustomerTaskItem> taskViewAndHolderFactory = (parent, pViewType) -> {
                    View view;
                    switch (pViewType) {
                        case ConstantManager.RECYCLER_VIEW_TYPE_HEADER:
                            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_task_header, parent, false);
                            break;
                        case ConstantManager.RECYCLER_VIEW_TYPE_ITEM:
                            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_task_item, parent, false);
                            break;
                        default:
                            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_list_item, parent, false);
                    }
                    return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                            view,
                            new CustomerTaskViewHolder(view)
                    );
                };
                ReactiveRecyclerAdapter<CustomerTaskItem> mTaskAdapter = new ReactiveRecyclerAdapter<>(
                        mModel.getCustomerTasksHeadered(mCustomer.getCustomerId()),
                        taskViewAndHolderFactory,
                        true
                );
                getView().setTaskAdapter(mTaskAdapter);

            }

        }

        @Override
        protected void initActionBar() {

        }



        void updateTask(String taskId, boolean checked, String result) {
            mModel.updateTask(taskId, checked, result);
        }
    }
}
