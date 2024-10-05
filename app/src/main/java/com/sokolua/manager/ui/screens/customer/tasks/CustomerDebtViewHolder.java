package com.sokolua.manager.ui.screens.customer.tasks;

import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.databinding.CustomerDebtHeaderBinding;
import com.sokolua.manager.databinding.CustomerDebtItemBinding;
import com.sokolua.manager.databinding.EmptyListItemBinding;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.util.Locale;

public class CustomerDebtViewHolder<B extends ViewBinding> extends ReactiveRecyclerAdapter.ReactiveViewHolder<CustomerDebtItem> {
    private final B binding;

    public CustomerDebtViewHolder(View itemView, B binding) {
        super(itemView);
        this.binding = binding;

        if (binding instanceof EmptyListItemBinding) {
            ((EmptyListItemBinding) binding).emptyListText.setText(App.getStringRes(R.string.customer_debt_no_debt));
        }
    }

    @Override
    public void setCurrentItem(CustomerDebtItem currentItem) {
        super.setCurrentItem(currentItem);

        if (currentItem.isHeader() && (binding instanceof CustomerDebtHeaderBinding)) {
            switch (currentItem.getDebtType()) {
                case ConstantManager.DEBT_TYPE_NO_DEBT:
                case ConstantManager.DEBT_TYPE_WHOLE:
                    ((CustomerDebtHeaderBinding) binding).debtTypeIcon.setVisibility(View.INVISIBLE);
                    break;
                case ConstantManager.DEBT_TYPE_NORMAL:
                    ((CustomerDebtHeaderBinding) binding).debtTypeIcon.setVisibility(View.VISIBLE);
                    ((CustomerDebtHeaderBinding) binding).debtTypeIcon.setColorFilter(App.getColorRes(R.color.color_orange));
                    break;
                case ConstantManager.DEBT_TYPE_OUTDATED:
                    ((CustomerDebtHeaderBinding) binding).debtTypeIcon.setVisibility(View.VISIBLE);
                    ((CustomerDebtHeaderBinding) binding).debtTypeIcon.setColorFilter(App.getColorRes(R.color.color_red));
                    break;
            }
            ((CustomerDebtHeaderBinding) binding).debtTypeText.setText(currentItem.getHeaderText());
        } else {
            if ((binding instanceof CustomerDebtItemBinding) && currentItem.getDebt() != null && currentItem.getDebt().isValid()) {
                ((CustomerDebtItemBinding) binding).debtValueCurrency.setText(currentItem.getDebt().getCurrency());
                ((CustomerDebtItemBinding) binding).debtValueSum.setText(String.format(Locale.getDefault(), App.getStringRes(R.string.numeric_format), currentItem.getDebt().getAmount()));
            }
        }
    }
}
