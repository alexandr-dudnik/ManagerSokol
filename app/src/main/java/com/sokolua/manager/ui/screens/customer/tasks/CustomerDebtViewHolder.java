package com.sokolua.manager.ui.screens.customer.tasks;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerDebtViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<CustomerDebtItem> {

    @Nullable @BindView(R.id.debt_type_icon)      ImageView mDebtTypeIcon;
    @Nullable @BindView(R.id.debt_type_text)      TextView mDebtTypeText;
    @Nullable @BindView(R.id.debt_value_currency) TextView mDebtCurrency;
    @Nullable @BindView(R.id.debt_value_sum)      TextView mDebtValueSum;
    @Nullable @BindView(R.id.empty_list_text)     TextView mEmptyText;

    @Inject
    CustomerTasksScreen.Presenter mPresenter;

    public CustomerDebtViewHolder(View itemView) {
        super(itemView);
        DaggerService.<CustomerTasksScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);

        if (mEmptyText != null){
            mEmptyText.setText(App.getStringRes(R.string.customer_debt_no_debt));
        }
    }


    @Override
    public void setCurrentItem(CustomerDebtItem currentItem) {
        super.setCurrentItem(currentItem);

        if (currentItem.isHeader()){
            if (mDebtTypeIcon != null){
                switch (currentItem.getDebtType()) {
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
                mDebtTypeText.setText(currentItem.getHeaderText());
            }
        }else{
            if (mDebtValueSum !=null && mDebtCurrency != null && currentItem.getDebt() != null && currentItem.getDebt().isValid()) {
                mDebtCurrency.setText(currentItem.getDebt().getCurrency());
                mDebtValueSum.setText(String.format(Locale.getDefault(),App.getStringRes(R.string.numeric_format),currentItem.getDebt().getAmount()));
            }
        }
    }



}
