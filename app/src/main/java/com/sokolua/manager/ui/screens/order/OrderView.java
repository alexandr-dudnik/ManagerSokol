package com.sokolua.manager.ui.screens.order;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.databinding.ScreenOrderBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.OnSpinItemSelectedListener;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.SwipeToDeleteCallback;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kotlin.Unit;

@SuppressLint("NonConstantResourceId")
public class OrderView extends AbstractView<OrderScreen.Presenter, ScreenOrderBinding> {
    private ItemTouchHelper itemTouchHelper;

    private final Map<String, Integer> orderTypes = new HashMap<>();
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
    protected ScreenOrderBinding bindView(View view) {
        return ScreenOrderBinding.bind(view);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<OrderScreen.Component>getDaggerComponent(context).inject(this);
        }
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            orderTypes.put(App.getStringRes(R.string.payment_type_cash), ConstantManager.ORDER_PAYMENT_CASH);
            orderTypes.put(App.getStringRes(R.string.payment_type_official), ConstantManager.ORDER_PAYMENT_OFFICIAL);
            orderTypes.put(App.getStringRes(R.string.payment_type_fop), ConstantManager.ORDER_PAYMENT_FOP);
            binding.orderTypeSpin.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.simple_item, orderTypes.keySet().toArray()));
            binding.orderTypeSpin.setSelection(0);

            dateFormat = new SimpleDateFormat(App.getStringRes(R.string.date_format), Locale.getDefault());

            mPresenter.updateAllFields();

            if (mStatus == ConstantManager.ORDER_STATUS_CART) {
                itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(getContext()) {
                    @Override
                    public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                        mPresenter.removeLine(((OrderLineViewHolder) viewHolder).getCurrentItem());
                    }
                });
                itemTouchHelper.attachToRecyclerView(binding.orderItemsList);
            }

            binding.orderCommentEdit.setOnEditorActionListener((textView, i, keyEvent) -> {
                mPresenter.updateComment(binding.orderCommentEdit.getText().toString());
                return true;
            });

            binding.orderDeliveryText.setOnClickListener(view -> {
                if (mStatus == ConstantManager.ORDER_STATUS_CART) {
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
            });

            binding.orderTypeSpin.setOnItemSelectedListener(
                    new OnSpinItemSelectedListener(
                            () -> {
                                final Object selectedItem = binding.orderTypeSpin.getSelectedItem();
                                if (mStatus == ConstantManager.ORDER_STATUS_CART && selectedItem != null && !binding.orderTypeText.getText().toString().equals(selectedItem.toString())) {
                                    paymentType = orderTypes.get(selectedItem.toString());
                                    binding.orderTypeText.setText(selectedItem.toString());
                                    mPresenter.updatePayment(paymentType);
                                    updateVisibility();
                                }
                                return Unit.INSTANCE;
                            }
                    )
            );

            binding.orderCurrencySpin.setOnItemSelectedListener(
                    new OnSpinItemSelectedListener(
                            () -> {
                                final Object selectedItem = binding.orderCurrencySpin.getSelectedItem();
                                if (mStatus == ConstantManager.ORDER_STATUS_CART && selectedItem != null && !binding.orderCurrencyText.getText().toString().equals(selectedItem.toString())) {
                                    binding.orderCurrencyText.setText(selectedItem.toString());
                                    mPresenter.updateCurrency(selectedItem.toString());
                                }
                                return Unit.INSTANCE;
                            }
                    )
            );

            binding.orderTradeSpin.setOnItemSelectedListener(
                    new OnSpinItemSelectedListener(
                            () -> {
                                final Object selectedItem = binding.orderTradeSpin.getSelectedItem();
                                if (mStatus == ConstantManager.ORDER_STATUS_CART && selectedItem != null && !binding.orderTradeText.getText().toString().equals(selectedItem.toString())) {
                                    binding.orderTradeText.setText(selectedItem.toString());
                                    mPresenter.updateTrade(selectedItem.toString());
                                }
                                return Unit.INSTANCE;
                            }
                    )
            );

            binding.orderFactChb.setOnClickListener(view -> {
                if (mStatus == ConstantManager.ORDER_STATUS_CART && binding.orderFactChb.isChecked() != payOnFact) {
                    payOnFact = binding.orderFactChb.isChecked();
                    mPresenter.updateFactFlag(binding.orderFactChb.isChecked());
                    updateVisibility();
                }
            });

            binding.orderCommentEdit.setOnClickListener(view -> {
                if (mStatus == ConstantManager.ORDER_STATUS_CART) {
                    mPresenter.openCommentDialog();
                }
            });

            binding.orderCommentText.setOnClickListener(view -> mPresenter.openCommentDialog());
            binding.orderItemsAddImage.setOnClickListener(view -> mPresenter.addNewItemToOrder());
        }
    }

    private void updateVisibility() {
        boolean isCart = (mStatus == ConstantManager.ORDER_STATUS_CART);

        binding.goodRequestPriceColumn.setVisibility(isCart ? VISIBLE : GONE);
        binding.orderTypeSpin.setVisibility(isCart ? VISIBLE : GONE);
        binding.orderTypeText.setVisibility(!isCart ? VISIBLE : GONE);
        binding.orderCommentEdit.setVisibility(isCart ? VISIBLE : GONE);
        binding.orderCommentText.setVisibility(!isCart ? VISIBLE : GONE);
        binding.orderItemsListFooter.setVisibility(isCart ? VISIBLE : GONE);
        binding.orderFactChb.setEnabled(isCart);

        binding.orderTradeText.setVisibility(!isCart || binding.orderFactChb.isChecked() ? VISIBLE : GONE);
        binding.orderTradeSpin.setVisibility(isCart && !binding.orderFactChb.isChecked() ? VISIBLE : GONE);
        binding.orderCurrencyText.setVisibility(!isCart || !(paymentType == ConstantManager.ORDER_PAYMENT_CASH) ? VISIBLE : GONE);
        binding.orderCurrencySpin.setVisibility(isCart && paymentType == ConstantManager.ORDER_PAYMENT_CASH ? VISIBLE : GONE);
    }

    //region ===================== Setters =========================

    public void setLinesAdapter(ReactiveRecyclerAdapter mAdapter) {
        binding.orderItemsList.setHasFixedSize(true);
        binding.orderItemsList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.orderItemsList.setAdapter(mAdapter);
    }

    public void setCurrencyList(ArrayList<String> mList) {
        if (mStatus == ConstantManager.ORDER_STATUS_CART) {
            final Object selectedItem = binding.orderCurrencySpin.getSelectedItem();
            String curItem = selectedItem == null ? "" : selectedItem.toString();
            binding.orderCurrencyText.setText(curItem);
            currencies = mList;
            binding.orderCurrencySpin.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.simple_item, mList));
            int idx = Math.max(currencies.indexOf(curItem), 0);
            binding.orderCurrencySpin.setSelection(idx);
        }
    }

    public void setTradeList(ArrayList<String> mList) {
        if (mStatus == ConstantManager.ORDER_STATUS_CART) {
            final Object selectedItem = binding.orderTradeSpin.getSelectedItem();
            String curItem = selectedItem == null ? "" : selectedItem.toString();
            binding.orderTradeText.setText(curItem);
            trades = mList;
            binding.orderTradeSpin.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.simple_item, mList));
            int idx = Math.max(trades.indexOf(curItem), 0);
            binding.orderTradeSpin.setSelection(idx);
        }
    }

    public void setStatus(int status) {
        mStatus = status;
        binding.orderTitleText.setText(App.getStringRes(R.string.order_title));
        updateVisibility();

        if (itemTouchHelper != null) {
            itemTouchHelper.attachToRecyclerView(status == ConstantManager.ORDER_STATUS_CART ? binding.orderItemsList : null);
        }
        switch (status) {
            case ConstantManager.ORDER_STATUS_CART:
                binding.orderStatusImage.setImageResource(R.drawable.ic_cart);
                binding.orderStatusImage.setColorFilter(App.getColorRes(R.color.color_order_cart));
                binding.orderTitleText.setText(App.getStringRes(R.string.cart_title));
                break;
            case ConstantManager.ORDER_STATUS_DELIVERED:
                binding.orderStatusImage.setImageResource(R.drawable.ic_done);
                binding.orderStatusImage.setColorFilter(App.getColorRes(R.color.color_order_done));
                break;
            case ConstantManager.ORDER_STATUS_IN_PROGRESS:
                binding.orderStatusImage.setImageResource(R.drawable.ic_sync);
                binding.orderStatusImage.setColorFilter(App.getColorRes(R.color.color_order_in_progress));
                break;
            case ConstantManager.ORDER_STATUS_SENT:
                binding.orderStatusImage.setImageResource(R.drawable.ic_backup);
                binding.orderStatusImage.setColorFilter(App.getColorRes(R.color.color_order_sent));
                break;
            default:
                binding.orderStatusImage.setVisibility(View.INVISIBLE);
        }
    }

    public void setOrderDate(Date orderDate) {
        binding.orderDateText.setText(dateFormat.format(orderDate));
    }

    public void setCurrency(String currency) {
        binding.orderCurrencyText.setText(currency);
        if (mStatus == ConstantManager.ORDER_STATUS_CART) {
            int idx = currencies.indexOf(currency);

            if (idx == -1) {
                currencies.add(currency);
                idx = currencies.size() - 1;
                setCurrencyList(currencies);
            }
            binding.orderCurrencySpin.setSelection(idx);
        }
    }

    public void setTrade(String trade) {
        binding.orderTradeText.setText(trade);
        if (mStatus == ConstantManager.ORDER_STATUS_CART) {
            int idx = trades.indexOf(trade);
            if (idx == -1) {
                trades.add(trade);
                idx = trades.size() - 1;
                setTradeList(trades);
            }
            binding.orderTradeSpin.setSelection(idx);
        }
    }

    public void setOrderType(int orderType) {
        paymentType = orderType;
        int index = 0;
        for (String key : orderTypes.keySet()) {
            //noinspection ConstantConditions
            if (orderTypes.get(key) == orderType) {
                binding.orderTypeText.setText(key);
                break;
            }
            index++;
        }
        if (mStatus == ConstantManager.ORDER_STATUS_CART) {
            binding.orderTypeSpin.setSelection(index);
        }
        updateVisibility();
    }

    public void setDeliveryDate(Date deliveryDate) {
        binding.orderDeliveryText.setText(dateFormat.format(deliveryDate));
    }

    public void setOrderAmount(Float orderAmount) {
        binding.orderAmountText.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), orderAmount));
    }

    public void setComment(String comment) {
        binding.orderCommentEdit.setText(comment);
        binding.orderCommentText.setText(comment);
    }

    public void setFact(boolean payByFact) {
        payOnFact = payByFact;
        binding.orderFactChb.setChecked(payByFact);
        updateVisibility();
    }

    public void setPriceList(String price) {
        binding.orderPriceText.setText(price);
    }

    //endregion ================== Setters =========================

}
