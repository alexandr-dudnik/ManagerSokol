package com.sokolua.manager.ui.screens.goods;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.BrandsRealm;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import dagger.Provides;
import flow.Flow;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import mortar.MortarScope;

@Screen(R.layout.screen_goods)
public class GoodsScreen extends AbstractScreen<RootActivity.RootComponent>{
    private String mCustomerOrderId = null;
    private String mFilterCategoryId = null;

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
            return mCustomerOrderId==null?"":mCustomerOrderId;
        }

    }


    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(GoodsScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(GoodsView view);

        void inject(GroupViewHolder viewHolder);

        void inject(ItemViewHolder viewHolder);

        Picasso getPicasso();
//        OrderRealm  getCustomerCart();
    }

    public GoodsScreen() {
        super();
    }

    public GoodsScreen(String orderId) {
        super();
        this.mCustomerOrderId = orderId;
    }

    public GoodsScreen(String orderId, String filterCategoryId) {
        super();
        this.mCustomerOrderId = orderId;
        this.mFilterCategoryId = filterCategoryId;
    }

    @Override
    public String getScopeName() {
        return super.getScopeName()+(mCustomerOrderId==null||mCustomerOrderId.isEmpty()?"":("_"+mCustomerOrderId));
    }

    //endregion ================== DI =========================


    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<GoodsView, GoodsModel> {

        OrderRealm currentCart;
        CustomerRealm mCustomer;

        GoodsGroupRealm currentGroup = null;

        ReactiveRecyclerAdapter.ReactiveViewHolderFactory<GoodsGroupRealm> groupViewHolder;
        ReactiveRecyclerAdapter.ReactiveViewHolderFactory<ItemRealm> itemViewHolder;
        ReactiveRecyclerAdapter groupsAdapter;
        ReactiveRecyclerAdapter itemsAdapter;
        private RealmObjectChangeListener<RealmModel> orderChangeListener;
        private RealmChangeListener<RealmResults<OrderLineRealm>> orderLinesChangeListener;
        private RealmChangeListener<GoodsGroupRealm> groupsListener;
        private RealmChangeListener<ItemRealm> itemsListener;

        private String currentFilter = "";
        private String currentBrand = "";


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
                mCustomer = currentCart.getCustomer();
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

            groupsListener = goodsGroupRealm -> updateGoodsList();
            itemsListener = goodsGroupRealm -> updateGoodsList();

            updateGoodsList();



            if (currentCart != null){
                getView().setCartMode();
                updateCartFields();

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
                            if (!orderLineRealms.isLoaded() || !orderLineRealms.isValid()){
                                orderLineRealms.removeAllChangeListeners();
                            }else{
                                getView().setCartAmount(currentCart.getTotal());
                                getView().setCartItemsCount(currentCart.getLines().size());
                            }
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
            groupsListener = null;
            itemsListener = null;

            super.dropView(view);
        }

        void updateCartFields() {

            getView().setCustomer(currentCart.getCustomer().getName());
            getView().setCartCurrency(currentCart.getCurrency());
            getView().setCartAmount(currentCart.getTotal());
            getView().setCartItemsCount(currentCart.getLines().size());

        }

        void updateGoodsList(){
            if (getRootView() != null) {
                ((RootActivity) getRootView()).setBackArrow(currentGroup!=null||currentCart!=null);
                ((RootActivity) getRootView()).setActionBarTitle((currentGroup == null?App.getStringRes(R.string.menu_goods):currentGroup.getName())+(currentBrand.isEmpty()?"":" ("+currentBrand+")"));
            }

            if ((currentGroup == null || currentGroup.getParent() == null) && (currentFilter == null || currentFilter.isEmpty()) && (mFilterCategoryId == null || mFilterCategoryId.isEmpty())){
                groupsAdapter.refreshList(mModel.getGroupList(currentGroup, currentBrand));
                getView().showGroups();
            }else{
                itemsAdapter.refreshList(mModel.getItemList(currentGroup, currentFilter, currentBrand, mFilterCategoryId));
                getView().showItems();
            }
        }

        @Override
        protected void initActionBar() {

            MenuItem.OnMenuItemClickListener brandListener = item -> {
                if (getRootView() != null) {
                    MenuItem groupItem = ((RootActivity)getRootView()).getMainMenuItemParent(item.getItemId());
                    if (groupItem != null) {
                        groupItem.setTitle(String.format("%s: %s",App.getStringRes(R.string.menu_brands), item.getTitle()));
                    }
                }
                item.setChecked(true);
                currentBrand = item.getTitle().toString().equals(App.getStringRes(R.string.all_brands)) ? "" : item.getTitle().toString();
                updateGoodsList();
                return true;
            };

            MenuItemHolder brandsSubMenu = new MenuItemHolder(String.format("%s: %s",App.getStringRes(R.string.menu_brands), App.getStringRes(R.string.all_brands)), R.drawable.ic_brand, null, ConstantManager.MENU_ITEM_TYPE_ITEM);
            brandsSubMenu.addSubMenuItem(new MenuItemHolder(App.getStringRes(R.string.all_brands), brandListener, 1, true));
            for (BrandsRealm brand: mModel.getBrands()){
                brandsSubMenu.addSubMenuItem(new MenuItemHolder(brand.getName(), brandListener, 1, false));
            }

            mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .setBackArrow(currentCart!=null)
                    .addAction(new MenuItemHolder(App.getStringRes(R.string.menu_synchronize), R.drawable.ic_sync, syncClickCallback(), ConstantManager.MENU_ITEM_TYPE_ITEM))
                    .addAction(brandsSubMenu)
                    .addAction(new MenuItemHolder(App.getStringRes(R.string.menu_search), new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            currentFilter = query;
                            updateGoodsList();
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            currentFilter = newText;
                            updateGoodsList();
                            return true;
                        }
                    }))
                    .setTitle(App.getStringRes(R.string.menu_goods))
                    .build();

        }

        private MenuItem.OnMenuItemClickListener syncClickCallback() {
            return item -> {
                ArrayList<Observable<Boolean>> obs = new ArrayList<>();
                obs.add(mModel.updateAllGroupsFromRemote().map(result -> true));
                obs.add(mModel.updateAllGoodItemsFromRemote().map(result -> true));

                Observable.concat(obs)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                if (getRootView() != null) {
                                    ((RootActivity)getRootView()).runOnUiThread(() -> getRootView().showLoad());
                                }
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {  }

                            @Override
                            public void onError(Throwable e) {
                                if (getRootView() != null) {
                                    ((RootActivity)getRootView()).runOnUiThread(() -> getRootView().hideLoad());
                                    getRootView().showError(e);
                                }
                            }

                            @Override
                            public void onComplete() {
                                if (getRootView() != null) {
                                    ((RootActivity)getRootView()).runOnUiThread(() -> getRootView().hideLoad());
                                    getRootView().showMessage(App.getStringRes(R.string.message_sync_complete));
                                }
                            }
                        });
                return true;
            };
        }


        void mainGroupSelected(GoodsGroupRealm selectedGroup) {

            if (getRootView() != null) {
                currentGroup = selectedGroup;
                updateGoodsList();
            }

        }

        public boolean goGroupBack(){
            if (currentGroup == null) {
                return false;
            } else {
                currentGroup = currentGroup.getParent();
            }
            updateGoodsList();
            return true;
        }

        void itemSelected(ItemRealm selectedItem) {
            if (currentCart == null){
                //TODO: make screen with item card
            }else{
                if (getRootView() != null) {
                    Float itemBasePrice = selectedItem.getBasePrice();
                    Float itemDiscount = getCustomerDiscount(selectedItem);
                    Float itemPrice = getCustomerPrice(selectedItem);

                    LayoutInflater layoutInflater = ((Activity)getRootView()).getLayoutInflater();
                    View view = layoutInflater.inflate(R.layout.add_item_to_cart, null);
                    EditText inputPrice = view.findViewById(R.id.item_price);
                    inputPrice.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format),itemPrice));
                    EditText inputQty  = view.findViewById(R.id.item_quantity);
                    inputQty.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int),1f));
                    TextView textDiscount  = view.findViewById(R.id.item_discount);
                    textDiscount.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format),itemDiscount));
                    TextView textBasePrice  = view.findViewById(R.id.item_base_price);
                    textBasePrice.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format),itemBasePrice));


                    AlertDialog.Builder alert = new AlertDialog.Builder(getView().getContext())
                            .setTitle(selectedItem.getName())
                            .setView(view);

                    alert.setPositiveButton(App.getStringRes(R.string.button_positive_text), (dialog, whichButton) -> {
                        float newPrice = Float.parseFloat(inputPrice.getText().toString().replace(",","."));
                        float newQty = Float.parseFloat(inputQty.getText().toString());
                        //check price
                        if (newPrice < selectedItem.getLowPrice()) {
                            if (getRootView() != null) {
                                getRootView().showMessage(App.getStringRes(R.string.error_low_price) + " (" + String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), selectedItem.getLowPrice()) + ")");
                            }
                        }

                        if (newQty > 0){
                            mModel.addItemToCart(currentCart, selectedItem, newQty, newPrice);

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

        Float getCustomerDiscount(ItemRealm item) {
            return  mModel.getCustomerDiscount(mCustomer, item);
        }

        Float getCustomerPrice(ItemRealm item) {
            Float discount = getCustomerDiscount(item);
            return  (float)(Math.round(item.getBasePrice() * (100 - discount))) / 100;
        }
    }

    //endregion ================== Presenter =========================

}
