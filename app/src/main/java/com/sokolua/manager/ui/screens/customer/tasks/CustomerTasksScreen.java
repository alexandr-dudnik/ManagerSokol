package com.sokolua.manager.ui.screens.customer.tasks;

import android.os.Bundle;
import android.util.ArrayMap;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.dto.CustomerDto;
import com.sokolua.manager.data.storage.dto.DebtDto;
import com.sokolua.manager.data.storage.dto.TaskDto;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.CustomerModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.ui.screens.customer.CustomerScreen;
import com.sokolua.manager.utils.App;

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

        void inject(CustomerDebtAdapter adapter);

        void inject(CustomerTaskAdapter adapter);
    }
    //endregion ================== DI =========================

    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<CustomerTasksView, CustomerModel> {
        @Inject
        protected CustomerDto mCustomerDto;


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

            Map<String, Float> wholeDebt = new HashMap<>();

            CustomerDebtAdapter mDebtAdapter = getView().getDebtAdapter();
            CustomerTaskAdapter mTaskAdapter = getView().getTaskAdapter();

            if (mCustomerDto.getDebt().size() == 0) {
                mDebtAdapter.addHeader(App.getStringRes(R.string.customer_debt_no_debt), ConstantManager.DEBT_TYPE_NO_DEBT);
            }else {
                mDebtAdapter.addHeader(App.getStringRes(R.string.customer_debt_outdated), ConstantManager.DEBT_TYPE_OUTDATED);
                for (DebtDto item : mCustomerDto.getDebt()) {
                    if (item.isOutdated()) {
                        mDebtAdapter.addItem(item.getCurrency(), item.getAmount(), ConstantManager.DEBT_TYPE_OUTDATED);
                        Float sum = wholeDebt.get(item.getCurrency());
                        if (sum == null) {
                            sum = 0f;
                        }
                        sum += item.getAmount();
                        wholeDebt.put(item.getCurrency(), sum);
                    }

                }
                mDebtAdapter.addHeader(App.getStringRes(R.string.customer_debt_normal), ConstantManager.DEBT_TYPE_NORMAL);
                for (DebtDto item : mCustomerDto.getDebt()) {
                    if (!item.isOutdated()) {
                        mDebtAdapter.addItem(item.getCurrency(), item.getAmount(), ConstantManager.DEBT_TYPE_NORMAL);
                        Float sum = wholeDebt.get(item.getCurrency());
                        if (sum == null) {
                            sum = 0f;
                        }
                        sum += item.getAmount();
                        wholeDebt.put(item.getCurrency(), sum);
                    }

                }
                mDebtAdapter.addHeader(App.getStringRes(R.string.customer_debt_whole), ConstantManager.DEBT_TYPE_WHOLE);
                for (Map.Entry<String, Float> entry : wholeDebt.entrySet()) {
                    mDebtAdapter.addItem(entry.getKey(), entry.getValue(), ConstantManager.DEBT_TYPE_WHOLE);
                }

            }

            if (mCustomerDto.getTasks().size() == 0){
                mTaskAdapter.addItem(App.getStringRes(R.string.customer_task_no_tasks), ConstantManager.TASK_TYPE_RESEARCH, true);
            }else{
                mTaskAdapter.addItem(App.getStringRes(R.string.customer_task_research), ConstantManager.TASK_TYPE_RESEARCH, true);
                for (TaskDto task: mCustomerDto.getTasks()){
                    if (task.getTaskType() == ConstantManager.TASK_TYPE_RESEARCH){
                        mTaskAdapter.addItem(task.getText(), task.getTaskType(), false);
                    }
                }
                mTaskAdapter.addItem(App.getStringRes(R.string.customer_task_individual), ConstantManager.TASK_TYPE_RESEARCH, true);
                for (TaskDto task: mCustomerDto.getTasks()){
                    if (task.getTaskType() == ConstantManager.TASK_TYPE_INDIVIDUAL){
                        mTaskAdapter.addItem(task.getText(), task.getTaskType(), false);
                    }
                }
            }


            getView().showData();
        }

        @Override
        protected void initActionBar() {

        }

    }
}
