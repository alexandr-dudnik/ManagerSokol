package com.sokolua.manager.ui.screens.cust_list;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.ReactiveRecyclerAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class CustomerViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<CustomerListItem> {

    @Nullable @BindView(R.id.exclamation_img)   ImageView mExclamationImg;
    @Nullable @BindView(R.id.map_pin_img)       ImageView mMapPinImg;
    @Nullable @BindView(R.id.call_img)          ImageView mCallImg;
    @Nullable @BindView(R.id.customer_name_text)TextView mCustomerNameText;
    @Nullable @BindView(R.id.item_header_text)  TextView mItemHeaderText;

    @Inject
    CustomerListScreen.Presenter mPresenter;

    public CustomerViewHolder(View itemView) {
        super(itemView);
        DaggerService.<CustomerListScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
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
                    switch ( DataManager.getInstance().getCustomerDebtType(((CustomerRealm)currentItem).getCustomerId())){
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

    @Optional
    @OnClick(R.id.map_pin_img)
    public void onMapClick(View view){
        mPresenter.openCustomerMap((CustomerRealm)currentItem);
    }

    @Optional
    @OnClick(R.id.call_img)
    public void onCallClick(View view){
        mPresenter.callToCustomer((CustomerRealm)currentItem);
    }

    @Optional
    @OnClick({R.id.exclamation_img, R.id.customer_name_text, R.id.customer_list_item})
    public void onCustomerClick(View view){
        mPresenter.openCustomerCard((CustomerRealm)currentItem);
    }


}
