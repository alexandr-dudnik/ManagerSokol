package com.sokolua.manager.ui.screens.customer.info;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerInfoNoteAdapter extends RecyclerView.Adapter<CustomerInfoNoteAdapter.ViewHolder>{
    private ArrayList<CustomerInfoNoteItem> items = new ArrayList<>();
    @Inject
    CustomerInfoScreen.Presenter mPresenter;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DaggerService.<CustomerInfoScreen.Component>getDaggerComponent(parent.getContext()).inject(this);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_info_note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerInfoNoteItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    public void addItem(CustomerInfoNoteItem item) {
        if (!items.contains(item)) {
            items.add(item);
            notifyDataSetChanged();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.note_date_text)
        TextView mNoteDate;
        @BindView(R.id.note_data_text)
        TextView mNoteData;



        private CustomerInfoNoteItem mItem = null;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        public void bind(CustomerInfoNoteItem item) {
            this.mItem = item;
            refreshInfo();
        }

        public void refreshInfo() {
            mNoteDate.setText(mItem.getDate());
            mNoteData.setText(mItem.getData());
        }

        @OnClick(R.id.delete_note_img)
        public void onIconClick(View view) {
            mPresenter.deleteNote(mItem);
        }
    }
}
