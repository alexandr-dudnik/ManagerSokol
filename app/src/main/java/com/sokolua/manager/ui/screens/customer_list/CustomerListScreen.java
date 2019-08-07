package com.sokolua.manager.ui.screens.customer_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.CustomerListModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.mvp.presenters.MenuItemHolder;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.ui.screens.customer.CustomerScreen;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.IntentStarter;

import dagger.Provides;
import flow.Flow;
import io.reactivex.Observable;
import mortar.MortarScope;

@Screen(R.layout.screen_customer_list)
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

        void inject(CustomerViewHolder viewHolder);
    }
    //endregion ================== DI =========================


    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<CustomerListView, CustomerListModel> {

        ReactiveRecyclerAdapter.ReactiveViewHolderFactory<CustomerListItem> viewAndHolderFactory;
        ReactiveRecyclerAdapter reactiveRecyclerAdapter;

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

            viewAndHolderFactory = (parent, pViewType) -> {
                View view;
                if (pViewType == ConstantManager.RECYCLER_VIEW_TYPE_HEADER) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_list_header, parent, false);
                }else{
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_list_item, parent, false);
                }
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new CustomerViewHolder(view)
                );
            };

            reactiveRecyclerAdapter = new ReactiveRecyclerAdapter(Observable.empty(), viewAndHolderFactory, false);
            getView().setAdapter(reactiveRecyclerAdapter);
            setCustomerListFilter("");
        }

        public void setCustomerListFilter(String filter){
            reactiveRecyclerAdapter.refreshList(mModel.getCustomerListHeadered(filter));
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .addAction(new MenuItemHolder(App.getStringRes(R.string.menu_search), new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            setCustomerListFilter(query);
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            setCustomerListFilter(newText);
                            return true;
                        }
                    }))
                    .setTitle(App.getStringRes(R.string.menu_customers))
                    .build();

        }


        //region ===================== Event Actions =========================

        public void openCustomerMap(CustomerRealm customer){
            if (!IntentStarter.openMap(customer.getAddress()) && getRootView() != null) {
                getRootView().showMessage(App.getStringRes(R.string.error_google_maps_not_found));
            }
        }

        public void callToCustomer(CustomerRealm customer) {
            if (!IntentStarter.openCaller(customer.getPhone()) && getRootView()!= null){
                getRootView().showMessage(App.getStringRes(R.string.error_phone_not_available));
            }
        }

        public void openCustomerCard(CustomerRealm customer){
            Flow.get(getView().getContext()).set(new CustomerScreen(customer.getCustomerId()));
        }

        //endregion ================== Event Actions =========================
    }

    //endregion ================== Presenter =========================

}
