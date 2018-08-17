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
import com.sokolua.manager.ui.screens.main.MainScreen;
import com.sokolua.manager.utils.App;

import dagger.Provides;
import flow.Direction;
import flow.Flow;
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

            ((Component)scope.getService(DaggerService.SERVICE_NAME)).inject(this);

        }


        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);

            getView().setUserName(mModel.getUserName());
            getView().setUserPassword(mModel.getUserPassword());

            if (getRootView()!=null) {
                getRootView().hideBottomBar();
            }

        }

        @Override
        public void dropView(AuthView view) {
            if (getRootView()!=null) {
                getRootView().showBottomBar();
            }
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
            if (getView() != null && getRootView() != null) {
                if (!mModel.isUserNameValid((getView().getUserName()))) {
                    getView().showInvalidUserName();
                    getRootView().showMessage(App.getStringRes(R.string.error_empty_login));
                    return;
                }
                if (!mModel.isPasswordValid(getView().getUserPassword())) {
                    getView().showInvalidPassword();
                    getRootView().showMessage(App.getStringRes(R.string.error_bad_password));
                    return;
                }

                    //TODO auth user
                    mModel.loginUser(getView().getUserName(),
                            getView().getUserPassword());

                    if (mModel.isUserAuth()) {
                        if (getRootView() != null) {
                            getRootView().showMessage(App.getStringRes(R.string.message_auth_success));
                        }
                        Flow.get(getView()).replaceHistory(new MainScreen(), Direction.REPLACE);
                    }else {
                        getView().login_error();
                        getRootView().showMessage(App.getStringRes(R.string.error_auth_error));
                    }

                }
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
