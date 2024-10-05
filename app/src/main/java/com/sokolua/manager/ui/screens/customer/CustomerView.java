package com.sokolua.manager.ui.screens.customer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.sokolua.manager.databinding.ScreenCustomerBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;

import flow.Flow;

public class CustomerView extends AbstractView<CustomerScreen.Presenter, ScreenCustomerBinding> {
    private CustomerPagerAdapter mAdapter;

    public CustomerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ScreenCustomerBinding bindView(View view) {
        return ScreenCustomerBinding.bind(view);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<CustomerScreen.Component>getDaggerComponent(context).inject(this);
        }
    }

    @Override
    public boolean viewOnBackPressed() {
        return Flow.get(this.getContext()).goBack();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAdapter = new CustomerPagerAdapter();
        binding.tabsPager.setAdapter(mAdapter);
    }

    public ViewPager getViewPager() {
        return binding.tabsPager;
    }

    public CustomerPagerAdapter getAdapter() {
        return mAdapter;
    }
}
