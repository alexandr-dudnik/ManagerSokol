package com.sokolua.manager.ui.screens.customer;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;

import butterknife.BindView;
import flow.Flow;

public class CustomerView extends AbstractView<CustomerScreen.Presenter> {
    @BindView(R.id.tabs_pager)
    ViewPager mViewPager;

    private CustomerPagerAdapter mAdapter;


    public CustomerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
        mAdapter = new CustomerPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        super.onAttachedToWindow();
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public CustomerPagerAdapter getAdapter() {
        return mAdapter;
    }
}
