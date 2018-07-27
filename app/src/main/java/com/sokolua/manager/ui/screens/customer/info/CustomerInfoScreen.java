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
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.IntentStarter;

import javax.inject.Inject;

import dagger.Provides;
import mortar.MortarScope;

@Screen(R.layout.screen_customer_info)
public class CustomerInfoScreen extends AbstractScreen<CustomerScreen.Component> {

    @Override
    public Object createScreenComponent(CustomerScreen.Component parentComponent) {
        return DaggerCustomerInfoScreen_Component
                .builder()
                .module(new Module())
                .component(parentComponent)
                .build();
    }

    public CustomerInfoScreen() {
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

        void inject(CustomerInfoNoteAdapter noteAdapter);

        void inject(CustomerInfoDataAdapter dataAdapter);
    }
    //endregion ================== DI =========================

    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<CustomerInfoView, CustomerModel> {
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
        }

        @Override
        protected void initActionBar() {

        }


        public void callToCustomer(CustomerInfoDataItem mItem) {
            if (!IntentStarter.openCaller(mItem.getActionData()) && getRootView() != null) {
                getRootView().showMessage(App.getStringRes(R.string.error_phone_not_available));
            }
        }

        public void openMap(CustomerInfoDataItem mItem) {
            if (!IntentStarter.openMap(mItem.getActionData()) && getRootView() != null) {
                getRootView().showMessage(App.getStringRes(R.string.error_google_maps_not_found));
            }
        }

        public void sendEmail(CustomerInfoDataItem mItem) {
            if (!IntentStarter.composeEmail(mItem.getActionData()) && getRootView() != null) {
                getRootView().showMessage(App.getStringRes(R.string.error_email_not_available));
            }
        }
    }
}
