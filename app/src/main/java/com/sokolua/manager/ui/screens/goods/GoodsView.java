package com.sokolua.manager.ui.screens.goods;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sokolua.manager.R;
import com.sokolua.manager.databinding.ScreenGoodsBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.util.Locale;

public class GoodsView extends AbstractView<GoodsScreen.Presenter, ScreenGoodsBinding> {

    public GoodsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ScreenGoodsBinding bindView(View view) {
        return ScreenGoodsBinding.bind(view);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<GoodsScreen.Component>getDaggerComponent(context).inject(this);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        binding.cartImage.setOnClickListener(view -> mPresenter.returnToCart());
    }

    @Override
    public boolean viewOnBackPressed() {
        return mPresenter.goGroupBack();
    }

    public void setGroupsAdapter(ReactiveRecyclerAdapter<?> mAdapter) {
        binding.groupsGrid.setHasFixedSize(true);
        binding.groupsGrid.setLayoutManager(new GridLayoutManager(getContext(), 3)); //в три колонки
        binding.groupsGrid.setAdapter(mAdapter);
    }

    public void setItemsAdapter(ReactiveRecyclerAdapter<?> mAdapter) {
        binding.itemList.setHasFixedSize(true);
        binding.itemList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.itemList.setAdapter(mAdapter);
    }

    public void showGroups() {
        if (binding.groupsGrid.getAlpha() == 0f) {
            binding.itemList.setAlpha(0f);
            binding.itemList.setVisibility(GONE);
            binding.groupsGrid.setVisibility(VISIBLE);
            binding.groupsGrid.animate()
                    .setDuration(500)
                    .alpha(1f)
                    .start();
        }
    }

    public void showItems() {
        if (binding.itemList.getAlpha() == 0f) {
            binding.groupsGrid.setAlpha(0f);
            binding.groupsGrid.setVisibility(GONE);
            binding.itemList.setVisibility(VISIBLE);
            binding.itemList.animate()
                    .setDuration(500)
                    .alpha(1f)
                    .start();
        }
    }

    public void setCartMode() {
        binding.cartPanel.setVisibility(VISIBLE);
    }

    public void setCatalogMode() {
        binding.cartPanel.setVisibility(GONE);
    }

    public void setCustomer(String customerName) {
        binding.cartCustomerText.setText(customerName);
    }

    public void setCartCurrency(String currency) {
        binding.cartCurrency.setText(currency);
    }

    public void setCartAmount(Float amount) {
        binding.cartAmount.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), amount));
    }

    public void setCartItemsCount(int count) {
        binding.cartItemsCounter.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int), (count + 0.f)));
    }

}
