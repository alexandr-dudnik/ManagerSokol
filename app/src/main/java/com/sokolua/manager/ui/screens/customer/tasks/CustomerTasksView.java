package com.sokolua.manager.ui.screens.customer.tasks;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sokolua.manager.databinding.ScreenCustomerTasksBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

public class CustomerTasksView extends AbstractView<CustomerTasksScreen.Presenter, ScreenCustomerTasksBinding> {
    public CustomerTasksView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ScreenCustomerTasksBinding bindView(View view) {
        return ScreenCustomerTasksBinding.bind(view);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CustomerTasksScreen.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    public boolean viewOnBackPressed() {
        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setDebtAdapter(ReactiveRecyclerAdapter mDebtAdapter) {
        binding.debtList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.debtList.setAdapter(mDebtAdapter);
    }

    public void setTaskAdapter(ReactiveRecyclerAdapter mTaskAdapter) {
        binding.tasksList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.tasksList.setAdapter(mTaskAdapter);
    }

}
