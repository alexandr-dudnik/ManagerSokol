package com.sokolua.manager.ui.screens.customer.tasks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.utils.App;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerTaskAdapter extends RecyclerView.Adapter<CustomerTaskAdapter.ViewHolder> {
    private ArrayList<CustomerTaskItem> items = new ArrayList<>();
    @Inject
    CustomerTasksScreen.Presenter mPresenter;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DaggerService.<CustomerTasksScreen.Component>getDaggerComponent(parent.getContext()).inject(this);
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_task_item, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_task_header, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerTaskItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isHeader()?1:0;
    }

    public void addItem(String taskText, int taskType, boolean header) {
        items.add(new CustomerTaskItem(taskText, taskType, header));
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @Nullable @BindView(R.id.task_text)      TextView mTaskText;
        @Nullable @BindView(R.id.task_type_text) TextView mTaskTypeText;


        private CustomerTaskItem mItem = null;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        public void bind(CustomerTaskItem item) {
            this.mItem = item;
            refreshInfo();
        }

        public void refreshInfo() {
            if (mItem.isHeader()){
                if (mTaskTypeText != null) {
                    mTaskTypeText.setText(mItem.getTaskText());
                }
            }else{
                if (mTaskText != null) {
                    mTaskText.setText(mItem.getTaskText());
                }
            }
        }

    }

}
