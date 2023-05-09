package com.sokolua.manager.ui.screens.check_in;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.VisitRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.CheckInModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.mvp.presenters.MenuItemHolder;
import com.sokolua.manager.mvp.presenters.RootPresenter;
import com.sokolua.manager.mvp.views.IRootView;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.screens.routes.RoutesScreen;
import com.sokolua.manager.utils.App;

import dagger.Provides;
import flow.TreeKey;
import io.realm.RealmObjectChangeListener;
import mortar.MortarScope;

@Screen(R.layout.screen_check_in)
public class CheckInScreen extends AbstractScreen<RootActivity.RootComponent> implements TreeKey {
    private String mVisitId;


    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerCheckInScreen_Component.builder()
                .module(new Module())
                .rootComponent(parentComponent)
                .build();
    }

    public CheckInScreen(String visitId) {
        mVisitId = visitId;
    }

    @Override
    public String getScopeName() {
        return super.getScopeName()+"_"+ mVisitId;
    }


    //region ===================== DI =========================

    @dagger.Module
    class Module {

        @Provides
        @DaggerScope(CheckInScreen.class)
        CheckInModel provideCheckInModel() {
            return new CheckInModel();
        }

        @Provides
        @DaggerScope(CheckInScreen.class)
        CheckInScreen.Presenter providePresenter() {
            return new CheckInScreen.Presenter();
        }

    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(CheckInScreen.class)
    public interface Component {
        void inject(Presenter presenter);
        void inject(CheckInView view);

        RootPresenter getRootPresenter();
    }
    //endregion ================== DI =========================

    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<CheckInView, CheckInModel> {

        private RealmObjectChangeListener<VisitRealm> visitChangeListener;
        private VisitRealm mVisit;

        boolean useCamera = false;


        public Presenter() {
        }



        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);

            useCamera = App.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);

            mVisit = mModel.getVisitById(mVisitId);

            mRootPresenter.checkPermissionsAndRequestIfNotGranted(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, ConstantManager.REQUEST_PERMISSION_USE_CAMERA);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);

            visitChangeListener = (realmModel, changeSet) -> {
                if (!realmModel.isLoaded() || !realmModel.isValid() || changeSet != null && changeSet.isDeleted()) {
                    getView().viewOnBackPressed();
                }
            };
            mVisit.addChangeListener(visitChangeListener);
        }

        @Override
        public void dropView(CheckInView view) {
            if (mVisit != null && visitChangeListener != null) {
                mVisit.removeChangeListener(visitChangeListener);
            }
            super.dropView(view);
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .setBackArrow(true)
                    .setTitle(mVisit.getCustomer().getName())
                    .addAction(new MenuItemHolder(App.getStringRes(R.string.check_in), R.drawable.ic_check_in, item ->{
                        doCheckIn();
                        return true;
                    } , ConstantManager.MENU_ITEM_TYPE_ACTION))
                    .build();

        }

        public String getCustomerName() {
            return mVisit.getCustomer()==null?"":mVisit.getCustomer().getName();
        }

        public String getCustomerAddress() {
            return mVisit.getCustomer()==null?"":mVisit.getCustomer().getAddress();
        }

        public void doCheckIn() {
            if (getView() != null){
                float mLat = getView().getLatitude();
                float mLong = getView().getLongitude();
                if (mLat == 0f && mLong == 0f){
                    if (getRootView()!=null){
                        getRootView().showMessage(App.getStringRes(R.string.message_unable_checkin));
                        return;
                    }
                }
                mModel.updateVisitGeolocation(mVisitId, mLat, mLong);
                getView().makeScreenshot();
            }

        }

        public void setScreenshot(Bitmap scr){
            mModel.setVisitScreenshot(mVisitId, scr);

            if (getRootView() != null) {
                getRootView().showMessage("Checked In");
            }
            if (getView() != null) {
                getView().viewOnBackPressed();
            }

        }

        @Nullable
        @Override
        protected IRootView getRootView() {
            return super.getRootView();
        }
    }
    //endregion ================== Presenter =========================

    //region ===================== TreeKey =========================

    @NonNull
    @Override
    public Object getParentKey() {
        return new RoutesScreen();
    }

    //endregion ================== TreeKey =========================    }
}
