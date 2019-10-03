package com.sokolua.manager.ui.screens.routes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.VisitRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.RoutesModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.ui.screens.check_in.CheckInScreen;
import com.sokolua.manager.ui.screens.customer.CustomerScreen;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.IntentStarter;

import java.util.Calendar;

import dagger.Provides;
import flow.Flow;
import io.reactivex.Observable;
import mortar.MortarScope;

@Screen(R.layout.screen_routes)
public class RoutesScreen extends AbstractScreen<RootActivity.RootComponent>{

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerRoutesScreen_Component.builder()
                .module(new Module())
                .rootComponent(parentComponent)
                .build();
    }


    //region ===================== DI =========================

    @dagger.Module
    class Module {

        @Provides
        @DaggerScope(RoutesScreen.class)
        RoutesModel provideRoutesModel() {
            return new RoutesModel();
        }

        @Provides
        @DaggerScope(RoutesScreen.class)
        Presenter providePresenter() {
            return new Presenter();
        }

    }


    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(RoutesScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(RoutesView view);

        void inject(RouteViewHolder viewHolder);
    }
    //endregion ================== DI =========================


    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<RoutesView, RoutesModel> {

        private ReactiveRecyclerAdapter.ReactiveViewHolderFactory<RouteListItem> viewAndHolderFactory;
        private ReactiveRecyclerAdapter reactiveRecyclerAdapter;

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

            viewAndHolderFactory = (parent, pViewType) -> {
                View view;
                switch (pViewType){
                    case ConstantManager.RECYCLER_VIEW_TYPE_HEADER:
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_list_header, parent, false);
                        break;
                    case ConstantManager.RECYCLER_VIEW_TYPE_ITEM:
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_list_item, parent, false);
                        break;
                    default:
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_list_item, parent, false);

                }
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new RouteViewHolder(view)
                );
            };

            reactiveRecyclerAdapter = new ReactiveRecyclerAdapter(Observable.empty(), viewAndHolderFactory, true);
            getView().setAdapter(reactiveRecyclerAdapter);
        }


        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setVisible(true)
                    .setTitle(App.getStringRes(R.string.menu_route))
                    .build();

        }


        public void daySelected(int day) {
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            cal.set(Calendar.AM_PM, 0);
            cal.set(Calendar.HOUR,0);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);

            int curD = cal.get(Calendar.DAY_OF_WEEK)-cal.getFirstDayOfWeek();
            curD = curD<0?(7+curD):curD;
            cal.add(Calendar.DAY_OF_MONTH, day-curD);


            reactiveRecyclerAdapter.refreshList(mModel.getVisitsByDate(cal.getTime()));
        }

        void openCustomerCard(CustomerRealm customer) {
            Flow.get(getView().getContext()).set(new CustomerScreen(customer.getCustomerId()));
        }

        void doCheckIn(VisitRealm visit) {
            Flow.get(getView().getContext()).set(new CheckInScreen(visit.getId()));
        }

        void openCustomerMap(CustomerRealm customer){
            if (!IntentStarter.openMap(customer.getAddress()) && getRootView() != null) {
                getRootView().showMessage(App.getStringRes(R.string.error_google_maps_not_found));
            }
        }

        void callToCustomer(CustomerRealm customer) {
            if (!IntentStarter.openCaller(customer.getPhone()) && getRootView()!= null){
                getRootView().showMessage(App.getStringRes(R.string.error_phone_not_available));
            }
        }
    }

    //endregion ================== Presenter =========================

}
