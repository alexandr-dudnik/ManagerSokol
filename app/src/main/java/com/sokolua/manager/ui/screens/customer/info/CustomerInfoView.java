package com.sokolua.manager.ui.screens.customer.info;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.utils.App;

import javax.inject.Inject;

import butterknife.BindView;

public class CustomerInfoView extends AbstractView<CustomerInfoScreen.Presenter>{
    @BindView(R.id.customerList)
    RecyclerView mCustomerList;

    @Inject
    CustomerInfoScreen.Presenter mPresenter;

    private CustomerInfoDataAdapter mDataAdapter;

    public CustomerInfoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CustomerInfoScreen.Component>getDaggerComponent(context).inject(this);
        mDataAdapter = new CustomerInfoDataAdapter();
    }


    @Override
    public boolean viewOnBackPressed() {
        return false;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public CustomerInfoDataAdapter getmDataAdapter() {
        return mDataAdapter;
    }

    public void showCustomerList() {
        mCustomerList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL,false));
        mCustomerList.setAdapter(mDataAdapter);
    }

}
