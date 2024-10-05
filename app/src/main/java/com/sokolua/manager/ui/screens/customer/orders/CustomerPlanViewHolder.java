package com.sokolua.manager.ui.screens.customer.orders;

import android.view.View;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.OrderPlanRealm;
import com.sokolua.manager.databinding.CustomerPlanItemBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.util.Locale;

import javax.inject.Inject;

public class CustomerPlanViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<OrderPlanRealm> {
    @Inject
    CustomerOrdersScreen.Presenter mPresenter;

    private CustomerPlanItemBinding binding;

    public CustomerPlanViewHolder(View itemView) {
        super(itemView);
        DaggerService.<CustomerOrdersScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);

        TextView mEmptyText = itemView.findViewById(R.id.empty_list_text);
        if (mEmptyText != null) {
            mEmptyText.setText(App.getStringRes(R.string.customer_plan_no_plans));
        }
    }

    @Override
    public void setCurrentItem(OrderPlanRealm currentItem) {
        super.setCurrentItem(currentItem);
        binding = CustomerPlanItemBinding.bind(itemView);

        if (currentItem.isValid()) {
            binding.planGroupText.setText(currentItem.getCategory().getName());
            binding.planValueText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), currentItem.getAmount()));
        }
    }

}
