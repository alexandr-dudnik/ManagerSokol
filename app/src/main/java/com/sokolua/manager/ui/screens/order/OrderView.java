package com.sokolua.manager.ui.screens.order;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.SwipeToDeleteCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;

public class OrderView extends AbstractView<OrderScreen.Presenter> {
    @BindView(R.id.order_status_image)      ImageView mStatusImage;
    @BindView(R.id.order_date_text)         TextView mOrderDate;
    @BindView(R.id.order_title_text)        TextView mOrderTitle;
    @BindView(R.id.order_currency_text)     TextView mCurrency;
    @BindView(R.id.order_type_spin)         Spinner mOrderType;
    @BindView(R.id.order_type_text)         TextView mOrderTypeText;
    @BindView(R.id.order_delivery_text)     TextView mDeliveryDate;
    @BindView(R.id.order_amount_text)       TextView mOrderAmount;
    @BindView(R.id.order_comment_edit)      EditText mComment;
    @BindView(R.id.order_comment_text)      TextView mCommentText;

    @BindView(R.id.order_items_list)        RecyclerView mItems;
    @BindView(R.id.order_items_list_footer) LinearLayout mFooter;


    @BindDrawable(R.drawable.ic_cart)       Drawable cartDrawable;
    @BindDrawable(R.drawable.ic_sync)       Drawable progressDrawable;
    @BindDrawable(R.drawable.ic_done)       Drawable deliveredDrawable;
    @BindDrawable(R.drawable.ic_backup)     Drawable sentDrawable;

    private int mStatus;
    private ItemTouchHelper itemTouchHelper;

    private Map<String, Integer> orderTypes = new HashMap<>();

    private SimpleDateFormat dateFormat;

    public OrderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<OrderScreen.Component>getDaggerComponent(context).inject(this);
        }


    }

    @Override
    public boolean viewOnBackPressed() {
        return false ;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        orderTypes.put(App.getStringRes(R.string.payment_type_cash), ConstantManager.ORDER_PAYMENT_CASH);
        orderTypes.put(App.getStringRes(R.string.payment_type_official), ConstantManager.ORDER_PAYMENT_OFFICIAL);
        mOrderType.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.simple_item, orderTypes.keySet().toArray()));
        mOrderType.setSelection(0);

        dateFormat = new SimpleDateFormat(App.getStringRes(R.string.date_format), Locale.getDefault());

        mPresenter.updateFields();

        if (mStatus == ConstantManager.ORDER_STATUS_CART) {
            itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(getContext()) {
                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    mPresenter.removeLine(((OrderLineViewHolder) viewHolder).getCurrentItem());
                }
            });
        }

    }


    //region ===================== Setters =========================

    public void setLinesAdapter(ReactiveRecyclerAdapter mAdapter) {
        mItems.setHasFixedSize(true);
        mItems.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        mItems.setAdapter(mAdapter);
    }


    public void setStatus(int status) {
        mStatus = status;
        mOrderTitle.setText(App.getStringRes(R.string.order_title));
        mStatusImage.setVisibility(View.VISIBLE);
        mOrderType.setVisibility(GONE);
        mOrderTypeText.setVisibility(VISIBLE);
        mComment.setVisibility(GONE);
        mCommentText.setVisibility(VISIBLE);
        mFooter.setVisibility(GONE);
        switch (status){
            case ConstantManager.ORDER_STATUS_CART:
                mStatusImage.setImageDrawable(cartDrawable);
                mStatusImage.setColorFilter(App.getColorRes(R.color.color_order_cart));
                mOrderTitle.setText(App.getStringRes(R.string.cart_title));
                mOrderType.setVisibility(VISIBLE);
                mOrderTypeText.setVisibility(GONE);
                mComment.setVisibility(VISIBLE);
                mCommentText.setVisibility(GONE);
                mFooter.setVisibility(VISIBLE);
                if (itemTouchHelper != null) {
                    itemTouchHelper.attachToRecyclerView(mItems);
                }
                break;
            case ConstantManager.ORDER_STATUS_DELIVERED:
                mStatusImage.setImageDrawable(deliveredDrawable);
                mStatusImage.setColorFilter(App.getColorRes(R.color.color_order_done));
                if (itemTouchHelper != null) {
                    itemTouchHelper.attachToRecyclerView(null);
                }
                break;
            case ConstantManager.ORDER_STATUS_IN_PROGRESS:
                mStatusImage.setImageDrawable(progressDrawable);
                mStatusImage.setColorFilter(App.getColorRes(R.color.color_order_in_progress));
                if (itemTouchHelper != null) {
                    itemTouchHelper.attachToRecyclerView(null);
                }
                break;
            case ConstantManager.ORDER_STATUS_SENT:
                mStatusImage.setImageDrawable(sentDrawable);
                mStatusImage.setColorFilter(App.getColorRes(R.color.color_order_sent));
                if (itemTouchHelper != null) {
                    itemTouchHelper.attachToRecyclerView(null);
                }
                break;
            default:
                mStatusImage.setVisibility(View.INVISIBLE);
        }
    }

    public void setOrderDate(Date orderDate) {
        this.mOrderDate.setText(dateFormat.format(orderDate));
    }

    public void setCurrency(String currency) {
        this.mCurrency.setText(currency);
    }

    public void setOrderType(int orderType) {
        int index=0;
        for (String key : orderTypes.keySet()) {
            if (orderTypes.get(key).equals(orderType)) {
                mOrderTypeText.setText(key);
                break;
            }
            index++;
        }
        mOrderType.setSelection(index);
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.mDeliveryDate.setText(dateFormat.format(deliveryDate));
    }

    public void setOrderAmount(Float orderAmount) {
        this.mOrderAmount.setText(String.format(Locale.getDefault(),App.getStringRes(R.string.numeric_format),orderAmount));
    }

    public void setComment(String comment) {
        this.mComment.setText(comment);
        this.mCommentText.setText(comment);
    }

    //endregion ================== Setters =========================

    //region ===================== Events =========================
    @OnClick(R.id.order_delivery_text)
    void selectDeliveryDate(){
        if (mStatus == ConstantManager.ORDER_STATUS_CART){
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog picker = new DatePickerDialog(getRootView().getContext(), (datePicker, yy, mm, dd) -> {
                c.clear();
                c.set(yy, mm, dd);
                mPresenter.updateDeliveryDate(c.getTime());
            }, year, month, day);
            picker.show();
        }
    }

    @OnItemSelected(R.id.order_type_spin)
    void paymentChange(View view){
        if (mStatus == ConstantManager.ORDER_STATUS_CART) {
            mPresenter.updatePayment(orderTypes.get(mOrderType.getSelectedItem().toString()));
        }
    }

    @OnFocusChange(R.id.order_comment_edit)
    void commentFocus(View view, boolean focus){
        if (mStatus == ConstantManager.ORDER_STATUS_CART && !focus) {
            mPresenter.updateComment(mComment.getText().toString());
        }
    }

    @OnClick(R.id.order_items_add_image)
    void addItemsClick(View view){
        mPresenter.addNewItemToOrder();
    }
    //endregion ================== Events =========================
}
