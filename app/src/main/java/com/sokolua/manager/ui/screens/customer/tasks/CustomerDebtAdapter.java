package com.sokolua.manager.ui.screens.customer.tasks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.utils.App;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;

public class CustomerDebtAdapter extends RecyclerView.Adapter<CustomerDebtAdapter.ViewHolder> {
    private ArrayList<CustomerDebtItem> items = new ArrayList<>();
    @Inject
    CustomerTasksScreen.Presenter mPresenter;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DaggerService.<CustomerTasksScreen.Component>getDaggerComponent(parent.getContext()).inject(this);
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_debt_item, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_debt_header, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerDebtItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isHeader()?1:0;
    }

    public void addItem(String currency, Float value, int debtType) {
        items.add(new CustomerDebtItem(currency, value, debtType));
        notifyDataSetChanged();
    }

    public void addHeader(String title, int debtType) {
        items.add(new CustomerDebtItem(title, debtType));
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Nullable @BindView(R.id.debt_type_icon)      ImageView mDebtTypeIcon;
        @Nullable @BindView(R.id.debt_type_text)      TextView mDebtTypeText;
        @Nullable @BindView(R.id.debt_value_currency) TextView mDebtCurrency;
        @Nullable @BindView(R.id.debt_value_sum)      TextView mDebtValueSum;


        private CustomerDebtItem mItem = null;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        public void bind(CustomerDebtItem item) {
            this.mItem = item;
            refreshInfo();
        }

        public void refreshInfo() {
            if (mItem.isHeader()){
                if (mDebtTypeIcon != null){
                    switch (mItem.getDebtType()) {
                        case ConstantManager.DEBT_TYPE_NO_DEBT:
                        case ConstantManager.DEBT_TYPE_WHOLE:
                            mDebtTypeIcon.setVisibility(View.INVISIBLE);
                            break;
                        case ConstantManager.DEBT_TYPE_NORMAL:
                            mDebtTypeIcon.setVisibility(View.VISIBLE);
                            mDebtTypeIcon.setColorFilter(App.getColorRes(R.color.color_orange));
                            break;
                        case ConstantManager.DEBT_TYPE_OUTDATED:
                            mDebtTypeIcon.setVisibility(View.VISIBLE);
                            mDebtTypeIcon.setColorFilter(App.getColorRes(R.color.color_red));
                            break;
                    }
                }
                if (mDebtTypeText != null) {
                    mDebtTypeText.setText(mItem.getCurrency());
                }
            }else{
                if (mDebtCurrency != null) {
                    mDebtCurrency.setText(mItem.getCurrency());
                    mDebtValueSum.setText(String.format(Locale.getDefault(),"%1$.2f",mItem.getValue()));
                }
            }
        }

    }

}
