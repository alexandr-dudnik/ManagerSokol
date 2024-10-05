package com.sokolua.manager.ui.screens.order;

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
    private OrderLineItemBinding binding;

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
            binding.goodPriceText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), currentItem.getPrice()));
            binding.goodQuantityText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format_int), currentItem.getQuantity()));

            binding.goodPriceText.setOnClickListener(view -> mPresenter.updatePrice(currentItem));
            binding.goodQuantityText.setOnClickListener(view -> mPresenter.updateQuantity(currentItem));
        }
    }

}
