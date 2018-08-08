package com.sokolua.manager.ui.screens.goods;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.GoodsModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.mvp.presenters.MenuItemHolder;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import dagger.Provides;
import io.reactivex.Observable;
import mortar.MortarScope;

@Screen(R.layout.screen_goods)
public class GoodsScreen extends AbstractScreen<RootActivity.RootComponent>{

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

    }


    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(GoodsScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(GoodsView view);

        void inject(MainGroupViewHolder viewHolder);
    }
    //endregion ================== DI =========================


    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<GoodsView, GoodsModel> {

        GoodsGroupRealm currentGroup = null;

        ReactiveRecyclerAdapter.ReactiveViewHolderFactory<GoodsGroupRealm> groupaViewHolder;
        ReactiveRecyclerAdapter groupsAdapter;

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



            groupaViewHolder = (parent, pViewType) -> {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_group_item, parent, false);
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new MainGroupViewHolder(view)
                );
            };
            groupsAdapter = new ReactiveRecyclerAdapter(Observable.empty(), groupaViewHolder);

            getView().setAdapter(groupsAdapter);

            setOrderListFilter("");
        }

        public void setOrderListFilter(String filter){
            if (currentGroup == null || currentGroup.getParent() == null){
                groupsAdapter.refreshList(mModel.getGroupList(currentGroup));
            }else{
                groupsAdapter.refreshList(Observable.empty());
            }
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .setBackArrow(false)
                    .addAction(new MenuItemHolder(App.getStringRes(R.string.menu_search), R.drawable.ic_search, new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            setOrderListFilter(query);
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            setOrderListFilter(newText);
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
                setOrderListFilter("");
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
                    ((RootActivity) getRootView()).setBackArrow(false);
                    ((RootActivity) getRootView()).setActionBarTitle(App.getStringRes(R.string.menu_goods));
                }
                else{
                    ((RootActivity)getRootView()).setActionBarTitle(currentGroup.getName());
                }
                setOrderListFilter("");
            }
            return true;
        }
    }

    //endregion ================== Presenter =========================

}
