package com.sokolua.manager.mvp.presenters;


import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.sokolua.manager.R;
import com.sokolua.manager.data.network.res.UserRes;
import com.sokolua.manager.mvp.models.AuthModel;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.mvp.views.IAuthView;
import com.sokolua.manager.mvp.views.IRootView;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.activities.SplashActivity;
import com.sokolua.manager.ui.screens.main.MainScreen;
import com.sokolua.manager.utils.App;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import flow.Direction;
import flow.Flow;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mortar.Presenter;
import mortar.bundler.BundleService;


public class RootPresenter extends Presenter<IRootView> {

    private static final String BOTTOM_BAR_VISIBILITY_KEY = "bottom_bar_visibility";
    @Inject
    AuthModel mAuthModel;

    private static int DEFAULT_MODE = 0;
    private static int TAB_MODE = 1;

    public RootPresenter() {
        App.getRootActivityRootComponent().inject(this);
    }

    @Override
    protected BundleService extractBundleService(IRootView view) {
        return (view instanceof RootActivity) ?
                BundleService.getBundleService((RootActivity) view) : //Привязываем RootPresenter к RootActivity
                BundleService.getBundleService((SplashActivity) view);
    }


    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);

        if (savedInstanceState != null && getView() != null) {
            getView().setBottomBarVisibility(savedInstanceState.getBoolean(BOTTOM_BAR_VISIBILITY_KEY, true));
        }
    }

    @Override
    protected void onSave(Bundle outState) {
        if (getView() != null) {
            outState.putBoolean(BOTTOM_BAR_VISIBILITY_KEY, getView().getBottomBarVisibility());
        }

        super.onSave(outState);
    }

    @Override
    public void dropView(IRootView view) {
        super.dropView(view);
    }


    @Nullable
    public IRootView getRootView() {
        return getView();
    }


    public void doUserLogin(String userName, String password) {
        AbstractView scr = (AbstractView) getView().getCurrentScreen();
        if (!mAuthModel.isUserNameValid(userName)) {
            if (scr instanceof IAuthView) {
                ((IAuthView) scr).showInvalidUserName();
            }
            getView().showMessage(App.getStringRes(R.string.error_empty_login));
            return;
        }
        if (!mAuthModel.isPasswordValid(password)) {
            if (scr instanceof IAuthView) {
                ((IAuthView) scr).showInvalidPassword();
            }
            getView().showMessage(App.getStringRes(R.string.error_bad_password));
            return;
        }

        Observable<UserRes> obs = mAuthModel.loginUser(userName, password);
        obs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .take(1)
                .subscribe(
                        new Observer<UserRes>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                ((RootActivity) getView()).runOnUiThread(() -> getView().showLoad());
                            }

                            @Override
                            public void onNext(UserRes userRes) {
                                mAuthModel.updateUserData(userRes);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getView().showError(e);
                                ((RootActivity) getView()).runOnUiThread(() -> getView().hideLoad());
                                if (scr instanceof IAuthView) {
                                    ((IAuthView) scr).login_error();
                                }
                            }

                            @Override
                            public void onComplete() {
                                ((RootActivity) getView()).runOnUiThread(() -> getView().hideLoad());
                                if (mAuthModel.isUserAuth()) {
                                    getView().showMessage(App.getStringRes(R.string.message_auth_success));
                                    mAuthModel.updateAutoSynchronize(true);
                                    if (scr != null) {
                                        Flow.get(scr).replaceHistory(new MainScreen(), Direction.REPLACE);
                                    }
                                }
                            }
                        }
                );
    }


    public ActionBarBuilder newActionBarBuilder() {
        return this.new ActionBarBuilder();
    }


    public boolean checkPermissionsAndRequestIfNotGranted(@NonNull String[] permissions, int requestCode){
        boolean allGranted = true;
        for (String permission : permissions) {
            int selfPermission = ContextCompat.checkSelfPermission(((RootActivity) getView()),permission);
            if (selfPermission != PackageManager.PERMISSION_GRANTED){
                allGranted = false;
                break;
            }
        }

        if (!allGranted){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((RootActivity) getView()).requestPermissions(permissions, requestCode);
            }
        }

        return allGranted;
    }


    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult){

    }

    public class ActionBarBuilder{
        private boolean isGoBack = false;
        private boolean isVisible = false;
        private CharSequence title;
        private List<MenuItemHolder> items = new ArrayList<>();
        private ViewPager pager;
        private int toolbarMode = DEFAULT_MODE;

        public ActionBarBuilder setBackArrow(boolean enable) {
            this.isGoBack = enable;
            return this;
        }

        public ActionBarBuilder setVisible(boolean visible) {
            this.isVisible = visible;
            return this;
        }

        public ActionBarBuilder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public ActionBarBuilder addAction(MenuItemHolder menuItem) {
            this.items.add(menuItem);
            return this;
        }


        public ActionBarBuilder setTabs(ViewPager viewPager) {
            this.toolbarMode = TAB_MODE;
            this.pager = viewPager;
            return this;
        }

        public void build(){
            if (getView() != null) {
                RootActivity activity = (RootActivity) getView();
                activity.setVisible(isVisible);
                activity.setActionBarTitle(title);
                activity.setBackArrow(isGoBack);
                activity.setMenuItem(items);
                if (toolbarMode == TAB_MODE){
                    activity.setTabLayout(pager);
                }else{
                    activity.removeTabLayout();
                }
            }
        }
    }

}
