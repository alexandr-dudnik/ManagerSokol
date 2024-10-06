package com.sokolua.manager.ui.screens.order_list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sokolua.manager.databinding.ScreenOrderListBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

public class OrderListView extends AbstractView<OrderListScreen.Presenter, ScreenOrderListBinding> {
    public OrderListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<OrderListScreen.Component>getDaggerComponent(context).inject(this);
        }
    }

    @Override
    protected ScreenOrderListBinding bindView(View view) {
        return ScreenOrderListBinding.bind(view);
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public void setAdapter(ReactiveRecyclerAdapter mAdapter) {
        binding.orderList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.orderList.setAdapter(mAdapter);
    }

}
