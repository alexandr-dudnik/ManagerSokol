package com.sokolua.manager.ui.screens.customer.info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.dto.CustomerDto;
import com.sokolua.manager.data.storage.dto.NoteDto;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.flow.Screen;
import com.sokolua.manager.mvp.models.CustomerModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.ui.screens.customer.CustomerScreen;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.IntentStarter;
import com.sokolua.manager.utils.ReactiveRecyclerAdapter;

import javax.inject.Inject;

import dagger.Provides;
import mortar.MortarScope;

@Screen(R.layout.screen_customer_info)
public class CustomerInfoScreen extends AbstractScreen<CustomerScreen.Component> {

    @Override
    public Object createScreenComponent(CustomerScreen.Component parentComponent) {
        return DaggerCustomerInfoScreen_Component
                .builder()
                .module(new Module())
                .component(parentComponent)
                .build();
    }

    public CustomerInfoScreen() {
    }

    //region ===================== DI =========================

    @dagger.Module
    class Module {

        @Provides
        @DaggerScope(CustomerInfoScreen.class)
        CustomerModel provideCustomerModel() {
            return new CustomerModel();
        }

        @Provides
        @DaggerScope(CustomerInfoScreen.class)
        Presenter providePresenter() {
            return new Presenter();
        }

    }

    @dagger.Component(dependencies = CustomerScreen.Component.class, modules = Module.class)
    @DaggerScope(CustomerInfoScreen.class)
    public interface Component {
        void inject(Presenter presenter);

        void inject(CustomerInfoView view);

        void inject(CustomerNoteViewHolder viewHolder);

        void inject(CustomerInfoDataAdapter dataAdapter);
    }
    //endregion ================== DI =========================

    //region ===================== Presenter =========================
    public class Presenter extends AbstractPresenter<CustomerInfoView, CustomerModel> {
        @Inject
        protected CustomerRealm mCustomer;


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



            //Data custom adapter
            CustomerInfoDataAdapter mDataAdapter = new CustomerInfoDataAdapter();
            if (!mCustomer.getName().isEmpty()) {
                mDataAdapter.addItem(new CustomerInfoDataItem(App.getStringRes(R.string.customer_info_name_header), mCustomer.getName(), CustomerInfoDataItem.ACTION_TYPE_NO_ACTION));
            }
            if (!mCustomer.getContactName().isEmpty()) {
                mDataAdapter.addItem(new CustomerInfoDataItem(App.getStringRes(R.string.customer_info_contact_header), mCustomer.getContactName(), CustomerInfoDataItem.ACTION_TYPE_NO_ACTION));
            }
            if (!mCustomer.getAddress().isEmpty()) {
                mDataAdapter.addItem(new CustomerInfoDataItem(App.getStringRes(R.string.customer_info_address_header), mCustomer.getAddress(), CustomerInfoDataItem.ACTION_TYPE_OPEN_MAP));
            }
            if (!mCustomer.getPhone().isEmpty()) {
                mDataAdapter.addItem(new CustomerInfoDataItem(App.getStringRes(R.string.customer_info_phone_header), mCustomer.getPhone(), CustomerInfoDataItem.ACTION_TYPE_MAKE_CALL));
            }
            if (!mCustomer.getEmail().isEmpty()) {
                mDataAdapter.addItem(new CustomerInfoDataItem(App.getStringRes(R.string.customer_info_email_header), mCustomer.getEmail(), CustomerInfoDataItem.ACTION_TYPE_SEND_MAIL));
            }
            getView().setDataAdapter(mDataAdapter);

            //Notes realm adapter
            ReactiveRecyclerAdapter.ReactiveViewHolderFactory<NoteRealm> viewAndHolderFactory = (parent, pViewType) -> {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_info_note_item, parent, false);
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new CustomerNoteViewHolder(view)
                );
            };
            ReactiveRecyclerAdapter mNotesAdapter = new ReactiveRecyclerAdapter(mModel.getCustomerNotes(mCustomer.getCustomerId()), viewAndHolderFactory);
            getView().setNoteAdapter(mNotesAdapter);

        }

        @Override
        protected void initActionBar() {

        }


        public void callToCustomer(CustomerInfoDataItem mItem) {
            if (!IntentStarter.openCaller(mItem.getData()) && getRootView() != null) {
                getRootView().showMessage(App.getStringRes(R.string.error_phone_not_available));
            }
        }

        public void openMap(CustomerInfoDataItem mItem) {
            if (!IntentStarter.openMap(mItem.getData()) && getRootView() != null) {
                getRootView().showMessage(App.getStringRes(R.string.error_google_maps_not_found));
            }
        }

        public void sendEmail(CustomerInfoDataItem mItem) {
            if (!IntentStarter.composeEmail(mItem.getData()) && getRootView() != null) {
                getRootView().showMessage(App.getStringRes(R.string.error_email_not_available));
            }
        }

        public void deleteNote(NoteRealm note) {
            if (getRootView() != null) {
                getRootView().showMessage("Удаление заметки"); //TODO make realization of notes deletion
            }
        }
    }
}
