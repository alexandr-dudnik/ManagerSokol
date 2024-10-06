package com.sokolua.manager.ui.screens.goods;

import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.databinding.GoodsItemsItemBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.util.Locale;

import javax.inject.Inject;

public class ItemViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<ItemRealm> {
    private GoodsItemsItemBinding binding;

    @Inject
    GoodsScreen.Presenter mPresenter;
    @Inject
    String cartId;

    ItemViewHolder(View itemView) {
        super(itemView);
        DaggerService.<GoodsScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        binding = GoodsItemsItemBinding.bind(itemView);
    }

    @Override
    public void setCurrentItem(ItemRealm currentItem) {
        super.setCurrentItem(currentItem);
        updateFields(currentItem);
    }

    private void updateFields(ItemRealm currentItem) {
        if (currentItem.isLoaded() && currentItem.isValid()) {
            binding.goodArticleText.setText(currentItem.getArtNumber());
            binding.goodBrandText.setText(currentItem.getBrand() != null ? currentItem.getBrand().getName() : "");
            binding.goodRestWhText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int), currentItem.getRestStore()));
            binding.goodRestCwhText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int), currentItem.getRestDistr()));
            if (currentItem.getRestOfficial() > 999999) {
                binding.goodRestOfficialText.setText("∞");
            } else {
                binding.goodRestOfficialText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int), currentItem.getRestOfficial()));
            }
            binding.goodBasePriceText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), mPresenter.getItemPrice(currentItem.getItemId())));
            binding.goodMinPriceText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), mPresenter.getLowPrice(currentItem.getItemId())));
            binding.goodNameText.setText(currentItem.getName());
            itemView.setOnClickListener(view -> mPresenter.itemSelected(currentItem.getItemId()));
        }
    }

}
