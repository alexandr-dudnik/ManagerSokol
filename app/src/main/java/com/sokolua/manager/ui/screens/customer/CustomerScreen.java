package com.sokolua.manager.ui.screens.customer;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.CustomerModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.mvp.presenters.MenuItemHolder;
import com.sokolua.manager.mvp.presenters.RootPresenter;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.screens.customer_list.CustomerListScreen;
import com.sokolua.manager.ui.screens.order.OrderScreen;
import com.sokolua.manager.utils.App;

import dagger.Provides;
import flow.Flow;
import flow.TreeKey;
import io.realm.RealmObjectChangeListener;
import mortar.MortarScope;

@Screen(R.layout.screen_customer)
public class CustomerScreen extends AbstractScreen<RootActivity.RootComponent>  implements TreeKey {
    private CustomerRealm mCustomer;


    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerCustomerScreen_Component.builder()
                .module(new Module())
                .rootComponent(parentComponent)
                .build();
    }

    public CustomerScreen(String customerId) {
        mCustomer = DataManager.getInstance().getCustomerById(customerId);
    }

    @Override
    public String getScopeName() {
        return super.getScopeName()+"_"+mCustomer.getCustomerId();
    }


    //region ===================== DI =========================

    @dagger.Module
    class Module {

        @Provides
        @DaggerScope(CustomerScreen.class)
        CustomerModel provideCustomerModel() {
            return new CustomerModel();
        }

        @Provides
        @DaggerScope(CustomerScreen.class)
        Presenter providePresenter() {
            return new Presenter();
        }

        @Provides
        @DaggerScope(CustomerScreen.class)
        CustomerRealm provideCustomer() {
            return mCustomer;
        }
    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(CustomerScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(CustomerView view);

        void inject(CustomerRealm customer);

        RootPresenter getRootPresenter();

        CustomerRealm getCustomer();
    }
    //endregion ================== DI =========================

    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<CustomerView, CustomerModel>{

        private RealmObjectChangeListener<CustomerRealm> customerChangeListener;

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

            customerChangeListener = (realmModel, changeSet) -> {
                if (!realmModel.isLoaded() || !realmModel.isValid() || changeSet != null && changeSet.isDeleted()) {
                    getView().viewOnBackPressed();
                }
            };
            mCustomer.addChangeListener(customerChangeListener);
        }

        @Override
        public void dropView(CustomerView view) {
            mCustomer.removeChangeListener(customerChangeListener);
            super.dropView(view);
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .setBackArrow(true)
                    .setTitle(mCustomer.getName())
                    .setTabs(getView().getViewPager())
                    .addAction(new MenuItemHolder(App.getStringRes(R.string.cart_title), R.drawable.ic_cart, item ->{
                                OrderRealm cart = mModel.getCartForCustomer(mCustomer.getCustomerId());
                                Flow.get(getView()).set(new OrderScreen(cart.getId()));
                                return false;
                            } , ConstantManager.MENU_ITEM_TYPE_ACTION))
                    .addAction(new MenuItemHolder(App.getStringRes(R.string.menu_synchronize), R.drawable.ic_sync, item ->{
                        mModel.updateCustomerFromRemote(mCustomer.getCustomerId());
                        return false;
                    } , ConstantManager.MENU_ITEM_TYPE_ITEM))
                    .build();

        }
    }
    //endregion ================== Presenter =========================

    //region ===================== TreeKey =========================

    @NonNull
    @Override
    public Object getParentKey() {
        return new CustomerListScreen();
    }

    //endregion ================== TreeKey =========================    }
}
