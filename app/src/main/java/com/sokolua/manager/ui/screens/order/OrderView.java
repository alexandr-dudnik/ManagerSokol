package com.sokolua.manager.ui.screens.order;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.SwipeToDeleteCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    @BindView(R.id.order_type_spin)         Spinner mOrderType;
    @BindView(R.id.order_trade_spin)        Spinner mOrderTrade;
    @BindView(R.id.order_currency_spin)     Spinner mOrderCurrency;
    @BindView(R.id.order_type_text)         TextView mOrderTypeText;
    @BindView(R.id.order_trade_text)        TextView mOrderTradeText;
    @BindView(R.id.order_currency_text)     TextView mOrderCurrencyText;
    @BindView(R.id.order_delivery_text)     TextView mDeliveryDate;
    @BindView(R.id.order_amount_text)       TextView mOrderAmount;
    @BindView(R.id.order_comment_edit)      EditText mComment;
    @BindView(R.id.order_comment_text)      TextView mCommentText;
    @BindView(R.id.order_price_text)        TextView mPriceText;
    @BindView(R.id.order_fact_chb)          CheckBox mOrderFact;

    @BindView(R.id.order_items_list)        RecyclerView mItems;
    @BindView(R.id.order_items_list_footer) LinearLayout mFooter;


    @BindDrawable(R.drawable.ic_cart)       Drawable cartDrawable;
    @BindDrawable(R.drawable.ic_sync)       Drawable progressDrawable;
    @BindDrawable(R.drawable.ic_done)       Drawable deliveredDrawable;
    @BindDrawable(R.drawable.ic_backup)     Drawable sentDrawable;

    private ItemTouchHelper itemTouchHelper;

    private Map<String, Integer> orderTypes = new HashMap<>();
    private ArrayList<String> currencies = new ArrayList<>();
    private ArrayList<String> trades = new ArrayList<>();

    private SimpleDateFormat dateFormat;

    private int mStatus;
    private int paymentType;
    private boolean payOnFact;


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
        if (!isInEditMode()) {
            orderTypes.put(App.getStringRes(R.string.payment_type_cash), ConstantManager.ORDER_PAYMENT_CASH);
            orderTypes.put(App.getStringRes(R.string.payment_type_official), ConstantManager.ORDER_PAYMENT_OFFICIAL);
            mOrderType.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.simple_item, orderTypes.keySet().toArray()));
            mOrderType.setSelection(0);


            dateFormat = new SimpleDateFormat(App.getStringRes(R.string.date_format), Locale.getDefault());

            mPresenter.updateAllFields();

            if (mStatus == ConstantManager.ORDER_STATUS_CART) {
                itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(getContext()) {
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        mPresenter.removeLine(((OrderLineViewHolder) viewHolder).getCurrentItem());
                    }

                });
                itemTouchHelper.attachToRecyclerView(mItems);
            }

            mComment.setOnEditorActionListener((textView, i, keyEvent) -> {
                mPresenter.updateComment(mComment.getText().toString());
                return true;
            });
        }
    }


    private void updateVisibility(){
        boolean isCart = (mStatus == ConstantManager.ORDER_STATUS_CART);

        mOrderType.setVisibility(isCart?VISIBLE:GONE);
        mOrderTypeText.setVisibility(!isCart?VISIBLE:GONE);
        mComment.setVisibility(isCart?VISIBLE:GONE);
        mCommentText.setVisibility(!isCart?VISIBLE:GONE);
        mFooter.setVisibility(isCart?VISIBLE:GONE);
        mOrderFact.setEnabled(isCart);

        mOrderTradeText.setVisibility(!isCart || mOrderFact.isChecked()?VISIBLE:GONE);
        mOrderTrade.setVisibility(isCart && !mOrderFact.isChecked()?VISIBLE:GONE);
        mOrderCurrencyText.setVisibility(!isCart || !(paymentType == ConstantManager.ORDER_PAYMENT_CASH)?VISIBLE:GONE);
        mOrderCurrency.setVisibility(isCart && paymentType == ConstantManager.ORDER_PAYMENT_CASH?VISIBLE:GONE);
    }

    //region ===================== Setters =========================

    public void setLinesAdapter(ReactiveRecyclerAdapter mAdapter) {
        mItems.setHasFixedSize(true);
        mItems.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        mItems.setAdapter(mAdapter);
    }

    public void setCurrencyList(ArrayList<String> mList) {
        if (mStatus == ConstantManager.ORDER_STATUS_CART) {
            final Object selectedItem = mOrderCurrency.getSelectedItem();
            String curItem = selectedItem == null ? "" : selectedItem.toString();
            mOrderCurrencyText.setText(curItem);
            currencies = mList;
            mOrderCurrency.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.simple_item, mList));
            int idx = Math.max(currencies.indexOf(curItem), 0);
            mOrderCurrency.setSelection(idx);
        }
    }

    public void setTradeList(ArrayList<String> mList) {
        if (mStatus == ConstantManager.ORDER_STATUS_CART){
            final Object selectedItem = mOrderTrade.getSelectedItem();
            String curItem = selectedItem==null?"":selectedItem.toString();
            mOrderTradeText.setText(curItem);
            trades = mList;
            mOrderTrade.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.simple_item, mList));
            int idx = Math.max(trades.indexOf(curItem),0);
            mOrderTrade.setSelection(idx);
        }
    }


    public void setStatus(int status) {
        mStatus = status;
        mOrderTitle.setText(App.getStringRes(R.string.order_title));
        updateVisibility();

        if (itemTouchHelper != null){
            itemTouchHelper.attachToRecyclerView(status == ConstantManager.ORDER_STATUS_CART?mItems:null);
        }
        switch (status){
            case ConstantManager.ORDER_STATUS_CART:
                mStatusImage.setImageDrawable(cartDrawable);
                mStatusImage.setColorFilter(App.getColorRes(R.color.color_order_cart));
                mOrderTitle.setText(App.getStringRes(R.string.cart_title));
                break;
            case ConstantManager.ORDER_STATUS_DELIVERED:
                mStatusImage.setImageDrawable(deliveredDrawable);
                mStatusImage.setColorFilter(App.getColorRes(R.color.color_order_done));
                break;
            case ConstantManager.ORDER_STATUS_IN_PROGRESS:
                mStatusImage.setImageDrawable(progressDrawable);
                mStatusImage.setColorFilter(App.getColorRes(R.color.color_order_in_progress));
                break;
            case ConstantManager.ORDER_STATUS_SENT:
                mStatusImage.setImageDrawable(sentDrawable);
                mStatusImage.setColorFilter(App.getColorRes(R.color.color_order_sent));
                break;
            default:
                mStatusImage.setVisibility(View.INVISIBLE);
        }
    }

    public void setOrderDate(Date orderDate) {
        this.mOrderDate.setText(dateFormat.format(orderDate));
    }

    public void setCurrency(String currency) {
        mOrderCurrencyText.setText(currency);
        if (mStatus == ConstantManager.ORDER_STATUS_CART) {
            int idx = currencies.indexOf(currency);

            if (idx == -1) {
                currencies.add(currency);
                idx = currencies.size() - 1;
                setCurrencyList(currencies);
            }
            mOrderCurrency.setSelection(idx);
        }
    }

    public void setTrade(String trade) {
        mOrderTradeText.setText(trade);
        if (mStatus == ConstantManager.ORDER_STATUS_CART) {
            int idx = trades.indexOf(trade);
            if (idx == -1) {
                trades.add(trade);
                idx = trades.size() - 1;
                setTradeList(trades);
            }
            mOrderTrade.setSelection(idx);
        }
    }

    public void setOrderType(int orderType) {
        paymentType = orderType;
        int index=0;
        for (String key : orderTypes.keySet()) {
            if (orderTypes.get(key).equals(orderType)) {
                mOrderTypeText.setText(key);
                break;
            }
            index++;
        }
        if (mStatus == ConstantManager.ORDER_STATUS_CART) {
            mOrderType.setSelection(index);
        }
        updateVisibility();
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

    public void setFact(boolean payByFact){
        payOnFact = payByFact;
        mOrderFact.setChecked(payByFact);
        updateVisibility();

    }

    public void setPriceList(String price){
        mPriceText.setText(price);
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
                c.set(yy, mm, dd, 23, 59, 0);
                mPresenter.updateDeliveryDate(c.getTime());
            }, year, month, day);
            picker.show();
        }
    }

    @OnItemSelected(R.id.order_type_spin)
    void paymentChange(View view){
        final Object selectedItem = mOrderType.getSelectedItem();
        if (mStatus == ConstantManager.ORDER_STATUS_CART && selectedItem != null && !mOrderTypeText.getText().toString().equals(selectedItem.toString())) {
            //noinspection ConstantConditions
            paymentType = orderTypes.get(selectedItem.toString());
            mOrderTypeText.setText(selectedItem.toString());
            mPresenter.updatePayment(paymentType);
            updateVisibility();
        }
    }

    @OnItemSelected(R.id.order_currency_spin)
    void currencyChange(View view){
        final Object selectedItem = mOrderCurrency.getSelectedItem();
        if (mStatus == ConstantManager.ORDER_STATUS_CART && selectedItem!=null && !mOrderCurrencyText.getText().toString().equals(selectedItem.toString())) {
            mOrderCurrencyText.setText(selectedItem.toString());
            mPresenter.updateCurrency(selectedItem.toString());
        }
    }

    @OnItemSelected(R.id.order_trade_spin)
    void tradeChange(View view){
        final Object selectedItem = mOrderTrade.getSelectedItem();
        if (mStatus == ConstantManager.ORDER_STATUS_CART && selectedItem != null && !mOrderTradeText.getText().toString().equals(selectedItem.toString())) {
            mOrderTradeText.setText(selectedItem.toString());
            mPresenter.updateTrade(selectedItem.toString());
        }
    }

    @OnClick(R.id.order_fact_chb)
    void changeFact(CompoundButton view){
        if (mStatus == ConstantManager.ORDER_STATUS_CART && mOrderFact.isChecked() != payOnFact) {
            payOnFact = mOrderFact.isChecked();
            mPresenter.updateFactFlag(mOrderFact.isChecked());
            updateVisibility();
        }
    }

    @OnFocusChange(R.id.order_comment_edit)
    void commentFocus(View view, boolean focus){
        if (mStatus == ConstantManager.ORDER_STATUS_CART) {
            if (focus){
                mPresenter.openCommentDialog();
            }else {
                mPresenter.updateComment(mComment.getText().toString());
            }
        }
    }

    @OnClick(R.id.order_comment_edit)
    void commentClick(View view){
        mPresenter.openCommentDialog();
    }

    @OnClick(R.id.order_items_add_image)
    void addItemsClick(View view){
        mPresenter.addNewItemToOrder();
    }


    //endregion ================== Events =========================
}
