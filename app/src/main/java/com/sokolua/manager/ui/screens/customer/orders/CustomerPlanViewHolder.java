package com.sokolua.manager.ui.screens.customer.orders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.OrderPlanRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerPlanViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<OrderPlanRealm> {

    @Nullable    @BindView(R.id.plan_group_text)     TextView mGroupText;
    @Nullable    @BindView(R.id.plan_value_text)     TextView mValueText;
    @Nullable    @BindView(R.id.empty_list_text)     TextView mEmptyText;


    @Inject
    CustomerOrdersScreen.Presenter mPresenter;

    public CustomerPlanViewHolder(View itemView) {
        super(itemView);
        DaggerService.<CustomerOrdersScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);

        if (mEmptyText != null){
            mEmptyText.setText(App.getStringRes(R.string.customer_plan_no_plans));
        }
    }


    @Override
    public void setCurrentItem(OrderPlanRealm currentItem) {
        super.setCurrentItem(currentItem);

        if (currentItem.isValid()) {
            if (mGroupText != null) {
                mGroupText.setText(currentItem.getCategory().getName());
            }

            if (mValueText != null) {
                mValueText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), currentItem.getAmount()));
            }
        }

    }

}
