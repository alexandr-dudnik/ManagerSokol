package com.sokolua.manager.ui.screens.customer.tasks;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.screens.customer.info.CustomerInfoScreen;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.ReactiveRecyclerAdapter;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerDebtViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<CustomerDebtItem> {

    @Nullable @BindView(R.id.debt_type_icon)      ImageView mDebtTypeIcon;
    @Nullable @BindView(R.id.debt_type_text)      TextView mDebtTypeText;
    @Nullable @BindView(R.id.debt_value_currency) TextView mDebtCurrency;
    @Nullable @BindView(R.id.debt_value_sum)      TextView mDebtValueSum;

    @Inject
    CustomerTasksScreen.Presenter mPresenter;

    public CustomerDebtViewHolder(View itemView) {
        super(itemView);
        DaggerService.<CustomerTasksScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);
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
            if (mDebtCurrency != null && currentItem.getDebt() != null) {
                mDebtCurrency.setText(currentItem.getDebt().getCurrency());
                mDebtValueSum.setText(String.format(Locale.getDefault(),"%1$.2f",currentItem.getDebt().getAmount()));
            }
        }
    }



}
