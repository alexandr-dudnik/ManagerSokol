package com.sokolua.manager.ui.screens.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.AuthModel;
import com.sokolua.manager.mvp.models.SettingsModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.mvp.presenters.MenuItemHolder;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.screens.auth.AuthScreen;
import com.sokolua.manager.utils.App;

import javax.inject.Inject;

import dagger.Provides;
import flow.Direction;
import flow.Flow;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mortar.MortarScope;

@Screen(R.layout.screen_settings)
public class SettingsScreen extends AbstractScreen<RootActivity.RootComponent>{

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerSettingsScreen_Component.builder()
                .module(new Module())
                .rootComponent(parentComponent)
                .build();
    }


    //region ===================== DI =========================

    @dagger.Module
    class Module {

        @Provides
        @DaggerScope(SettingsScreen.class)
        SettingsModel provideSettingsModel() {
            return new SettingsModel();
        }

        @Provides
        @DaggerScope(SettingsScreen.class)
        Presenter providePresenter() {
            return new Presenter();
        }

        @Provides
        @DaggerScope(SettingsScreen.class)
        AuthModel provideAuthModel() {
            return new AuthModel();
        }
    }


    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(SettingsScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(SettingsView view);

    }
    //endregion ================== DI =========================


    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<SettingsView, SettingsModel> {

        @Inject
        AuthModel mAuthModel;

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

            getView().setServerAddress(mModel.getServerAddress());
            getView().setAutoSynchronize(mModel.getAutoSynchronize());
            getView().setUserName(mAuthModel.getUserName());
            getView().setUserPassword(mAuthModel.getUserPassword());
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .setBackArrow(true)
                    .addAction(new MenuItemHolder(App.getStringRes(R.string.menu_synchronize), R.drawable.ic_sync, syncClickCallback(), ConstantManager.MENU_ITEM_TYPE_ACTION))
                    .addAction(new MenuItemHolder(App.getStringRes(R.string.menu_logout), R.drawable.ic_logout, logoutClickCallback(), ConstantManager.MENU_ITEM_TYPE_ITEM))
                    .setTitle(App.getStringRes(R.string.menu_settings))
                    .build();

        }

        @NonNull
        private MenuItem.OnMenuItemClickListener syncClickCallback() {
            return item -> {
                Observable.mergeDelayError(
                        mModel.updateAllGroupsFromRemote()
//                                .flatMap(group -> mModel.updateGoodGroupFromRemote(group.getGroupId()))
                                .map(group -> group.isLoaded())
                        ,
                        mModel.updateAllGoodItemsFromRemote()
  //                              .flatMap(good_item -> mModel.updateGoodItemFromRemote(good_item.getItemId()))
                                .map(good_item -> good_item.isLoaded())
                    )
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

        @NonNull
        private MenuItem.OnMenuItemClickListener logoutClickCallback() {
            return item -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(getView().getContext())
                        .setMessage(App.getStringRes(R.string.question_logout))
                        .setCancelable(false)
                        .setPositiveButton(R.string.button_positive_text, ((dialog, which) -> {
                            if (getRootView() != null) {
                                mAuthModel.ClearUserData();
                                Flow.get(getView()).replaceHistory(new AuthScreen(), Direction.REPLACE);
                                getRootView().showMessage(App.getStringRes(R.string.message_logout));

                            }
                        }))
                        .setNegativeButton(R.string.button_negative_text, ((dialog, which) -> {
                        }));
                alert.show();
                return true;
            };
        }


        public void updateServerAddress(String address) {
            mModel.updateServerAddress(address);
        }

        public void updateAutoSynchronize(boolean checked) {
            mModel.updateAutoSynchronize(checked);
        }


        public void checkAuth() {
            mRootPresenter.doUserLogin(getView().getUserName(),getView().getUserPassword());
        }
    }

    //endregion ================== Presenter =========================

}
