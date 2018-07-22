package com.sokolua.manager.ui.screens.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.mvp.views.IView;

import butterknife.OnClick;

public class MainView extends AbstractView<MainScreen.MainPresenter> implements IView {
    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<MainScreen.Component>getDaggerComponent(context).inject(this);
        }
    }

    @Override
    public boolean viewOnBackPressed() {
        return false ;
    }

    //region ===================== Events =========================

    @OnClick({R.id.customers_text,R.id.customers_img})
    public void customersClick(View view){
        mPresenter.clickOnCustomers();
    }

    @OnClick({R.id.goods_text,R.id.goods_img})
    public void goodsClick(View view){
        mPresenter.clickOnGoods();
    }

    @OnClick({R.id.route_text,R.id.route_img})
    public void routeClick(View view){
        mPresenter.clickOnRoutes();
    }

    @OnClick({R.id.orders_text,R.id.orders_img})
    public void ordersClick(View view){
        mPresenter.clickOnOrders();
    }
    //endregion ================== Events =========================


}
