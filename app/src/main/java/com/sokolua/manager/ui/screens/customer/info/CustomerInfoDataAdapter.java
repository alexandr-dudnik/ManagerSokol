package com.sokolua.manager.ui.screens.customer.info;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.utils.App;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerInfoDataAdapter extends RecyclerView.Adapter<CustomerInfoDataAdapter.ViewHolder> {
    private ArrayList<CustomerInfoDataItem> items = new ArrayList<>();
    @Inject
    CustomerInfoScreen.Presenter mPresenter;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DaggerService.<CustomerInfoScreen.Component>getDaggerComponent(parent.getContext()).inject(this);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_info_data_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerInfoDataItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    public void addItem(CustomerInfoDataItem item) {
        if (!items.contains(item)) {
            items.add(item);
            notifyDataSetChanged();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.customer_info_header)
        TextView mCustomerInfoHeader;
        @BindView(R.id.customer_info_data)
        TextView mCustomerInfoData;
        @BindView(R.id.customer_info_icon)
        ImageView mCustomerInfoIcon;

        @BindDrawable(R.drawable.ic_person_pin)
        Drawable mapImage;
        @BindDrawable(R.drawable.ic_call)
        Drawable callImage;
        @BindDrawable(R.drawable.ic_mail)
        Drawable mailImage;


        private CustomerInfoDataItem mItem = null;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        public void bind(CustomerInfoDataItem item) {
            this.mItem = item;
            refreshInfo();
        }

        public void refreshInfo() {
            mCustomerInfoHeader.setText(mItem.getHeader());
            mCustomerInfoData.setText(mItem.getData());
            if (mItem.getActionType()!=CustomerInfoDataItem.ACTION_TYPE_NO_ACTION){
                mCustomerInfoIcon.setVisibility(View.VISIBLE);
                switch (mItem.getActionType()) {
                    case CustomerInfoDataItem.ACTION_TYPE_MAKE_CALL:
                        mCustomerInfoIcon.setImageDrawable(callImage);
                        mCustomerInfoIcon.setColorFilter(App.getColorRes(R.color.transparent));
                        mCustomerInfoIcon.setBackgroundColor(App.getColorRes(R.color.color_green));
                        break;
                    case CustomerInfoDataItem.ACTION_TYPE_OPEN_MAP:
                        mCustomerInfoIcon.setImageDrawable(mapImage);
                        mCustomerInfoIcon.setColorFilter(App.getColorRes(R.color.color_green));
                        mCustomerInfoIcon.setBackgroundColor(App.getColorRes(R.color.color_white));
                        break;
                    case CustomerInfoDataItem.ACTION_TYPE_SEND_MAIL:
                        mCustomerInfoIcon.setImageDrawable(mailImage);
                        mCustomerInfoIcon.setColorFilter(App.getColorRes(R.color.color_black));
                        mCustomerInfoIcon.setBackgroundColor(App.getColorRes(R.color.transparent));
                        break;
                    default:
                        mCustomerInfoIcon.setVisibility(View.GONE);
                }
            }else{
                mCustomerInfoIcon.setVisibility(View.GONE);
            }

        }

        @OnClick(R.id.customer_info_icon)
        public void onIconClick(View view) {
            switch (mItem.getActionType()) {
                case CustomerInfoDataItem.ACTION_TYPE_MAKE_CALL:
                    mPresenter.callToCustomer(mItem);
                    break;
                case CustomerInfoDataItem.ACTION_TYPE_OPEN_MAP:
                    mPresenter.openMap(mItem);
                    break;
                case CustomerInfoDataItem.ACTION_TYPE_SEND_MAIL:
                    mPresenter.sendEmail(mItem);
                    break;
                default:

            }

        }
    }

}
