package com.sokolua.manager.ui.screens.cust_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;

import javax.inject.Inject;

import butterknife.BindView;

public class CustomerListView extends AbstractView<CustomerListScreen.Presenter> {
    @BindView(R.id.customerList)
    RecyclerView mcuxtomerList;

    @Inject
    CustomerListScreen.Presenter mPresenter;

    private CustomerListAdapter mAdapter;


    public CustomerListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<CustomerListScreen.Component>getDaggerComponent(context).inject(this);
            mAdapter = new CustomerListAdapter(true);
        }


    }

    @Override
    public boolean viewOnBackPressed() {
        return false ;
    }

    public String getCustomerFilter(){
        return "";
    }

    public CustomerListAdapter getAdapter(){
        return mAdapter;
    }
}
