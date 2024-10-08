package com.sokolua.manager.ui.screens.customer.info;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.CustomerPhoneRealm;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.flow.AbstractScreen;
import com.sokolua.manager.mvp.models.CustomerModel;
import com.sokolua.manager.mvp.presenters.AbstractPresenter;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.ui.screens.customer.CustomerScreen;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.IntentStarter;

import javax.inject.Inject;

import dagger.Provides;
import io.realm.RealmObjectChangeListener;
import mortar.MortarScope;

public class CustomerInfoScreen extends AbstractScreen<CustomerScreen.Component> {

    @Override
    public Object createScreenComponent(CustomerScreen.Component parentComponent) {
        return DaggerCustomerInfoScreen_Component
                .builder()
                .module(new Module())
                .component(parentComponent)
                .build();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.screen_customer_info;
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
        private ReactiveRecyclerAdapter<NoteRealm> mNotesAdapter;
        private ReactiveRecyclerAdapter.ReactiveViewHolderFactory<NoteRealm> viewAndHolderFactory;
        private CustomerInfoDataAdapter mDataAdapter;
        private RealmObjectChangeListener<CustomerRealm> mCustomerChangeListener;

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);

        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);

            //Data custom adapter
            mDataAdapter = new CustomerInfoDataAdapter();
            getView().setDataAdapter(mDataAdapter);
            updateCustomerData();
            mCustomerChangeListener = (realmModel, changeSet) -> {
                if (changeSet != null && changeSet.isDeleted() || !realmModel.isValid() || !realmModel.isLoaded()) {
                    realmModel.removeAllChangeListeners();
                } else {
                    updateCustomerData();
                }
            };
            mCustomer.addChangeListener(mCustomerChangeListener);

            //Notes realm adapter
            viewAndHolderFactory = (parent, pViewType) -> {
                View view;
                if (pViewType == ConstantManager.RECYCLER_VIEW_TYPE_EMPTY) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_list_item, parent, false);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_info_note_item, parent, false);
                }
                return new ReactiveRecyclerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
                        view,
                        new CustomerNoteViewHolder(view)
                );
            };
            mNotesAdapter = new ReactiveRecyclerAdapter<>(
                    mModel.getCustomerNotes(mCustomer.getCustomerId()),
                    viewAndHolderFactory,
                    true
            );
            getView().setNoteAdapter(mNotesAdapter);
        }

        private void updateCustomerData() {
            CustomerInfoDataItem item;
            item = new CustomerInfoDataItem(App.getStringRes(R.string.customer_info_name_header), mCustomer.getName(), CustomerInfoDataItem.ACTION_TYPE_NO_ACTION);
            if (!mCustomer.getName().isEmpty()) {
                mDataAdapter.addItem(item);
            } else {
                mDataAdapter.removeItem(item);
            }
            item = new CustomerInfoDataItem(App.getStringRes(R.string.customer_info_contact_header), mCustomer.getContactName(), CustomerInfoDataItem.ACTION_TYPE_NO_ACTION);
            if (!mCustomer.getContactName().isEmpty()) {
                mDataAdapter.addItem(item);
            } else {
                mDataAdapter.removeItem(item);
            }
            if (!mCustomer.getCategory().isEmpty()) {
                mDataAdapter.addItem(new CustomerInfoDataItem(App.getStringRes(R.string.customer_info_category_header), mCustomer.getCategory(), CustomerInfoDataItem.ACTION_TYPE_NO_ACTION));
            } else {
                mDataAdapter.addItem(new CustomerInfoDataItem(App.getStringRes(R.string.customer_info_category_header), App.getStringRes(R.string.customer_info_category_no_category), CustomerInfoDataItem.ACTION_TYPE_NO_ACTION));
            }
            item = new CustomerInfoDataItem(App.getStringRes(R.string.customer_info_address_header), mCustomer.getAddress(), CustomerInfoDataItem.ACTION_TYPE_OPEN_MAP);
            if (!mCustomer.getAddress().isEmpty()) {
                mDataAdapter.addItem(item);
            } else {
                mDataAdapter.removeItem(item);
            }
            for (CustomerPhoneRealm phone : mCustomer.getPhones()) {
                item = new CustomerInfoDataItem(App.getStringRes(R.string.customer_info_phone_header), phone.getPhoneNumber(), CustomerInfoDataItem.ACTION_TYPE_MAKE_CALL);
                if (!phone.getPhoneNumber().isEmpty()) {
                    mDataAdapter.addItem(item);
                } else {
                    mDataAdapter.removeItem(item);
                }
            }
            item = new CustomerInfoDataItem(App.getStringRes(R.string.customer_info_email_header), mCustomer.getEmail(), CustomerInfoDataItem.ACTION_TYPE_SEND_MAIL);
            if (!mCustomer.getEmail().isEmpty()) {
                mDataAdapter.addItem(item);
            } else {
                mDataAdapter.removeItem(item);
            }
        }

        @Override
        public void dropView(CustomerInfoView view) {
            if (mCustomer != null && mCustomerChangeListener != null) {
                mCustomer.removeChangeListener(mCustomerChangeListener);
            }
            super.dropView(view);
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

        public void deleteNote(String noteId) {
            mModel.deleteNote(noteId);
        }

        public void addNewNote() {
            final TextInputEditText input = new TextInputEditText(getView().getContext());
            input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

            AlertDialog.Builder alert = new AlertDialog.Builder(getView().getContext())
                    .setTitle(App.getStringRes(R.string.customer_info_notes))
                    .setMessage(App.getStringRes(R.string.customer_add_new_note_title))
                    .setCancelable(false)
                    .setView(input)
                    .setPositiveButton(App.getStringRes(R.string.button_positive_text), (dialog, whichButton) -> {
                        String newNote = input.getText() == null ? "" : input.getText().toString();
                        mModel.addNewNote(mCustomer.getCustomerId(), newNote);
                    })
                    .setNegativeButton(App.getStringRes(R.string.button_negative_text), (dialog, whichButton) -> {
                    });
            alert.show();
        }
    }
}
