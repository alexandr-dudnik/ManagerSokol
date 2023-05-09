package com.sokolua.manager.ui.screens.goods;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.BrandsRealm;
import com.sokolua.manager.data.storage.realm.CurrencyRealm;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.data.storage.realm.PriceListRealm;
import com.sokolua.manager.data.storage.realm.TradeRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.GoodsModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.mvp.presenters.MenuItemHolder;
import com.sokolua.manager.mvp.presenters.RootPresenter;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.ui.screens.order.OrderScreen;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.MiscUtils;

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
        ReactiveRecyclerAdapter<GoodsGroupRealm> groupsAdapter;
        ReactiveRecyclerAdapter<ItemRealm> itemsAdapter;
        private RealmObjectChangeListener<OrderRealm> orderChangeListener;
        private RealmChangeListener<RealmResults<OrderLineRealm>> orderLinesChangeListener;


        private String currentFilter = "";
        private String currentBrand = "";
        private String currentPrice = "";
        private String currentTrade = "";
        private String currentCurrency = "";

        private MenuItemHolder brandsSubMenu;
        private MenuItemHolder pricesSubMenu;
        private MenuItemHolder tradesSubMenu;
        private MenuItemHolder currencySubMenu;
        private String priceId;
        private String tradeId;
        private String currencyId;


        public Presenter() {
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {

            groupViewHolder = (parent, pViewType) -> {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_groups_item, parent, false);
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new GroupViewHolder(view)
                );
            };
            groupsAdapter = new ReactiveRecyclerAdapter<>(Observable.empty(), groupViewHolder, false);

            getView().setGroupsAdapter(groupsAdapter);

            itemViewHolder = (parent, pViewType) -> {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_items_item, parent, false);
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new ItemViewHolder(view)
                );
            };
            itemsAdapter = new ReactiveRecyclerAdapter<>(Observable.empty(), itemViewHolder, false);

            getView().setItemsAdapter(itemsAdapter);

            if (savedInstanceState != null){
                mCustomerOrderId = savedInstanceState.getString(ConstantManager.STATE_GOODS_ORDER_KEY,"");
                mFilterCategoryId = savedInstanceState.getString(ConstantManager.STATE_GOODS_CATEGORY_KEY,"");
                currentPrice = savedInstanceState.getString(ConstantManager.STATE_GOODS_PRICE_KEY,"");
                currentTrade = savedInstanceState.getString(ConstantManager.STATE_GOODS_TRADE_KEY,"");
                currentCurrency = savedInstanceState.getString(ConstantManager.STATE_GOODS_CURRENCY_KEY,"");
            }

            if (mCustomerOrderId!=null && !mCustomerOrderId.isEmpty()) {
                currentCart = mModel.getOrderById(mCustomerOrderId);
                mCustomer = currentCart.getCustomer();
                currentPrice = mCustomer.getPrice()==null?"":mCustomer.getPrice().getName();
                TradeRealm mTrade = currentCart.getTrade()==null?currentCart.getPayment() == ConstantManager.ORDER_PAYMENT_CASH ? mCustomer.getTradeCash() : mCustomer.getTradeOfficial() : currentCart.getTrade();
                currentTrade = mTrade == null ? "" : mTrade.getName();
                currentCurrency = currentCart.getCurrency().getName();
            }


            checkTrade(currentTrade);
            checkPrice(currentPrice);
            checkCurrency(currentCurrency);

            if (currentCurrency==null || currentCurrency.isEmpty()) currentCurrency = ConstantManager.MAIN_CURRENCY;

            super.onLoad(savedInstanceState);


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
                        getView().setCartCurrency(currentCart.getCurrency().getName());
                    }
                };
                currentCart.addChangeListener(orderChangeListener);
                orderLinesChangeListener = orderLineRealms -> {
                            if (!orderLineRealms.isLoaded() || !orderLineRealms.isValid()){
                                orderLineRealms.removeAllChangeListeners();
                            }else {
                                if (currentCart != null && getView() != null){
                                    getView().setCartAmount(currentCart.getTotal());
                                    getView().setCartItemsCount(currentCart.getLines().size());
                                }
                            }
                        };
                currentCart.getLines().addChangeListener(orderLinesChangeListener);

            }else{
                getView().setCatalogMode();
            }



        }

        @Override
        protected void onSave(Bundle outState) {
            outState.putString(ConstantManager.STATE_GOODS_ORDER_KEY, mCustomerOrderId);
            outState.putString(ConstantManager.STATE_GOODS_CATEGORY_KEY, mFilterCategoryId);
            outState.putString(ConstantManager.STATE_GOODS_PRICE_KEY, currentPrice);
            outState.putString(ConstantManager.STATE_GOODS_TRADE_KEY, currentTrade);
            outState.putString(ConstantManager.STATE_GOODS_CURRENCY_KEY, currentCurrency);

            super.onSave(outState);
        }

        @Override
        public void dropView(GoodsView view) {
            if (currentCart != null){
                currentCart.removeChangeListener(orderChangeListener);
                if (currentCart.getLines() != null) {
                    currentCart.getLines().removeChangeListener(orderLinesChangeListener);
                }
                currentCart = null;
            }
            super.dropView(view);
        }

        private void checkTrade(String tradeName){
            final TradeRealm mTrade = mModel.getTradeByName(tradeName);
            if (mTrade == null){
                currentTrade = "";
                tradeId = "";
            }else{
                currentTrade = mTrade.getName();
                tradeId = mTrade.getTradeId();
            }
            updateGoodsList();
        }

        private void checkPrice(String priceName){
            PriceListRealm mPrice = mModel.getPriceByName(priceName);
            if (mPrice == null) mPrice = mModel.getPriceById(ConstantManager.PRICE_BASE_PRICE_ID);
            if (mPrice == null){
                currentPrice = "";
                priceId = "";
            }else{
                currentPrice = mPrice.getName();
                priceId = mPrice.getPriceId();
            }
            updateGoodsList();
        }

        private void checkCurrency(String currencyName){
            final CurrencyRealm mCurrency = mModel.getCurrencyByName(currencyName);
            if (mCurrency == null) mModel.getCurrencyByName(ConstantManager.MAIN_CURRENCY);
            if (mCurrency == null){
                currentCurrency = "";
                currencyId = "";
            }else{
                currentCurrency = mCurrency.getName();
                currencyId = mCurrency.getCurrencyId();
            }
            updateGoodsList();
        }


        void updateCartFields() {

            getView().setCustomer(currentCart.getCustomer().getName());
            getView().setCartCurrency(currentCart.getCurrency().getName());
            getView().setCartAmount(currentCart.getTotal());
            getView().setCartItemsCount(currentCart.getLines().size());

        }

        void updateGoodsList(){
            if (getRootView() != null) {
                ((RootActivity) getRootView()).setBackArrow(currentGroup!=null||currentCart!=null);
                ((RootActivity) getRootView()).setActionBarTitle((currentGroup == null?App.getStringRes(R.string.menu_goods):currentGroup.getName())+(currentBrand.isEmpty()?"":" ("+currentBrand+")"));

            }

            if (getView() != null) {
                String currentGroupId = currentGroup == null?"":currentGroup.getGroupId();
                if ((currentGroup == null || currentGroup.getParent() == null) && (currentFilter == null || currentFilter.isEmpty()) && (mFilterCategoryId == null || mFilterCategoryId.isEmpty())){
                    groupsAdapter.refreshList(mModel.getGroupList(currentGroupId, currentBrand));
                    getView().showGroups();
                }else{
                    itemsAdapter.refreshList(mModel.getItemList(currentGroupId, currentFilter, currentBrand, mFilterCategoryId));
                    getView().showItems();
                }
            }
        }

        private void subMenuItemAction(MenuItem item, String parentTitle){
            if (getRootView() != null) {
                MenuItem groupItem = ((RootActivity)getRootView()).getMainMenuItemParent(item.getItemId());
                if (groupItem != null) {
                    groupItem.setTitle(String.format("%s: %s",parentTitle, item.getTitle()));
                }
            }
            item.setChecked(true);
            updateGoodsList();
        }

        private void rebuildActionBar(){
            if (getView() != null) {
                RootPresenter.ActionBarBuilder mActionBarBuilder = mRootPresenter.newActionBarBuilder()
                        .setVisible(true)
                        .setBackArrow(currentCart != null)
                        .addAction(new MenuItemHolder(App.getStringRes(R.string.menu_synchronize), R.drawable.ic_sync, syncClickCallback(), ConstantManager.MENU_ITEM_TYPE_ITEM))
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
                        .setTitle(App.getStringRes(R.string.menu_goods));

                mActionBarBuilder.addAction(brandsSubMenu);
                if (pricesSubMenu != null) mActionBarBuilder.addAction(pricesSubMenu);
                if (tradesSubMenu != null) mActionBarBuilder.addAction(tradesSubMenu);
                if (currencySubMenu != null) mActionBarBuilder.addAction(currencySubMenu);

                mActionBarBuilder.build();
            }
        }


        @Override
        protected void initActionBar() {

            MenuItem.OnMenuItemClickListener brandListener = item -> {
                currentBrand = item.getTitle().toString().equals(App.getStringRes(R.string.all_brands)) ? "" : item.getTitle().toString();
                subMenuItemAction(item, App.getStringRes(R.string.menu_brands));
                return true;
            };

            brandsSubMenu = new MenuItemHolder(String.format("%s: %s",App.getStringRes(R.string.menu_brands), (currentBrand.isEmpty()?App.getStringRes(R.string.all_brands):currentBrand)), R.drawable.ic_brand, null, ConstantManager.MENU_ITEM_TYPE_ITEM);
            brandsSubMenu.addSubMenuItem(new MenuItemHolder(App.getStringRes(R.string.all_brands), brandListener, 1, (currentBrand.isEmpty())));
            mModel.getBrands()
                    .take(1)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(brands -> {
                            brandsSubMenu.clearSubMenu();
                            brandsSubMenu.addSubMenuItem(new MenuItemHolder(App.getStringRes(R.string.all_brands), brandListener, 1, (currentBrand.isEmpty())));
                            for (BrandsRealm brand : brands) {
                                brandsSubMenu.addSubMenuItem(new MenuItemHolder(brand.getName(), brandListener, 1, (currentBrand.equals(brand.getName()))));
                            }
                            rebuildActionBar();
                        })
                    .doOnError(throwable -> Log.e("ERROR","Menu brands", throwable) )
                    .subscribe()
            ;

            if (currentCart == null) {
                MenuItem.OnMenuItemClickListener priceListener = item -> {
                    checkPrice(item.getTitle().toString());
                    subMenuItemAction(item, App.getStringRes(R.string.menu_prices));
                    return true;
                };
                MenuItem.OnMenuItemClickListener tradeListener = item -> {
                    checkTrade(item.getTitle().toString().equals(App.getStringRes(R.string.no_trades)) ? "" : item.getTitle().toString());
                    subMenuItemAction(item, App.getStringRes(R.string.menu_trades));
                    return true;
                };
                MenuItem.OnMenuItemClickListener currencyListener = item -> {
                    checkCurrency(item.getTitle().toString());
                    subMenuItemAction(item, App.getStringRes(R.string.menu_currency));
                    return true;
                };

                pricesSubMenu = new MenuItemHolder(String.format("%s: %s",App.getStringRes(R.string.menu_prices), currentPrice), R.drawable.ic_price, null, ConstantManager.MENU_ITEM_TYPE_ITEM);

                mModel.getPrices()
                        //.take(1)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(prices -> {
                            pricesSubMenu.clearSubMenu();
                            for (PriceListRealm price : prices) {
                                pricesSubMenu.addSubMenuItem(new MenuItemHolder(price.getName(), priceListener, 1, (currentPrice.equals(price.getName()))));
                            }
                            rebuildActionBar();
                        })
                        .doOnError(throwable -> Log.e("ERROR","Menu prices", throwable) )
                        .subscribe()
                ;

                tradesSubMenu = new MenuItemHolder(String.format("%s: %s",App.getStringRes(R.string.menu_trades), (currentTrade.isEmpty()?App.getStringRes(R.string.no_trades):currentTrade)), R.drawable.ic_trade, null, ConstantManager.MENU_ITEM_TYPE_ITEM);
                tradesSubMenu.addSubMenuItem(new MenuItemHolder(App.getStringRes(R.string.no_trades), tradeListener, 1, (currentTrade.isEmpty())));

                mModel.getTrades(null, null, null, null)
                        //.take(1)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(trades -> {
                            tradesSubMenu.clearSubMenu();
                            tradesSubMenu.addSubMenuItem(new MenuItemHolder(App.getStringRes(R.string.no_trades), tradeListener, 1, (currentTrade.isEmpty())));
                            for (TradeRealm trade : trades) {
                                tradesSubMenu.addSubMenuItem(new MenuItemHolder(trade.getName(), tradeListener, 1, (currentPrice.equals(trade.getName()))));
                            }
                            rebuildActionBar();
                        })
                        .doOnError(throwable -> Log.e("ERROR","Menu trades", throwable) )
                        .subscribe()
                ;

                currencySubMenu = new MenuItemHolder(String.format("%s: %s",App.getStringRes(R.string.menu_currency), currentCurrency), R.drawable.ic_price, null, ConstantManager.MENU_ITEM_TYPE_ITEM);

                mModel.getCurrencies()
                        //.take(1)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(currencies -> {
                            currencySubMenu.clearSubMenu();
                            for (CurrencyRealm currency : currencies) {
                                currencySubMenu.addSubMenuItem(new MenuItemHolder(currency.getName(), currencyListener, 1, (currentCurrency.equals(currency.getName()))));
                            }
                            rebuildActionBar();
                        })
                        .doOnError(throwable -> Log.e("ERROR","Menu currencies", throwable) )
                        .subscribe()
                ;
            }else{
                pricesSubMenu = null;
                tradesSubMenu = null;
                currencySubMenu = null;
            }

            rebuildActionBar();

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

        void itemSelected(String selectedItemId) {
            if (currentCart == null){
                //TODO: make screen with item card
            }else{
                if (getView() != null && getRootView() != null) {
                    ItemRealm selectedItem = mModel.getItemById(selectedItemId);
                    TradeRealm trade = mModel.getTradeById(tradeId);
                    PriceListRealm price = mModel.getPriceById(priceId);
                    PriceListRealm basePrice = mModel.getPriceById(ConstantManager.PRICE_BASE_PRICE_ID);


                    Float itemBasePrice = mModel.getItemPrice(selectedItemId, priceId, null, currencyId, null, trade != null && trade.isLTD());
                    Float itemDiscount = getCustomerDiscount(selectedItemId, tradeId);
                    Float itemPrice = getItemPrice(selectedItemId);

                    LayoutInflater layoutInflater = ((Activity)getRootView()).getLayoutInflater();
                    View view = layoutInflater.inflate(R.layout.add_item_to_cart, null);
                    EditText inputPrice = view.findViewById(R.id.item_price);
                    inputPrice.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format),itemPrice));
                    inputPrice.setEnabled(itemPrice < 0.01f);
                    EditText inputQty  = view.findViewById(R.id.item_quantity);
                    inputQty.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int),1f));
                    TextView textDiscount  = view.findViewById(R.id.item_discount);
                    textDiscount.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format),itemDiscount));
                    TextView textBasePrice  = view.findViewById(R.id.item_base_price);
                    textBasePrice.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format),itemBasePrice));
                    TextView textPrice  = view.findViewById(R.id.price_name_label);
                    textPrice.setText(price==null?(basePrice==null?"":basePrice.getName()):price.getName());
                    TextView textTrade  = view.findViewById(R.id.trade_name_label);
                    textTrade.setText(trade==null?"":trade.getName());


                    AlertDialog.Builder alert = new AlertDialog.Builder(getView().getContext())
                            .setTitle(selectedItem.getName())
                            .setView(view);

                    alert.setPositiveButton(App.getStringRes(R.string.button_positive_text), (dialog, whichButton) -> {
                        float newPrice = 0;
                        try{newPrice = Float.parseFloat(inputPrice.getText().toString().replace(",","."));}catch (Throwable ignore){}
                        if (trade!=null && !trade.isCash()){
                            newPrice = MiscUtils.roundPrice(newPrice);
                        }
                        float newQty = 0 ;
                        try{newQty = Float.parseFloat(inputQty.getText().toString());}catch (Throwable ignore){}
                        //check price
                        final float itemLowPrice = mModel.getItemLowPrice(selectedItemId, currencyId);
                        if (newPrice < itemLowPrice) {
                            if (getRootView() != null) {
                                getRootView().showMessage(App.getStringRes(R.string.error_low_price) + " (" + String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), itemLowPrice) + ")");
                            }
                        }

                        if (newQty > 0){
                            mModel.addItemToCart(mCustomerOrderId, selectedItemId, newQty, newPrice);

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
                Flow.get(getView()).set(new OrderScreen(mCustomerOrderId));
            }
        }

        Float getCustomerDiscount(String itemId, String tradeId) {
            return  mModel.getCustomerDiscount(mCustomer.getCustomerId(), itemId) - mModel.getTradePercent(itemId, tradeId);
        }

        Float getItemPrice(String itemId){
            TradeRealm trade = mModel.getTradeById(tradeId);
            return mModel.getItemPrice(itemId, priceId, tradeId, currencyId, mCustomer==null?null:mCustomer.getCustomerId(), trade != null && trade.isLTD());
        }

        Float getLowPrice(String itemId){
            return mModel.getItemLowPrice(itemId, currencyId);
        }
    }

    //endregion ================== Presenter =========================

}
