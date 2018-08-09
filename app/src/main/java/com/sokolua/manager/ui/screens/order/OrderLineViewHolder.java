package com.sokolua.manager.ui.screens.order;

import android.view.View;

import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class OrderLineViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<OrderLineRealm> {


    @Inject
    OrderScreen.Presenter mPresenter;

    public OrderLineViewHolder(View itemView) {
        super(itemView);
        DaggerService.<OrderScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);
    }


    @Override
    public void setCurrentItem(OrderLineRealm currentItem) {
        super.setCurrentItem(currentItem);

    }

}
