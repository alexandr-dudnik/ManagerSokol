package com.sokolua.manager.ui.screens.routes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class RouteViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<RouteListItem> {

    @Nullable @BindView(R.id.check_in_img)      ImageView mCheckInImg;
    @Nullable @BindView(R.id.map_pin_img)       ImageView mMapPinImg;
    @Nullable @BindView(R.id.call_img)          ImageView mCallImg;
    @Nullable @BindView(R.id.customer_name_text)TextView mCustomerNameText;
    @Nullable @BindView(R.id.item_header_text)  TextView mItemHeaderText;
    @Nullable @BindView(R.id.empty_list_text)   TextView mEmptyText;

    @Inject
    RoutesScreen.Presenter mPresenter;

    public RouteViewHolder(View itemView) {
        super(itemView);
        DaggerService.<RoutesScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);

        if (mEmptyText != null){
            mEmptyText.setText(App.getStringRes(R.string.routes_no_route));
        }
    }


    @Override
    public void setCurrentItem(RouteListItem currentItem) {
        super.setCurrentItem(currentItem);

        if (currentItem != null) {
            if (currentItem.isHeader() && mItemHeaderText != null){
                mItemHeaderText.setText(currentItem.getHeaderText());
            }else if (currentItem.getCustomer() != null && currentItem.getCustomer().isValid() && currentItem.getVisit() != null && currentItem.getVisit().isValid()){
                if (mCheckInImg != null) {
                    if (currentItem.getVisit().isToSync()) {
                        mCheckInImg.setColorFilter(App.getColorRes(R.color.color_orange));
                    }else {
                        if (currentItem.getVisit().isDone()) {
                            mCheckInImg.setColorFilter(App.getColorRes(R.color.color_green));
                        } else {
                            mCheckInImg.setColorFilter(App.getColorRes(R.color.color_red));
                        }
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

    @Optional
    @OnClick(R.id.map_pin_img)
    void onMapClick(View view){
        if (currentItem.getCustomer() != null) {

            mPresenter.openCustomerMap(currentItem.getCustomer());
        }
    }

    @Optional
    @OnClick(R.id.call_img)
    void onCallClick(View view){
        if (currentItem.getCustomer() != null) {

            mPresenter.callToCustomer(currentItem.getCustomer());
        }
    }

    @Optional
    @OnClick({R.id.check_in_img, R.id.customer_list_item})
    void onCheckInClick(View view){
        if (currentItem.getVisit() != null && !currentItem.getVisit().isDone()) {
            mPresenter.doCheckIn(currentItem.getVisit());
        }
    }


    @Optional
    @OnClick({R.id.customer_name_text})
    void onCustomerClick(View view){
        if (currentItem.getCustomer() != null) {

            mPresenter.openCustomerCard(currentItem.getCustomer());
        }
    }


}
