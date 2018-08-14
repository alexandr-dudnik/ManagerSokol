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
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import butterknife.BindView;

public class CustomerInfoView extends AbstractView<CustomerInfoScreen.Presenter>{
    @BindView(R.id.customer_info_list)
    RecyclerView mCustomerInfoList;
    @BindView(R.id.customer_notes_list)
    RecyclerView mCustomerNotesList;


    public CustomerInfoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CustomerInfoScreen.Component>getDaggerComponent(context).inject(this);
    }


    @Override
    public boolean viewOnBackPressed() {
        return false;
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    public void setNoteAdapter(ReactiveRecyclerAdapter mNoteAdapter) {
        mCustomerNotesList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL,false));
        mCustomerNotesList.setAdapter(mNoteAdapter);
    }

    public void setDataAdapter(CustomerInfoDataAdapter mDataAdapter) {
        mCustomerInfoList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL,false));
        mCustomerInfoList.setAdapter(mDataAdapter);
    }


}
