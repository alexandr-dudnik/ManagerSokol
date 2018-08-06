package com.sokolua.manager.ui.screens.customer.orders;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.ReactiveRecyclerAdapter;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerOrderViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<OrderRealm> {

    @BindView(R.id.order_status_img)
    ImageView mOrderStatusImage;
    @BindView(R.id.order_date_text)
    TextView mOrderDate;
    @BindView(R.id.order_type_text)
    TextView mOrderType;
    @BindView(R.id.order_amount_text)
    TextView mOrderAmountText;
    @BindView(R.id.order_comment_text)
    TextView mOrderCommentText;


    @BindDrawable(R.drawable.ic_cart)
    Drawable cartDrawable;
    @BindDrawable(R.drawable.ic_sync)
    Drawable progressDrawable;
    @BindDrawable(R.drawable.ic_done)
    Drawable deliveredDrawable;
    @BindDrawable(R.drawable.ic_backup)
    Drawable sentDrawable;

    @Inject
    CustomerOrdersScreen.Presenter mPresenter;

    public CustomerOrderViewHolder(View itemView) {
        super(itemView);
        DaggerService.<CustomerOrdersScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);
    }


    @Override
    public void setCurrentItem(OrderRealm currentItem) {
        super.setCurrentItem(currentItem);

        mOrderStatusImage.setVisibility(View.VISIBLE);
        switch (currentItem.getStatus()){
            case ConstantManager.ORDER_STATUS_CART:
                mOrderStatusImage.setImageDrawable(cartDrawable);
                mOrderStatusImage.setColorFilter(App.getColorRes(R.color.color_gray));
                break;
            case ConstantManager.ORDER_STATUS_DELIVERED:
                mOrderStatusImage.setImageDrawable(deliveredDrawable);
                mOrderStatusImage.setColorFilter(App.getColorRes(R.color.color_green));
                break;
            case ConstantManager.ORDER_STATUS_IN_PROGRESS:
                mOrderStatusImage.setImageDrawable(progressDrawable);
                mOrderStatusImage.setColorFilter(App.getColorRes(R.color.color_green));
                break;
            case ConstantManager.ORDER_STATUS_SENT:
                mOrderStatusImage.setImageDrawable(sentDrawable);
                mOrderStatusImage.setColorFilter(App.getColorRes(R.color.color_gray));
                break;
            default:
                mOrderStatusImage.setVisibility(View.INVISIBLE);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        mOrderDate.setText(dateFormat.format(currentItem.getDate()));
        switch (currentItem.getPayment()){
            case ConstantManager.ORDER_PAYMENT_CASH:
                mOrderType.setText(App.getStringRes(R.string.payment_type_cash));
                break;
            case ConstantManager.ORDER_PAYMENT_OFFICIAL:
                mOrderType.setText(App.getStringRes(R.string.payment_type_oficial));
                break;
        }

        mOrderAmountText.setText(String.format(Locale.getDefault(),"%1$.2f "+App.getStringRes(R.string.national_currency),currentItem.getTotal()));
        mOrderCommentText.setText(currentItem.getComments());


    }



}
