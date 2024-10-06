package com.sokolua.manager.ui.screens.customer.info;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sokolua.manager.R;
import com.sokolua.manager.databinding.CustomerInfoDataItemBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.utils.App;

import java.util.ArrayList;

import javax.inject.Inject;

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

    public void removeItem(CustomerInfoDataItem item) {
        if (items.contains(item)) {
            items.remove(item);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CustomerInfoDataItemBinding binding;
        private CustomerInfoDataItem mItem = null;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = CustomerInfoDataItemBinding.bind(itemView);
        }

        public void bind(CustomerInfoDataItem item) {
            this.mItem = item;
            refreshInfo();
        }

        public void refreshInfo() {
            binding.customerInfoHeader.setText(mItem.getHeader());
            binding.customerInfoData.setText(mItem.getData());
            if (mItem.getActionType() != CustomerInfoDataItem.ACTION_TYPE_NO_ACTION) {
                binding.customerInfoIcon.setVisibility(View.VISIBLE);
                switch (mItem.getActionType()) {
                    case CustomerInfoDataItem.ACTION_TYPE_MAKE_CALL:
                        binding.customerInfoIcon.setImageResource(R.drawable.ic_call);
                        binding.customerInfoIcon.setColorFilter(App.getColorRes(R.color.transparent));
                        binding.customerInfoIcon.setBackgroundColor(App.getColorRes(R.color.color_green));
                        break;
                    case CustomerInfoDataItem.ACTION_TYPE_OPEN_MAP:
                        binding.customerInfoIcon.setImageResource(R.drawable.ic_map);
                        binding.customerInfoIcon.setColorFilter(App.getColorRes(R.color.color_green));
                        binding.customerInfoIcon.setBackgroundColor(App.getColorRes(R.color.color_white));
                        break;
                    case CustomerInfoDataItem.ACTION_TYPE_SEND_MAIL:
                        binding.customerInfoIcon.setImageResource(R.drawable.ic_mail);
                        binding.customerInfoIcon.setColorFilter(App.getColorRes(R.color.color_black));
                        binding.customerInfoIcon.setBackgroundColor(App.getColorRes(R.color.transparent));
                        break;
                    default:
                        binding.customerInfoIcon.setVisibility(View.GONE);
                }
            } else {
                binding.customerInfoIcon.setVisibility(View.GONE);
            }
            binding.customerInfoIcon.setOnClickListener(view -> {
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
            });
        }
    }

}
