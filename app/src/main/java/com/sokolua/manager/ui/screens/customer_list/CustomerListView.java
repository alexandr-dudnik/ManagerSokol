package com.sokolua.manager.ui.screens.customer_list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sokolua.manager.databinding.ScreenCustomerListBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

public class CustomerListView extends AbstractView<CustomerListScreen.Presenter, ScreenCustomerListBinding> {

    public CustomerListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ScreenCustomerListBinding bindView(View view) {
        return ScreenCustomerListBinding.bind(view);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<CustomerListScreen.Component>getDaggerComponent(context).inject(this);
        }
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public void setAdapter(ReactiveRecyclerAdapter mAdapter) {
        binding.customerList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.customerList.setAdapter(mAdapter);
    }

}
