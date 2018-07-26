package com.sokolua.manager.ui.screens.customer.info;

import android.os.Bundle;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.dto.CustomerDto;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.CustomerModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.ui.screens.customer.CustomerScreen;

import dagger.Provides;
import mortar.MortarScope;

@Screen(R.layout.screen_customer_info)
public class CustomerInfoScreen extends AbstractScreen<CustomerScreen.Component> {
    private CustomerDto mCustomerDto;

    @Override
    public Object createScreenComponent(CustomerScreen.Component parentComponent) {
        return null;
    }

    public CustomerInfoScreen(CustomerDto customerDto) {
        mCustomerDto = customerDto;
    }

    //region ===================== DI =========================

    @dagger.Module
    class Module {

        @Provides
        @DaggerScope(CustomerInfoScreen.class)
        CustomerModel provideCustomerModel() {
            return new CustomerModel();
        }

        @Provides
        @DaggerScope(CustomerInfoScreen.class)
        Presenter providePresenter() {
            return new Presenter();
        }

    }

    @dagger.Component(dependencies = CustomerScreen.Component.class, modules = Module.class)
    @DaggerScope(CustomerInfoScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(CustomerInfoView view);

    }
    //endregion ================== DI =========================

    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<CustomerInfoView, CustomerModel> {


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

        }
    }
}
