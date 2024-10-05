package com.sokolua.manager.ui.screens.main;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.mvp.models.AuthModel;
import com.sokolua.manager.mvp.models.MainModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.mvp.presenters.MenuItemHolder;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.screens.settings.SettingsScreen;
import com.sokolua.manager.utils.App;

import javax.inject.Inject;

import dagger.Provides;
import flow.Flow;
import mortar.MortarScope;

public class MainScreen extends AbstractScreen<RootActivity.RootComponent> {

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerMainScreen_Component.builder()
                .module(new Module())
                .rootComponent(parentComponent)
                .build();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.screen_main;
    }

    //region ===================== DI =========================

    @dagger.Module
    class Module {

        @Provides
        @DaggerScope(MainScreen.class)
        MainModel provideMainModel() {
            return new MainModel();
        }

        @Provides
        @DaggerScope(MainScreen.class)
        Presenter provideMainPresenter() {
            return new Presenter();
        }

        @Provides
        @DaggerScope(MainScreen.class)
        AuthModel provideAuthModel() {
            return new AuthModel();
        }
    }


    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(MainScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(MainView view);
    }
    //endregion ================== DI =========================

    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<MainView, MainModel> {
        @Inject
        AuthModel mAuthModel;

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);

            if (getRootView() != null) {
                getRootView().setBottomBarVisibility(true);
            }
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .addAction(new MenuItemHolder(App.getStringRes(R.string.menu_settings), R.drawable.ic_settings, item -> {
                        Flow.get(getView()).set(new SettingsScreen());
                        return true;
                    }, ConstantManager.MENU_ITEM_TYPE_ACTION))
                    .setTitle(mAuthModel.getManagerName())
                    .build();
        }

        void clickOnGoods() {
            if (getRootView() != null) {
                ((RootActivity) getRootView()).selectNavigationMenu(R.id.bottomBarGoods);
            }
        }

        void clickOnCustomers() {
            if (getRootView() != null) {
                ((RootActivity) getRootView()).selectNavigationMenu(R.id.bottomBarCustomers);
            }
        }

        void clickOnRoutes() {
            if (getRootView() != null) {
                ((RootActivity) getRootView()).selectNavigationMenu(R.id.bottomBarRoute);
            }
        }

        void clickOnOrders() {
            if (getRootView() != null) {
                ((RootActivity) getRootView()).selectNavigationMenu(R.id.bottomBarOrders);
            }
        }

    }
    //endregion ================== Presenter =========================

}
