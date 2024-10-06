package com.sokolua.manager.ui.screens.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.sokolua.manager.databinding.ScreenMainBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.mvp.views.IView;

public class MainView extends AbstractView<MainScreen.Presenter, ScreenMainBinding> implements IView {
    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ScreenMainBinding bindView(View view) {
        return ScreenMainBinding.bind(view);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<MainScreen.Component>getDaggerComponent(context).inject(this);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        binding.customersImg.setOnClickListener(view -> mPresenter.clickOnCustomers());
        binding.customersText.setOnClickListener(view -> mPresenter.clickOnCustomers());

        binding.goodsImg.setOnClickListener(view -> mPresenter.clickOnGoods());
        binding.goodsText.setOnClickListener(view -> mPresenter.clickOnGoods());

        binding.routeImg.setOnClickListener(view -> mPresenter.clickOnRoutes());
        binding.routeText.setOnClickListener(view -> mPresenter.clickOnRoutes());

        binding.ordersImg.setOnClickListener(view -> mPresenter.clickOnOrders());
        binding.ordersText.setOnClickListener(view -> mPresenter.clickOnOrders());
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

}
