package com.sokolua.manager.ui.screens.customer_list;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.viewbinding.ViewBinding;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.databinding.CustomerListHeaderBinding;
import com.sokolua.manager.databinding.CustomerListItemBinding;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

public class CustomerViewHolder<B extends ViewBinding> extends ReactiveRecyclerAdapter.ReactiveViewHolder<CustomerListItem> {
    final private B binding;
    final private Consumer<CustomerRealm> openMap;
    final private Consumer<CustomerRealm> openCall;
    final private Consumer<CustomerRealm> openCustomer;

    public CustomerViewHolder(
            View itemView,
            B binding,
            @Nullable Consumer<CustomerRealm> openMapFun,
            @Nullable Consumer<CustomerRealm> openCallFun,
            @Nullable Consumer<CustomerRealm> openCustomerFun
    ) {
        super(itemView);
        this.binding = binding;
        openMap = openMapFun;
        openCall = openCallFun;
        openCustomer = openCustomerFun;
    }

    @Override
    public void setCurrentItem(CustomerListItem currentItem) {
        super.setCurrentItem(currentItem);
        updateFields(currentItem);
    }

    private void updateFields(CustomerListItem currentItem) {
        if (currentItem != null) {
            if (currentItem.isHeader() && (binding instanceof CustomerListHeaderBinding)) {
                ((CustomerListHeaderBinding) binding).itemHeaderText.setText(currentItem.getHeaderText());
            } else if (currentItem.getCustomer() != null && currentItem.getCustomer().isValid()) {
                switch (DataManager.getInstance().getCustomerDebtType(currentItem.getCustomer().getCustomerId())) {
                    case ConstantManager.DEBT_TYPE_NORMAL:
                        ((CustomerListItemBinding) binding).exclamationImg.setVisibility(View.VISIBLE);
                        ((CustomerListItemBinding) binding).exclamationImg.setColorFilter(App.getColorRes(R.color.color_orange));
                        break;
                    case ConstantManager.DEBT_TYPE_OUTDATED:
                        ((CustomerListItemBinding) binding).exclamationImg.setVisibility(View.VISIBLE);
                        ((CustomerListItemBinding) binding).exclamationImg.setColorFilter(App.getColorRes(R.color.color_red));
                        break;
                    default:
                        ((CustomerListItemBinding) binding).exclamationImg.setVisibility(View.INVISIBLE);
                }
                ((CustomerListItemBinding) binding).customerNameText.setText(currentItem.getCustomer().getName());
                ((CustomerListItemBinding) binding).mapPinImg.setVisibility((currentItem.getCustomer().getAddress() == null || currentItem.getCustomer().getAddress().isEmpty()) ? View.INVISIBLE : View.VISIBLE);
                ((CustomerListItemBinding) binding).callImg.setVisibility((currentItem.getCustomer().getPhone() == null || currentItem.getCustomer().getPhone().isEmpty()) ? View.INVISIBLE : View.VISIBLE);

                ((CustomerListItemBinding) binding).mapPinImg.setOnClickListener(view -> {
                    if (currentItem.getCustomer() != null && openMap != null) {
                        openMap.accept(currentItem.getCustomer());
                    }
                });
                ((CustomerListItemBinding) binding).callImg.setOnClickListener(view -> {
                    if (currentItem.getCustomer() != null && openCall != null) {
                        openCall.accept(currentItem.getCustomer());
                    }
                });
                itemView.setOnClickListener(view -> {
                    if (currentItem.getCustomer() != null && openCustomer != null) {
                        openCustomer.accept(currentItem.getCustomer());
                    }
                });
            }
        }
    }
}
