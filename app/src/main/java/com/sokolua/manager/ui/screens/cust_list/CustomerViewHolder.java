package com.sokolua.manager.ui.screens.cust_list;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.ReactiveRecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerViewHolder<T> extends ReactiveRecyclerAdapter.ReactiveViewHolder<T> {

    @Nullable @BindView(R.id.exclamation_img)   ImageView mExclamationImg;
    @Nullable @BindView(R.id.map_pin_img)       ImageView mMapPinImg;
    @Nullable @BindView(R.id.call_img)          ImageView mCallImg;
    @Nullable @BindView(R.id.customer_name_text)TextView mCustomerNameText;
    @Nullable @BindView(R.id.item_header_text)  TextView mItemHeaderText;

    public CustomerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setCurrentItem(T currentItem) {
        super.setCurrentItem(currentItem);

        if (currentItem != null) {
//            if (currentItem.isHeader() && mItemHeaderText != null){
//                mItemHeaderText.setText(currentItem.getCustomerName());
//            }else {
                if (mExclamationImg != null) {
                    switch ( DataManager.getInstance().getCustomerDebtType((CustomerRealm)currentItem)){
                        case ConstantManager.DEBT_TYPE_NORMAL:
                            mExclamationImg.setVisibility(View.VISIBLE);
                            mExclamationImg.setColorFilter(App.getColorRes(R.color.color_orange));
                            break;
                        case ConstantManager.DEBT_TYPE_OUTDATED:
                            mExclamationImg.setVisibility(View.VISIBLE);
                            mExclamationImg.setColorFilter(App.getColorRes(R.color.color_red));
                            break;
                        default:
                            mExclamationImg.setVisibility(View.INVISIBLE);
                    }
                }
                if (mCustomerNameText != null) {
                    mCustomerNameText.setText(((CustomerRealm)currentItem).getName());
                }
                if (mMapPinImg != null) {
                    mMapPinImg.setVisibility(((CustomerRealm)currentItem).getAddress().isEmpty()?View.INVISIBLE:View.VISIBLE);
                }
                if (mCallImg != null) {
                    mCallImg.setVisibility(((CustomerRealm)currentItem).getPhone().isEmpty()?View.INVISIBLE:View.VISIBLE);
                }
//            }
        }

    }


}
