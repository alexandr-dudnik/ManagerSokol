package com.sokolua.manager.ui.screens.customer.tasks;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.utils.App;

import javax.inject.Inject;

import butterknife.BindView;

public class CustomerTasksView extends AbstractView<CustomerTasksScreen.Presenter>{
    @BindView(R.id.debt_list)
    RecyclerView mDebtList;
    @BindView(R.id.tasks_list)
    RecyclerView mTaskList;

    @Inject
    CustomerTasksScreen.Presenter mPresenter;

    private CustomerDebtAdapter mDebtAdapter;
    private CustomerTaskAdapter mTaskAdapter;

    public CustomerTasksView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CustomerTasksScreen.Component>getDaggerComponent(context).inject(this);

        mDebtAdapter = new CustomerDebtAdapter();
        mTaskAdapter = new CustomerTaskAdapter();
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void showData() {
        mDebtList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL,false));
        mDebtList.setAdapter(mDebtAdapter);

        mTaskList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL,false));
        mTaskList.setAdapter(mTaskAdapter);
    }


    public CustomerDebtAdapter getDebtAdapter() {
        return mDebtAdapter;
    }

    public CustomerTaskAdapter getTaskAdapter() {
        return mTaskAdapter;
    }
}
