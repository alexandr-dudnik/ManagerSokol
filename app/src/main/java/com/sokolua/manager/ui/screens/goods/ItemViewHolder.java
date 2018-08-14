package com.sokolua.manager.ui.screens.goods;

import android.view.View;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ItemViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<ItemRealm> {

    @BindView(R.id.good_article_text)     TextView mArticle;
    @BindView(R.id.good_brand_text)       TextView mBrand;
    @BindView(R.id.good_rest_wh_text)     TextView mRestWH;
    @BindView(R.id.good_rest_cwh_text)    TextView mRestCWH;
    @BindView(R.id.good_rest_official_text)TextView mRestOF;
    @BindView(R.id.good_base_price_text)  TextView mBasePrice;
    @BindView(R.id.good_min_price_text)   TextView mMinPrice;
    @BindView(R.id.good_name_text)        TextView mName;



    @Inject
    GoodsScreen.Presenter mPresenter;

    public ItemViewHolder(View itemView) {
        super(itemView);
        DaggerService.<GoodsScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);
    }


    @Override
    public void setCurrentItem(ItemRealm currentItem) {
        super.setCurrentItem(currentItem);

        mArticle.setText(currentItem.getArtNumber());
        mBrand.setText(currentItem.getBrand().getName());
        mRestWH.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int),currentItem.getRestStore()));
        mRestCWH.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int),currentItem.getRestDistr()));
        mRestOF.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int),currentItem.getRestOfficial()));
        mBasePrice.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format),currentItem.getBasePrice()));
        mMinPrice.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format),currentItem.getLowPrice()));
        mName.setText(currentItem.getName());

    }

    @OnClick(R.id.good_item_wrapper)
    void onClick(View view){
        mPresenter.itemSelected(currentItem);
    }

}
