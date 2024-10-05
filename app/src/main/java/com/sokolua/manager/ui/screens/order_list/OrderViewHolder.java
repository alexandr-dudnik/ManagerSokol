package com.sokolua.manager.ui.screens.order_list;

import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.databinding.OrderListItemBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

public class OrderViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<OrderRealm> {
    private OrderListItemBinding binding;

    @Inject
    OrderListScreen.Presenter mPresenter;

    public OrderViewHolder(View itemView) {
        super(itemView);
        DaggerService.<OrderListScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        binding = OrderListItemBinding.bind(itemView);
    }

    @Override
    public void setCurrentItem(OrderRealm currentItem) {
        super.setCurrentItem(currentItem);
        if (currentItem.isValid()) {
            binding.customerNameText.setText(currentItem.getCustomer().getName());
            binding.orderStatusImg.setVisibility(View.VISIBLE);
            switch (currentItem.getStatus()) {
                case ConstantManager.ORDER_STATUS_CART:
                    binding.orderStatusImg.setImageResource(R.drawable.ic_cart);
                    binding.orderStatusImg.setColorFilter(App.getColorRes(R.color.color_order_cart));
                    break;
                case ConstantManager.ORDER_STATUS_DELIVERED:
                    binding.orderStatusImg.setImageResource(R.drawable.ic_done);
                    binding.orderStatusImg.setColorFilter(App.getColorRes(R.color.color_order_done));
                    break;
                case ConstantManager.ORDER_STATUS_IN_PROGRESS:
                    binding.orderStatusImg.setImageResource(R.drawable.ic_sync);
                    binding.orderStatusImg.setColorFilter(App.getColorRes(R.color.color_order_in_progress));
                    break;
                case ConstantManager.ORDER_STATUS_SENT:
                    binding.orderStatusImg.setImageResource(R.drawable.ic_backup);
                    binding.orderStatusImg.setColorFilter(App.getColorRes(R.color.color_order_sent));
                    break;
                default:
                    binding.orderStatusImg.setVisibility(View.INVISIBLE);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat(App.getStringRes(R.string.date_format), Locale.getDefault());
            binding.orderDateText.setText(dateFormat.format(currentItem.getDate()));

            String tmpDelivery = App.getStringRes(R.string.delivery_date_prefix) + dateFormat.format(currentItem.getDelivery());
            binding.orderDeliveryText.setText(tmpDelivery);

            switch (currentItem.getPayment()) {
                case ConstantManager.ORDER_PAYMENT_CASH:
                    binding.orderTypeText.setText(App.getStringRes(R.string.payment_type_cash));
                    break;
                case ConstantManager.ORDER_PAYMENT_OFFICIAL:
                    binding.orderTypeText.setText(App.getStringRes(R.string.payment_type_official));
                    break;
            }

            binding.orderAmountText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), currentItem.getTotal()));
            binding.orderCurrencyText.setText(currentItem.getCurrency() == null ? App.getStringRes(R.string.national_currency) : currentItem.getCurrency().getName());
            binding.orderCommentText.setText(currentItem.getComments());

            binding.orderPlaceholder.setOnClickListener(view -> mPresenter.openOrder(currentItem.getId()));
        }
    }

}
