package com.sokolua.manager.ui.screens.routes;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.ui.screens.customer_list.CustomerListItem;
import com.sokolua.manager.utils.App;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class RouteViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<CustomerListItem> {

    @Nullable @BindView(R.id.exclamation_img)   ImageView mExclamationImg;
    @Nullable @BindView(R.id.map_pin_img)       ImageView mMapPinImg;
    @Nullable @BindView(R.id.call_img)          ImageView mCallImg;
    @Nullable @BindView(R.id.customer_name_text)TextView mCustomerNameText;
    @Nullable @BindView(R.id.item_header_text)  TextView mItemHeaderText;

    @Inject
    RoutesScreen.Presenter mPresenter;

    public RouteViewHolder(View itemView) {
        super(itemView);
        DaggerService.<RoutesScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);
    }


    @Override
    public void setCurrentItem(CustomerListItem currentItem) {
        super.setCurrentItem(currentItem);

        if (currentItem != null) {
            if (currentItem.isHeader() && mItemHeaderText != null){
                mItemHeaderText.setText(currentItem.getHeaderText());
            }else if (currentItem.getCustomer() != null){
                if (mExclamationImg != null) {
                    switch ( DataManager.getInstance().getCustomerDebtType(currentItem.getCustomer().getCustomerId())){
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
                    mCustomerNameText.setText(currentItem.getCustomer().getName());
                }
                if (mMapPinImg != null) {
                    mMapPinImg.setVisibility(currentItem.getCustomer().getAddress().isEmpty()?View.INVISIBLE:View.VISIBLE);
                }
                if (mCallImg != null) {
                    mCallImg.setVisibility(currentItem.getCustomer().getPhone().isEmpty()?View.INVISIBLE:View.VISIBLE);
                }
            }
        }

    }

//    @Optional
//    @OnClick(R.id.map_pin_img)
//    public void onMapClick(View view){
//        if (currentItem.getCustomer() != null) {
//
//            mPresenter.openCustomerMap(currentItem.getCustomer());
//        }
//    }
//
//    @Optional
//    @OnClick(R.id.call_img)
//    public void onCallClick(View view){
//        if (currentItem.getCustomer() != null) {
//
//            mPresenter.callToCustomer(currentItem.getCustomer());
//        }
//    }
//
    @Optional
    @OnClick({R.id.exclamation_img, R.id.customer_name_text, R.id.customer_list_item})
    public void onCustomerClick(View view){
        if (currentItem.getCustomer() != null) {

            mPresenter.openCustomerCard(currentItem.getCustomer());
        }
    }


}
