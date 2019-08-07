package com.sokolua.manager.ui.screens.customer.orders;


import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import butterknife.BindView;

public class CustomerOrdersView extends AbstractView<CustomerOrdersScreen.Presenter>{

    @BindView(R.id.plan_list)
    RecyclerView mPlanList;

    @BindView(R.id.orders_list)
    RecyclerView mOrdersList;


    public CustomerOrdersView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CustomerOrdersScreen.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    public void setPlanAdapter(ReactiveRecyclerAdapter planAdapter) {
        mPlanList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL,false));
        mPlanList.setAdapter(planAdapter);
    }

    public void setOrdersAdapter(ReactiveRecyclerAdapter ordersAdapter) {
        mOrdersList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL,false));
        mOrdersList.setAdapter(ordersAdapter);
    }
}
