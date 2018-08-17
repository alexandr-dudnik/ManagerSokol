package com.sokolua.manager.mvp.presenters;


import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.sokolua.manager.mvp.models.AuthModel;
import com.sokolua.manager.mvp.views.IRootView;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.activities.SplashActivity;
import com.sokolua.manager.utils.App;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import mortar.Presenter;
import mortar.bundler.BundleService;


public class RootPresenter extends Presenter<IRootView> {

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


    }

    @Override
    public void dropView(IRootView view) {
        super.dropView(view);
    }

//    private Subscription subscribeOnUserInfoObs(){
//        return mAccountModel.getUserInfoObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new UserInfoSubscriber());
//    }

    @Nullable
    public IRootView getRootView() {
        return getView();
    }

    public ActionBarBuilder newActionBarBuilder() {
        return this.new ActionBarBuilder();
    }

//    @RxLogSubscriber
//    private class UserInfoSubscriber extends Subscriber<UserInfoDto>{
//
//        @Override
//        public void onCompleted() {
//
//        }
//
//        @Override
//        public void onError(Throwable e) {
//            if (getView() != null) {
//                getView().showError(e);
//            }
//        }
//
//        @Override
//        public void onNext(UserInfoDto userInfoDto) {
//            if (getView() != null) {
//                getView().initDrawer(userInfoDto);
//            }
//        }
//    }

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


    public void onRequstPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult){
        // TODO: 19.02.2017 implement request result
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
