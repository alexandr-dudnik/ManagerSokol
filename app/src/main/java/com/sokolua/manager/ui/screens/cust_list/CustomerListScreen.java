package com.sokolua.manager.ui.screens.cust_list;

import android.os.Bundle;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.CustomerListModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.screens.customer.CustomerScreen;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.IntentStarter;

import javax.inject.Inject;

import dagger.Provides;
import flow.Flow;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import mortar.MortarScope;

@Screen(R.layout.screen_cust_list)
public class CustomerListScreen extends AbstractScreen<RootActivity.RootComponent>{

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerCustomerListScreen_Component.builder()
                .module(new Module())
                .rootComponent(parentComponent)
                .build();
    }


    //region ===================== DI =========================

    @dagger.Module
    class Module {

        @Provides
        @DaggerScope(CustomerListScreen.class)
        CustomerListModel provideCustomerListModel() {
            return new CustomerListModel();
        }

        @Provides
        @DaggerScope(CustomerListScreen.class)
        Presenter providePresenter() {
            return new Presenter();
        }

    }


    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(CustomerListScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(CustomerListView view);

    }
    //endregion ================== DI =========================


    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<CustomerListView, CustomerListModel> {

        @Inject
        CustomerListModel mModel;

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
            mCompSubs.add(subscribeOnCustomersRealmObs());

            getView().showCustomerList();
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .setTitle("Клиенты")
                    .build();

        }


        private Disposable subscribeOnCustomersRealmObs() {

            return subscribe(mModel.getCustomerList(getView().getCustomerFilter()), new RealmSubscriber());
        }

        private class RealmSubscriber extends ViewSubscriber<CustomerRealm> {
            CustomerListAdapter mAdapter = getView().getAdapter();

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {
                if (getRootView() != null) {
                    getRootView().showError(e);
                }
            }

            @Override
            public void onNext(CustomerRealm customerRealm) {
                mAdapter.addItem(new CustomerListItem(customerRealm));
            }
        }


        //List actions
        public void openCustomerMap(@NonNull CustomerListItem customer){
            if (!IntentStarter.openMap(customer.getAddress()) && getRootView() != null) {
                getRootView().showMessage(App.getStringRes(R.string.error_google_maps_not_found));
            }
        }

        public void callToCustomer(CustomerListItem customer) {
                if (!IntentStarter.openMap(customer.getPhone()) && getRootView()!= null){
                    getRootView().showMessage(App.getStringRes(R.string.error_phone_not_available));
                }
        }

        public void openCustomerCard(CustomerListItem customer){
            if (getRootView()!= null) {
                Flow.get(getView().getContext()).set(new CustomerScreen(mModel.getCustomerDtoById(customer.getCustomerId())));
            }
        }
    }
    //endregion ================== Presenter =========================

}
