package com.sokolua.manager.ui.screens.settings;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.managers.DebugManager;
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
import com.sokolua.manager.utils.AppConfig;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.Provides;
import flow.Direction;
import flow.Flow;
import io.reactivex.Observable;
import io.reactivex.Observer;
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

            getView().setAutoSynchronize(mModel.getAutoSynchronize());
            getView().setUserName(mAuthModel.getUserName());
            getView().setUserPassword(mAuthModel.getUserPassword());
            getView().setServerList(AppConfig.API_SERVERS, mModel.getServerAddress());
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

                if (mAuthModel.getUserName().equals(AppConfig.TEST_USERNAME)){
                    try {
                        mModel.clearDatabase();
                        DebugManager.mock_RealmDB();
                        if (getRootView() != null) {
                            getRootView().showMessage(App.getStringRes(R.string.message_sync_complete));
                        }
                    }catch (Exception e){
                        if (getRootView() != null) {
                            getRootView().showError(e);
                        }
                    }
                    return true;
                }

                final Boolean currentAuto = mModel.getAutoSynchronize();

                ArrayList<Observable<Integer>> obs = new ArrayList<>();
                obs.add(Observable.just("Send orders...").doOnNext(it->mModel.sendAllOrders()).map(it->0));
                obs.add(Observable.just("Send notes...").doOnNext(it->mModel.sendAllNotes()).map(it->1));
                obs.add(Observable.just("Send tasks...").doOnNext(it->mModel.sendAllTasks()).map(it->2));
                obs.add(mModel.updateCurrencyFromRemote().map(it->3));
                obs.add(mModel.updateTradesFromRemote().map(it->4));
                obs.add(mModel.updateAllGroupsFromRemote().map(it->5));
                obs.add(mModel.updateAllGoodItemsFromRemote().map(it->6));
                obs.add(mModel.updateAllCustomersFromRemote().map(it->7));
                obs.add(mModel.updateAllOrdersFromRemote().map(it->8));

                Observable.concat(obs)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.single())
                    .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mModel.updateAutoSynchronize(false);
                        if (getRootView() != null) {
                            ((RootActivity)getRootView()).runOnUiThread(() -> getRootView().showLoad(obs.size()));
                        }
                    }

                    @Override
                    public void onNext(Integer param) {
                        if (getRootView() != null) {
                            ((RootActivity)getRootView()).runOnUiThread(() -> getRootView().updateProgress(param));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getRootView() != null) {
                            ((RootActivity)getRootView()).runOnUiThread(() -> getRootView().hideLoad());
                            getRootView().showError(e);
                        }
                        mModel.updateAutoSynchronize(currentAuto);
                    }

                    @Override
                    public void onComplete() {
                        if (getRootView() != null) {
                            ((RootActivity)getRootView()).runOnUiThread(() -> getRootView().updateProgress(obs.size()));
                            ((RootActivity)getRootView()).runOnUiThread(() -> getRootView().hideLoad());
                            Objects.requireNonNull(getRootView()).showMessage(App.getStringRes(R.string.message_sync_complete));
                        }
                        mModel.updateAutoSynchronize(currentAuto);
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
                            Observable.interval(1000, TimeUnit.MILLISECONDS)
                                .filter(aBool-> mModel.checkAllJobsFinished())
                                .take(1)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(Schedulers.single())
                                .subscribe(new Observer<Object>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        if (getRootView() != null) {
                                            ((RootActivity) getRootView()).runOnUiThread(() -> getRootView().showLoad());
                                        }
                                        mModel.updateAutoSynchronize(false);
                                        mModel.cancelAllJobs();
                                    }

                                    @Override
                                    public void onNext(Object param) {
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        if (getRootView() != null) {
                                            ((RootActivity) getRootView()).runOnUiThread(() -> getRootView().hideLoad());
                                            getRootView().showError(e);
                                        }
                                    }

                                    @Override
                                    public void onComplete() {
                                        if (getRootView() != null) {
                                            ((RootActivity) getRootView()).runOnUiThread(() -> {
                                                mAuthModel.clearUserData();
                                                getRootView().hideLoad();
                                                Flow.get(getView()).replaceHistory(new AuthScreen(), Direction.REPLACE);
                                                getRootView().showMessage(App.getStringRes(R.string.message_logout));
                                            });
                                        }
                                    }
                                });

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

        public void updateServer(String serverName) {
            mModel.updateServerAddress(serverName);
        }
    }

    //endregion ================== Presenter =========================

}
