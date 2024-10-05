package com.sokolua.manager.ui.screens.customer.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.databinding.CustomerDebtHeaderBinding;
import com.sokolua.manager.databinding.CustomerDebtItemBinding;
import com.sokolua.manager.databinding.CustomerTaskHeaderBinding;
import com.sokolua.manager.databinding.CustomerTaskItemBinding;
import com.sokolua.manager.databinding.EmptyListItemBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.mvp.models.CustomerModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.ui.screens.customer.CustomerScreen;

import javax.inject.Inject;

import dagger.Provides;
import mortar.MortarScope;

public class CustomerTasksScreen extends AbstractScreen<CustomerScreen.Component> {

    @Override
    public Object createScreenComponent(CustomerScreen.Component parentComponent) {
        return DaggerCustomerTasksScreen_Component
                .builder()
                .module(new Module())
                .component(parentComponent)
                .build();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.screen_customer_tasks;
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
    }
    //endregion ================== DI =========================

    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<CustomerTasksView, CustomerModel> {
        @Inject
        protected CustomerRealm mCustomer;

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);

            if (mCustomer != null && mCustomer.isValid()) {
                //Debt realm adapter
                ReactiveRecyclerAdapter.ReactiveViewHolderFactory<CustomerDebtItem> debtViewAndHolderFactory = (parent, pViewType) -> {
                    final View view;
                    final CustomerDebtViewHolder<?> holder = switch (pViewType) {
                        case ConstantManager.RECYCLER_VIEW_TYPE_HEADER:
                            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_debt_header, parent, false);
                            yield new CustomerDebtViewHolder<CustomerDebtHeaderBinding>(view, CustomerDebtHeaderBinding.bind(view));
                        case ConstantManager.RECYCLER_VIEW_TYPE_ITEM:
                            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_debt_item, parent, false);
                            yield new CustomerDebtViewHolder<CustomerDebtItemBinding>(view, CustomerDebtItemBinding.bind(view));
                        default:
                            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_list_item, parent, false);
                            yield new CustomerDebtViewHolder<EmptyListItemBinding>(view, EmptyListItemBinding.bind(view));
                    };
                    return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(view, holder);
                };

                ReactiveRecyclerAdapter<CustomerDebtItem> mDebtAdapter = new ReactiveRecyclerAdapter<>(
                        mModel.getCustomerDebtHeadered(mCustomer.getCustomerId()),
                        debtViewAndHolderFactory,
                        true
                );
                getView().setDebtAdapter(mDebtAdapter);

                //Task Realm adapter
                ReactiveRecyclerAdapter.ReactiveViewHolderFactory<CustomerTaskItem> taskViewAndHolderFactory = (parent, pViewType) -> {
                    final View view;
                    final CustomerTaskViewHolder<?> holder = switch (pViewType) {
                        case ConstantManager.RECYCLER_VIEW_TYPE_HEADER:
                            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_task_header, parent, false);
                            yield new CustomerTaskViewHolder<CustomerTaskHeaderBinding>(view, CustomerTaskHeaderBinding.bind(view), null);
                        case ConstantManager.RECYCLER_VIEW_TYPE_ITEM:
                            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_task_item, parent, false);
                            yield new CustomerTaskViewHolder<CustomerTaskItemBinding>(
                                    view,
                                    CustomerTaskItemBinding.bind(view),
                                    (id, done, comment) -> {
                                        updateTask(id, done, comment);
                                        return true;
                                    }
                            );
                        default:
                            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_list_item, parent, false);
                            yield new CustomerTaskViewHolder<EmptyListItemBinding>(view, EmptyListItemBinding.bind(view), null);
                    };
                    return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(view, holder);
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
