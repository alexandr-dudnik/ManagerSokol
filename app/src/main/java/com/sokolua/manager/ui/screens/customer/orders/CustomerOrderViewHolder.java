package com.sokolua.manager.ui.screens.customer.orders;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class CustomerOrderViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<OrderRealm> {

    @Nullable    @BindView(R.id.order_status_img)    ImageView mOrderStatusImage;
    @Nullable    @BindView(R.id.order_date_text)     TextView mOrderDate;
    @Nullable    @BindView(R.id.order_type_text)     TextView mOrderType;
    @Nullable    @BindView(R.id.order_amount_text)   TextView mOrderAmountText;
    @Nullable    @BindView(R.id.order_comment_text)  TextView mOrderCommentText;
    @Nullable    @BindView(R.id.order_delivery_text) TextView mDeliveryDateText;
    @Nullable    @BindView(R.id.order_currency_text) TextView mCurrencyText;
    @Nullable    @BindView(R.id.empty_list_text)     TextView mEmptyText;



    @BindDrawable(R.drawable.ic_cart)   Drawable cartDrawable;
    @BindDrawable(R.drawable.ic_sync)   Drawable progressDrawable;
    @BindDrawable(R.drawable.ic_done)   Drawable deliveredDrawable;
    @BindDrawable(R.drawable.ic_backup) Drawable sentDrawable;

    @Inject
    CustomerOrdersScreen.Presenter mPresenter;

    public CustomerOrderViewHolder(View itemView) {
        super(itemView);
        DaggerService.<CustomerOrdersScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);

        if (mEmptyText != null){
            mEmptyText.setText(App.getStringRes(R.string.order_no_orders));
        }
    }


    @Override
    public void setCurrentItem(OrderRealm currentItem) {
        super.setCurrentItem(currentItem);
        if (currentItem.isValid()) {
            if (mOrderStatusImage != null) {
                mOrderStatusImage.setVisibility(View.VISIBLE);
                switch (currentItem.getStatus()) {
                    case ConstantManager.ORDER_STATUS_CART:
                        mOrderStatusImage.setImageDrawable(cartDrawable);
                        mOrderStatusImage.setColorFilter(App.getColorRes(R.color.color_order_cart));
                        break;
                    case ConstantManager.ORDER_STATUS_DELIVERED:
                        mOrderStatusImage.setImageDrawable(deliveredDrawable);
                        mOrderStatusImage.setColorFilter(App.getColorRes(R.color.color_order_done));
                        break;
                    case ConstantManager.ORDER_STATUS_IN_PROGRESS:
                        mOrderStatusImage.setImageDrawable(progressDrawable);
                        mOrderStatusImage.setColorFilter(App.getColorRes(R.color.color_order_in_progress));
                        break;
                    case ConstantManager.ORDER_STATUS_SENT:
                        mOrderStatusImage.setImageDrawable(sentDrawable);
                        mOrderStatusImage.setColorFilter(App.getColorRes(R.color.color_order_sent));
                        break;
                    default:
                        mOrderStatusImage.setVisibility(View.INVISIBLE);
                }
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(App.getStringRes(R.string.date_format), Locale.getDefault());
            if (mOrderDate != null) {
                mOrderDate.setText(dateFormat.format(currentItem.getDate()));
            }

            if (mDeliveryDateText != null) {
                String tmpDelivery = App.getStringRes(R.string.delivery_date_prefix) + dateFormat.format(currentItem.getDelivery());
                mDeliveryDateText.setText(tmpDelivery);
            }

            if (mOrderType != null) {
                switch (currentItem.getPayment()) {
                    case ConstantManager.ORDER_PAYMENT_CASH:
                        mOrderType.setText(App.getStringRes(R.string.payment_type_cash));
                        break;
                    case ConstantManager.ORDER_PAYMENT_OFFICIAL:
                        mOrderType.setText(App.getStringRes(R.string.payment_type_official));
                        break;
                }
            }

            if (mOrderAmountText != null) {
                mOrderAmountText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), currentItem.getTotal()));
            }
            if (mCurrencyText != null) {
                mCurrencyText.setText(currentItem.getCurrency().getName());
            }
            if (mOrderCommentText != null) {
                mOrderCommentText.setText(currentItem.getComments());
            }
        }

    }


    @Optional
    @OnClick(R.id.order_placeholder)
    void onClick(View view){
        mPresenter.openOrder(currentItem.getId());
    }

}
