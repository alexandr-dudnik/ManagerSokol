package com.sokolua.manager.ui.screens.main;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.MainModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.mvp.presenters.RootPresenter;
import com.sokolua.manager.ui.activities.RootActivity;

import dagger.Provides;
import mortar.MortarScope;

@Screen(R.layout.screen_main)
public class MainScreen extends AbstractScreen<RootActivity.RootComponent> {

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerMainScreen_Component.builder()
                .module(new Module())
                .rootComponent(parentComponent)
                .build();
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

    }


    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(MainScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(MainView view);

        RootPresenter getRootPresenter();
    }
    //endregion ================== DI =========================

    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<MainView, MainModel> {

        public Presenter() {
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }


        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .setTitle("Вася Пупкин") //TODO - Имя менеджера
                    .build();

            //mRootPresenter.hideFab();
        }


        void clickOnGoods(){
            if (getRootView()!=null) {
                getRootView().showMessage("Здесь будут товыры");
            }
        }

        void clickOnCustomers(){
            if (getRootView()!=null) {
                getRootView().showMessage("Здесь будут клиенты");
            }
        }

        void clickOnRoutes(){
            if (getRootView()!=null) {
                getRootView().showMessage("Здесь будут маршруты");
            }
        }

        void clickOnOrders(){
            if (getRootView()!=null) {
                getRootView().showMessage("Здесь будут заказы");
            }
        }

    }
    //endregion ================== Presenter =========================
}
