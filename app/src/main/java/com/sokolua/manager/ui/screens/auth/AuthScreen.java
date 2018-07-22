package com.sokolua.manager.ui.screens.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.AuthScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.AuthModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.mvp.presenters.IAuthPresenter;
import com.sokolua.manager.mvp.presenters.RootPresenter;
import com.sokolua.manager.mvp.views.IRootView;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.screens.main.MainScreen;
import com.sokolua.manager.utils.App;

import javax.inject.Inject;

import dagger.Component;
import dagger.Provides;
import flow.Direction;
import flow.Flow;
import mortar.MortarScope;
import mortar.ViewPresenter;

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
    public static class AuthPresenter extends AbstractPresenter<AuthView, AuthModel> implements IAuthPresenter {
        @Inject
        AuthModel mAuthModel;
        @Inject
        RootPresenter mRootPresenter;



        public AuthPresenter() {
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);

            ((Component)scope.getService(DaggerService.SERVICE_NAME)).inject(this);

        }


        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);

            if (getView() != null) {
                if (getRootView()!=null) {
                    getRootView().hideBottomBar();
                }
            } else {
                if (getRootView() != null) {
                    getRootView().showError(new NullPointerException("Что-то пошло не так..."));
                }
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


        public boolean isUserNameValid(String userName) {

            //return email.matches("^[a-z0-9_]([a-z0-9_-]+\\.*)+[a-z0-9_]@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$");
            return !userName.isEmpty();
        }

        public boolean isPasswordValid(String pass) {
            return pass.length() >= 8;
        }

        @Override
        public void clickOnLogin() {
            if (getView() != null && getRootView() != null) {
                if (!isUserNameValid((getView().getUserName()))) {
                    getView().showInvalidUserName();
                    getRootView().showMessage(App.getStringRes(R.string.error_empty_login));
                    return;
                }
                if (!isPasswordValid(getView().getUserPassword())) {
                    getView().showInvalidPassword();
                    getRootView().showMessage(App.getStringRes(R.string.error_bad_password));
                    return;
                }

                    //TODO auth user
                    mAuthModel.loginUser(getView().getUserName(),
                            getView().getUserPassword());

                    if (mAuthModel.isUserAuth()) {
                        Flow.get(getView()).replaceHistory(new MainScreen(), Direction.REPLACE);
                    }else {
                        getView().login_error();
                        getRootView().showMessage(App.getStringRes(R.string.error_auth_error));
                    }

                }
            }

        @Override
        public boolean checkUserAuth() {
            return mAuthModel.isUserAuth();
        }

    }
    //endregion ================== Presenter =========================

    //region ================================= DI ==================================
    @dagger.Module
    public class Module {
        @Provides
        @AuthScope
        AuthScreen.AuthPresenter providePresenter() {
            return new AuthScreen.AuthPresenter();
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
        void inject(AuthPresenter presenter);

        void inject(AuthView view);
    }
    //endregion ============================== DI ==================================

}
