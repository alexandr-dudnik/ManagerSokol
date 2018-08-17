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
import com.sokolua.manager.utils.App;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.Provides;
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
                    .addAction(new MenuItemHolder(App.getStringRes(R.string.menu_syncronize), R.drawable.ic_sync, syncClickCallback(), ConstantManager.MENU_ITEM_TYPE_ACTION))
                    .addAction(new MenuItemHolder(App.getStringRes(R.string.menu_logout), R.drawable.ic_logout, logoutClickCallback(), ConstantManager.MENU_ITEM_TYPE_ITEM))
                    .setTitle(App.getStringRes(R.string.menu_settings))
                    .build();

        }

        @NonNull
        private MenuItem.OnMenuItemClickListener syncClickCallback() {
            return item -> {
                Observable obs = Observable.just(true)
                        .delay(3, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        ;
                obs.subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (getRootView() != null) {
                            ((RootActivity)getRootView()).runOnUiThread(() -> getRootView().showLoad());
                        }
                    }

                    @Override
                    public void onNext(Object o) {

                    }

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
            if (getView() != null && getRootView() != null) {
                if (!mAuthModel.isUserNameValid((getView().getUserName()))) {
                    getView().showInvalidUserName();
                    getRootView().showMessage(App.getStringRes(R.string.error_empty_login));
                    return;
                }
                if (!mAuthModel.isPasswordValid(getView().getUserPassword())) {
                    getView().showInvalidPassword();
                    getRootView().showMessage(App.getStringRes(R.string.error_bad_password));
                    return;
                }

                //TODO auth user
                mAuthModel.loginUser(getView().getUserName(),
                        getView().getUserPassword());

                if (mAuthModel.isUserAuth()) {
                    if (getRootView() != null) {
                        getRootView().showMessage(App.getStringRes(R.string.message_auth_success));
                    }
                }else {
                    getView().login_error();
                    getRootView().showMessage(App.getStringRes(R.string.error_auth_error));
                }

            }
        }
    }

    //endregion ================== Presenter =========================

}
