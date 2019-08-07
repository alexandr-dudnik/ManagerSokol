package com.sokolua.manager.ui.screens.goods;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class GoodsView extends AbstractView<GoodsScreen.Presenter> {
    @BindView(R.id.groups_grid)         RecyclerView mGrid;
    @BindView(R.id.item_list)           RecyclerView mItems;
    @BindView(R.id.cart_panel)          RelativeLayout mCartPanel;
    @BindView(R.id.cart_customer_text)  TextView mCustomerName;
    @BindView(R.id.cart_currency)       TextView mCartCurrency;
    @BindView(R.id.cart_amount)         TextView mCartAmount;
    @BindView(R.id.cart_items_counter)  TextView mCartItemsCount;


    public GoodsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<GoodsScreen.Component>getDaggerComponent(context).inject(this);
        }


    }

    @Override
    public boolean viewOnBackPressed() {
        return mPresenter.goGroupBack() ;
    }


    public void setGroupsAdapter(ReactiveRecyclerAdapter mAdapter) {
        mGrid.setHasFixedSize(true);
        mGrid.setLayoutManager(new GridLayoutManager(getContext(), 3)); //в три колонки
        mGrid.setAdapter(mAdapter);
    }

    public void setItemsAdapter(ReactiveRecyclerAdapter mAdapter) {
        mItems.setHasFixedSize(true);
        mItems.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        mItems.setAdapter(mAdapter);
    }

    public void showGroups() {
        if (mGrid.getAlpha() == 0f) {
            mItems.setAlpha(0f);
            mItems.setVisibility(GONE);
            mGrid.setVisibility(VISIBLE);
            mGrid.animate()
                    .setDuration(500)
                    .alpha(1f)
                    .start();
        }
    }
    public void showItems() {
        if (mItems.getAlpha() == 0f) {
            mGrid.setAlpha(0f);
            mGrid.setVisibility(GONE);
            mItems.setVisibility(VISIBLE);
            mItems.animate()
                    .setDuration(500)
                    .alpha(1f)
                    .start();
        }
    }



    public void setCartMode(){
        mCartPanel.setVisibility(VISIBLE);
    }
    public void setCatalogMode(){
        mCartPanel.setVisibility(GONE);
    }

    public void setCustomer(String customerName){
        mCustomerName.setText(customerName);
    }

    public void setCartCurrency(String currency){
        mCartCurrency.setText(currency);
    }

    public void setCartAmount(Float amount){
        mCartAmount.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format),amount));
    }

    public void setCartItemsCount(int count){
        mCartItemsCount.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int),(count+0.f)));
    }


    @OnClick(R.id.cart_image)
    void onCartClick(View v){
        mPresenter.returnToCart();
    }

}
