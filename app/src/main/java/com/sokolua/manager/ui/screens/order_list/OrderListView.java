package com.sokolua.manager.ui.screens.order_list;

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

public class OrderListView extends AbstractView<OrderListScreen.Presenter> {
    @BindView(R.id.order_list)
    RecyclerView mOrderList;


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
    public boolean viewOnBackPressed() {
        return false ;
    }


    public void setAdapter(ReactiveRecyclerAdapter mAdapter) {
        mOrderList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL,false));
        mOrderList.setAdapter(mAdapter);
    }

}
