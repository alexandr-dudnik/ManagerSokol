package com.sokolua.manager.ui.screens.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.AuthScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.AuthModel;
import com.sokolua.manager.mvp.presenters.IAuthPresenter;
import com.sokolua.manager.mvp.presenters.RootPresenter;
import com.sokolua.manager.mvp.views.IRootView;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.utils.App;

import javax.inject.Inject;

import dagger.Component;
import dagger.Provides;
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



    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @AuthScope
    public interface Component {
        void inject(AuthPresenter presenter);

        void inject(AuthView view);
    }


    //region ===================== Presenter =========================
    public static class AuthPresenter extends ViewPresenter<AuthView> implements IAuthPresenter {
        @Inject
        AuthModel mAuthModel;
        @Inject
        RootPresenter mRootPresenter;


        //for test
        public AuthPresenter(AuthModel authModel, RootPresenter rootPresenter) {
            mAuthModel = authModel;
            mRootPresenter = rootPresenter;
        }

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
            } else {
                if (getRootView() != null) {
                    getRootView().showError(new NullPointerException("Что-то пошло не так..."));
                }
            }

        }

        @Nullable
        private IRootView getRootView() {
            return mRootPresenter.getRootView();
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
                        getRootView().showMessage("Ура! Мы в программе");
                        // TODO: 20.06.2018 Запуск основного экрана
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
    //endregion ============================== DI ==================================

}
