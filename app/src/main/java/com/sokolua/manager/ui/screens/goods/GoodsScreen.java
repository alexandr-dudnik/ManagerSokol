package com.sokolua.manager.ui.screens.goods;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.GoodsModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.mvp.presenters.MenuItemHolder;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.ui.screens.order.OrderScreen;
import com.sokolua.manager.utils.App;

import java.util.Locale;

import dagger.Provides;
import flow.Flow;
import io.reactivex.Observable;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import mortar.MortarScope;

@Screen(R.layout.screen_goods)
public class GoodsScreen extends AbstractScreen<RootActivity.RootComponent>{
    private String mCustomerOrderId = null;

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerGoodsScreen_Component.builder()
                .module(new Module())
                .rootComponent(parentComponent)
                .build();
    }


    //region ===================== DI =========================

    @dagger.Module
    class Module {

        @Provides
        @DaggerScope(GoodsScreen.class)
        GoodsModel provideGoodsModel() {
            return new GoodsModel();
        }

        @Provides
        @DaggerScope(GoodsScreen.class)
        Presenter providePresenter() {
            return new Presenter();
        }

        @Provides
        @DaggerScope(GoodsScreen.class)
        String provideCustomerCart() {
            return mCustomerOrderId;
        }
    }


    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(GoodsScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(GoodsView view);

        void inject(GroupViewHolder viewHolder);

        void inject(ItemViewHolder viewHolder);

//        OrderRealm  getCustomerCart();
    }

    public GoodsScreen() {
        super();
    }

    public GoodsScreen(String orderId) {
        super();
        this.mCustomerOrderId = orderId;
    }

    @Override
    public String getScopeName() {
        return super.getScopeName()+(mCustomerOrderId==null||mCustomerOrderId.isEmpty()?"":("_"+mCustomerOrderId));
    }

    //endregion ================== DI =========================


    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<GoodsView, GoodsModel> {

        OrderRealm currentCart;

        GoodsGroupRealm currentGroup = null;

        ReactiveRecyclerAdapter.ReactiveViewHolderFactory<GoodsGroupRealm> groupViewHolder;
        ReactiveRecyclerAdapter.ReactiveViewHolderFactory<ItemRealm> itemViewHolder;
        ReactiveRecyclerAdapter groupsAdapter;
        ReactiveRecyclerAdapter itemsAdapter;
        private RealmObjectChangeListener<RealmModel> orderChangeListener;
        private RealmChangeListener<RealmResults<OrderLineRealm>> orderLinesChangeListener;

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

            if (mCustomerOrderId!=null && !mCustomerOrderId.isEmpty()) {
                currentCart = mModel.getOrderById(mCustomerOrderId);
            }


            groupViewHolder = (parent, pViewType) -> {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_groups_item, parent, false);
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new GroupViewHolder(view)
                );
            };
            groupsAdapter = new ReactiveRecyclerAdapter(Observable.empty(), groupViewHolder);

            getView().setGroupsAdapter(groupsAdapter);


            itemViewHolder = (parent, pViewType) -> {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_items_item, parent, false);
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new ItemViewHolder(view)
                );
            };
            itemsAdapter = new ReactiveRecyclerAdapter(Observable.empty(), itemViewHolder);

            getView().setItemsAdapter(itemsAdapter);

            setGoodsListFilter("");



            if (currentCart != null){
                getView().setCartMode();
                updateFields();

                orderChangeListener = (realmModel, changeSet) -> {
                    if (changeSet == null) {
                        return;
                    }
                    if (changeSet.isDeleted()) {
                        currentCart = null;
                        getView().setCatalogMode();
                    }
                    if (changeSet.isFieldChanged("status")) {
                        if (currentCart.getStatus() != ConstantManager.ORDER_STATUS_CART) {
                            currentCart = null;
                            getView().setCatalogMode();
                        }
                    }
                    if (changeSet.isFieldChanged("customer")) {
                        getView().setCustomer(currentCart.getCustomer().getName());
                    }
                    if (changeSet.isFieldChanged("currency")) {
                        getView().setCartCurrency(currentCart.getCurrency());
                    }
                };
                currentCart.addChangeListener(orderChangeListener);
                orderLinesChangeListener = orderLineRealms -> {
                            getView().setCartAmount(currentCart.getTotal());
                            getView().setCartItemsCount(currentCart.getLines().size());
                        };
                currentCart.getLines().addChangeListener(orderLinesChangeListener);

            }else{
                getView().setCatalogMode();
            }

        }

        @Override
        public void dropView(GoodsView view) {
            if (currentCart != null){
                currentCart.getLines().removeChangeListener(orderLinesChangeListener);
                currentCart.removeChangeListener(orderChangeListener);
                currentCart = null;
            }

            super.dropView(view);
        }

        public void updateFields() {

            getView().setCustomer(currentCart.getCustomer().getName());
            getView().setCartCurrency(currentCart.getCurrency());
            getView().setCartAmount(currentCart.getTotal());
            getView().setCartItemsCount(currentCart.getLines().size());

        }

        public void setGoodsListFilter(String filter){
            if ((currentGroup == null || currentGroup.getParent() == null) && (filter == null || filter.isEmpty())){
                groupsAdapter.refreshList(mModel.getGroupList(currentGroup));
                getView().showGroups();
            }else{
                itemsAdapter.refreshList(mModel.getItemList(currentGroup, filter));
                getView().showItems();
            }
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .setBackArrow(currentCart!=null)
                    .addAction(new MenuItemHolder(App.getStringRes(R.string.menu_search), R.drawable.ic_search, new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            setGoodsListFilter(query);
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            setGoodsListFilter(newText);
                            return true;
                        }
                    }, ConstantManager.MENU_ITEM_TYPE_SEARCH))
                    .setTitle(App.getStringRes(R.string.menu_goods))
                    .build();

        }


        public void mainGroupSelected(GoodsGroupRealm selectedGroup) {

            if (getRootView() != null) {
                currentGroup = selectedGroup;
                ((RootActivity)getRootView()).setBackArrow(true);
                ((RootActivity)getRootView()).setActionBarTitle(currentGroup.getName());
                setGoodsListFilter("");
            }

        }

        public boolean goGroupBack(){
            if (getRootView() != null) {
                if (currentGroup == null) {
                    return false;
                } else {
                    currentGroup = currentGroup.getParent();
                }
                if (currentGroup == null) {
                    if (currentCart == null) {
                        ((RootActivity) getRootView()).setBackArrow(false);
                    }
                    ((RootActivity) getRootView()).setActionBarTitle(App.getStringRes(R.string.menu_goods));
                }
                else{
                    ((RootActivity)getRootView()).setActionBarTitle(currentGroup.getName());
                }
                setGoodsListFilter("");
            }
            return true;
        }

        public void itemSelected(ItemRealm selectedItem) {
            if (currentCart == null){
                //TODO: make screen with item card
            }else{
                if (getRootView() != null) {
                    LayoutInflater layoutInflater = ((Activity)getRootView()).getLayoutInflater();
                    View view = layoutInflater.inflate(R.layout.add_item_to_cart, null);
                    EditText inputPrice = view.findViewById(R.id.item_price);
                    inputPrice.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format),selectedItem.getBasePrice()));
                    EditText inputQty  = view.findViewById(R.id.item_quantity);
                    inputQty.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int),1f));

                    AlertDialog.Builder alert = new AlertDialog.Builder(getView().getContext())
                            .setTitle(selectedItem.getName())
                            .setView(view);

                    alert.setPositiveButton(App.getStringRes(R.string.button_positive_text), (dialog, whichButton) -> {
                        float newPrice = Float.parseFloat(inputPrice.getText().toString());
                        float newQty = Float.parseFloat(inputQty.getText().toString());
                        //check price
                        if (newPrice < selectedItem.getLowPrice()) {
                            if (getRootView() != null) {
                                getRootView().showMessage(App.getStringRes(R.string.error_low_price) + " (" + String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), selectedItem.getLowPrice()) + ")");
                            }
                        } else{
                            if (newQty > 0){
                                mModel.addItemToCart(currentCart, selectedItem, newQty, newPrice);

                            }
                        }
                    });
                    alert.setNegativeButton(App.getStringRes(R.string.button_negative_text), (dialog, whichButton) -> {
                    });
                    alert.show();
                }

            }
        }

        public void returnToCart() {
            if (currentCart != null) {
                Flow.get(getView()).set(new OrderScreen(currentCart));
            }
        }
    }

    //endregion ================== Presenter =========================

}
