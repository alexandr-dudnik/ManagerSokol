package com.sokolua.manager.ui.screens.customer.orders;

import android.view.View;
import android.widget.TextView;

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

    @BindView(R.id.plan_group_text)
    TextView mGroupText;
    @BindView(R.id.plan_value_text)
    TextView mValueText;

    @Inject
    CustomerOrdersScreen.Presenter mPresenter;

    public CustomerPlanViewHolder(View itemView) {
        super(itemView);
        DaggerService.<CustomerOrdersScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);
    }


    @Override
    public void setCurrentItem(OrderPlanRealm currentItem) {
        super.setCurrentItem(currentItem);

        mGroupText.setText(currentItem.getCategory().getName());
        mValueText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format),currentItem.getAmount()));

    }

}
