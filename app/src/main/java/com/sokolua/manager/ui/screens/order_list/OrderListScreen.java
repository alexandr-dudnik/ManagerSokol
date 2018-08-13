package com.sokolua.manager.ui.screens.order_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.OrderListModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.ui.screens.order.OrderScreen;
import com.sokolua.manager.utils.App;

import dagger.Provides;
import flow.Direction;
import flow.Flow;
import mortar.MortarScope;

@Screen(R.layout.screen_order_list)
public class OrderListScreen extends AbstractScreen<RootActivity.RootComponent>{

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerOrderListScreen_Component.builder()
                .module(new Module())
                .rootComponent(parentComponent)
                .build();
    }


    //region ===================== DI =========================

    @dagger.Module
    class Module {

        @Provides
        @DaggerScope(OrderListScreen.class)
        OrderListModel provideOrderListModel() {
            return new OrderListModel();
        }

        @Provides
        @DaggerScope(OrderListScreen.class)
        Presenter providePresenter() {
            return new Presenter();
        }

    }


    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(OrderListScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(OrderListView view);

        void inject(OrderViewHolder viewHolder);
    }
    //endregion ================== DI =========================


    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<OrderListView, OrderListModel> {

        ReactiveRecyclerAdapter.ReactiveViewHolderFactory<OrderRealm> viewAndHolderFactory;

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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false);
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new OrderViewHolder(view)
                );
            };

            setOrderListFilter("");
        }

        public void setOrderListFilter(String filter){

            ReactiveRecyclerAdapter reactiveRecyclerAdapter = new ReactiveRecyclerAdapter(mModel.getOrderList(), viewAndHolderFactory);

            getView().setAdapter(reactiveRecyclerAdapter);
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
//                    .addAction(new MenuItemHolder(App.getStringRes(R.string.menu_search), R.drawable.ic_search, new SearchView.OnQueryTextListener() {
//                        @Override
//                        public boolean onQueryTextSubmit(String query) {
//                            setOrderListFilter(query);
//                            return true;
//                        }
//
//                        @Override
//                        public boolean onQueryTextChange(String newText) {
//                            setOrderListFilter(newText);
//                            return true;
//                        }
//                    }, ConstantManager.MENU_ITEM_TYPE_SEARCH))
                    .setTitle(App.getStringRes(R.string.menu_orders))
                    .build();

        }


        public void openOrder(OrderRealm order) {
            Flow.get(getView()).replaceHistory(new OrderScreen(order), Direction.FORWARD);
        }
    }

    //endregion ================== Presenter =========================

}
