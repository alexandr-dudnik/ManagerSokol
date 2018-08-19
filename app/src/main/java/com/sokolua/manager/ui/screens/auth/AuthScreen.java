package com.sokolua.manager.ui.screens.auth;

import android.os.Bundle;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.AuthScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.AuthModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.mvp.presenters.IAuthPresenter;
import com.sokolua.manager.ui.activities.RootActivity;

import dagger.Provides;
import mortar.MortarScope;

@Screen(R.layout.screen_auth)
public class AuthScreen extends AbstractScreen<RootActivity.RootComponent> {
    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerAuthScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }


    //region ===================== Presenter =========================
    public static class Presenter extends AbstractPresenter<AuthView, AuthModel> implements IAuthPresenter {


        public Presenter() {
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);

            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);

            if (getRootView() != null) {
                getRootView().setBottomBarVisibility(false);
            }
        }


        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);

            getView().setUserName(mModel.getUserName());
            getView().setUserPassword(mModel.getUserPassword());

            if (!mModel.getUserName().isEmpty() && !mModel.getUserPassword().isEmpty()){
                clickOnLogin();
            }

        }

        @Override
        public void dropView(AuthView view) {
            super.dropView(view);
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setVisible(false)
                    .build();
        }

        @Override
        public void clickOnLogin() {
            mRootPresenter.doUserLogin(getView().getUserName(), getView().getUserPassword());
        }

        @Override
        public boolean checkUserAuth() {
            return mModel.isUserAuth();
        }

    }
    //endregion ================== Presenter =========================

    //region ================================= DI ==================================
    @dagger.Module
    public class Module {
        @Provides
        @AuthScope
        Presenter providePresenter() {
            return new Presenter();
        }

        @Provides
        @AuthScope
        AuthModel provideAuthModel() {
            return new AuthModel();
        }
    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @AuthScope
    public interface Component {
        void inject(Presenter presenter);

        void inject(AuthView view);
    }
    //endregion ============================== DI ==================================

}
