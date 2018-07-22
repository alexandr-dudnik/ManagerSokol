package com.sokolua.manager.ui.screens.cust_list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.utils.App;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.ViewHolder> {
    private List<CustomerListItem> mCustomerListItem = new ArrayList<>();
    private boolean withHeaders = false;

    public void addItem(CustomerListItem item){
        if (!mCustomerListItem.contains(item)) {
            CustomerListItem head = new CustomerListItem(item.getCustomerName());
            if (!mCustomerListItem.contains(head)) {
                mCustomerListItem.add(head);
            }
            mCustomerListItem.add(item);
            notifyDataSetChanged();
        }

    }

    public CustomerListAdapter(boolean withHeaders) {
        this.withHeaders = withHeaders;
    }

    @Override
    public int getItemViewType(int position) {
        return mCustomerListItem.get(position).isHeader()&&withHeaders?1:0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_list_item, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_list_header, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerListItem item = mCustomerListItem.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mCustomerListItem.size();
    }

    //=============================================================================================
    //  Class View Holder
    public class ViewHolder extends RecyclerView.ViewHolder{
        private CustomerListItem mCustomerItem = null;

        @BindView(R.id.exclamation_img)
        ImageView mExclamationImg;
        @BindView(R.id.map_pin_img)
        ImageView mMapPinImg;
        @BindView(R.id.call_img)
        ImageView mCallImg;
        @BindView(R.id.customer_name_text)
        TextView mCustomerNameText;
        @BindView(R.id.item_header_text)
        TextView mItemHeaderText;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(CustomerListItem item){
            mCustomerItem= item;
            refreshInfo();
        }

        public void refreshInfo(){
            if (mCustomerItem != null) {
                if (mCustomerItem.isHeader()){
                    mItemHeaderText.setText(mCustomerItem.getCustomerName());
                }else {
                    switch (mCustomerItem.getDebtType()){
                        case CustomerListItem.DEBT_NORMAL:
                            mExclamationImg.setColorFilter(App.getColorRes(R.color.color_orange), android.graphics.PorterDuff.Mode.SRC_IN);
                            break;
                        case CustomerListItem.DEBT_OUTDATED:
                            mExclamationImg.setColorFilter(App.getColorRes(R.color.color_red), android.graphics.PorterDuff.Mode.SRC_IN);
                            break;
                        default:
                            mExclamationImg.setVisibility(View.GONE);
                    }
                    mCustomerNameText.setText(mCustomerItem.getCustomerName());
                    mMapPinImg.setVisibility(mCustomerItem.getAddress().isEmpty()?View.GONE:View.VISIBLE);
                    mCallImg.setVisibility(mCustomerItem.getPhone().isEmpty()?View.GONE:View.VISIBLE);
                }
            }
        }

    }

}
