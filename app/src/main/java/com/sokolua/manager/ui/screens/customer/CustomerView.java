package com.sokolua.manager.ui.screens.customer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;

import javax.inject.Inject;

import butterknife.BindView;
import flow.Flow;

public class CustomerView extends AbstractView<CustomerScreen.Presenter> {
    @BindView(R.id.tabs_pager)
    ViewPager mViewPager;


    @Inject
    CustomerScreen.Presenter mPresenter;

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
        CustomerPagerAdapter adapter = new CustomerPagerAdapter();
        mViewPager.setAdapter(adapter);
        super.onAttachedToWindow();
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }
}
