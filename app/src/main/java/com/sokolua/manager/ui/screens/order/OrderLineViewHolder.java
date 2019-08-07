package com.sokolua.manager.ui.screens.order;

import android.view.View;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderLineViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<OrderLineRealm> {

    @BindView(R.id.good_article_text)   TextView mArticle;
    @BindView(R.id.good_name_text)      TextView mName;
    @BindView(R.id.good_price_text)     TextView mPrice;
    @BindView(R.id.good_quantity_text)  TextView mQty;



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

        if (currentItem.isValid()) {
            mArticle.setText(currentItem.getItem().getArtNumber());
            mName.setText(currentItem.getItem().getName());
            mPrice.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), currentItem.getPrice()));
            mQty.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int), currentItem.getQuantity()));
        }
    }


    @OnClick(R.id.good_price_text)
    void onPriceClick(){
        mPresenter.updatePrice(currentItem);
    }

    @OnClick(R.id.good_quantity_text)
    void onQuantityClick(){
        mPresenter.updateQuantity(currentItem);
    }

}
