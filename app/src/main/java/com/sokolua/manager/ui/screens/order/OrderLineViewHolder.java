package com.sokolua.manager.ui.screens.order;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.databinding.OrderLineItemBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.util.Locale;

import javax.inject.Inject;

public class OrderLineViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<OrderLineRealm> {
    private final OrderLineItemBinding binding;

    @Inject
    OrderScreen.Presenter mPresenter;

    public OrderLineViewHolder(View itemView) {
        super(itemView);
        DaggerService.<OrderScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        binding = OrderLineItemBinding.bind(itemView);
    }

    @Override
    public void setCurrentItem(OrderLineRealm currentItem) {
        super.setCurrentItem(currentItem);

        if (currentItem.isValid()) {
            binding.goodArticleText.setText(currentItem.getItem().getArtNumber());
            binding.goodNameText.setText(currentItem.getItem().getName());
            Float showPrice;
            if (mPresenter.isOrderEditable() && currentItem.getPriceRequest() > 0f) {
                binding.goodPriceText.setTextColor(itemView.getContext().getColor(R.color.color_red));
                binding.goodPriceText.setTypeface(Typeface.DEFAULT_BOLD);
                showPrice = currentItem.getPriceRequest();
            } else {
                binding.goodPriceText.setTextColor(itemView.getContext().getColor(R.color.color_gray));
                binding.goodPriceText.setTypeface(Typeface.DEFAULT);
                showPrice = currentItem.getPrice();
            }
            binding.goodPriceText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), showPrice));
            binding.goodQuantityText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int), currentItem.getQuantity()));
            if (mPresenter.isOrderEditable()) {
                binding.goodRequestPriceImage.setVisibility(View.VISIBLE);
                binding.goodRequestPriceImage.setOnClickListener(view -> mPresenter.requestPrice(currentItem));
                if (currentItem.getPriceRequest() > 0f) {
                    binding.goodRequestPriceImage.setImageTintList(ColorStateList.valueOf(itemView.getContext().getColor(R.color.color_green)));
                } else {
                    binding.goodRequestPriceImage.setImageTintList(ColorStateList.valueOf(itemView.getContext().getColor(R.color.color_gray_light)));
                }
            } else {
                binding.goodRequestPriceImage.setVisibility(View.GONE);
            }

            if (mPresenter.isOrderEditable()) {
                if (currentItem.getPriceRequest() < 0.01f) {
                    binding.goodPriceText.setOnClickListener(view -> mPresenter.updatePrice(currentItem));
                } else {
                    binding.goodPriceText.setOnClickListener(view -> mPresenter.requestPrice(currentItem));
                }
                binding.goodQuantityText.setOnClickListener(view -> mPresenter.updateQuantity(currentItem));
            }
        }
    }

}
