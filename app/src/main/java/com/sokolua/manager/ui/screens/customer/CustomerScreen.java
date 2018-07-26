package com.sokolua.manager.ui.screens.customer;


import android.os.Bundle;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.dto.CustomerDto;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.CustomerModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.mvp.presenters.RootPresenter;
import com.sokolua.manager.ui.activities.RootActivity;

import dagger.Provides;
import mortar.MortarScope;

@Screen(R.layout.screen_cust_list)
public class CustomerScreen extends AbstractScreen<RootActivity.RootComponent> {
    private CustomerDto mCustomerDto;


    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerCustomerScreen_Component.builder()
                .module(new Module())
                .rootComponent(parentComponent)
                .build();
    }

    public CustomerScreen(CustomerDto customerDto) {
        mCustomerDto = customerDto;
    }

    //region ===================== DI =========================

    @dagger.Module
    class Module {

        @Provides
        @DaggerScope(CustomerScreen.class)
        CustomerModel provideCustomerListModel() {
            return new CustomerModel();
        }

        @Provides
        @DaggerScope(CustomerScreen.class)
        Presenter providePresenter() {
            return new Presenter();
        }

    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(CustomerScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(CustomerView view);

        RootPresenter getRootPresenter();
    }
    //endregion ================== DI =========================

    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<CustomerView, CustomerModel> {

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
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .setBackArrow(true)
                    .setTitle(mCustomerDto.getCustomerName())
                    .build();

        }
    }
    //endregion ================== Presenter =========================

}
