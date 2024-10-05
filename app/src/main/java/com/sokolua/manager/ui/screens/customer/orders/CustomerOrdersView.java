package com.sokolua.manager.ui.screens.customer.orders;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sokolua.manager.databinding.ScreenCustomerOrdersBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

public class CustomerOrdersView extends AbstractView<CustomerOrdersScreen.Presenter, ScreenCustomerOrdersBinding> {

    public CustomerOrdersView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ScreenCustomerOrdersBinding bindView(View view) {
        return ScreenCustomerOrdersBinding.bind(view);
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
        binding.planList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.planList.setAdapter(planAdapter);
    }

    public void setOrdersAdapter(ReactiveRecyclerAdapter ordersAdapter) {
        binding.ordersList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.ordersList.setAdapter(ordersAdapter);
    }
}
