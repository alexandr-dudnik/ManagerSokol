package com.sokolua.manager.ui.screens.customer.info;

import android.view.View;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.databinding.CustomerInfoNoteItemBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

public class CustomerNoteViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<NoteRealm> {
    private CustomerInfoNoteItemBinding binding;

    @Inject
    CustomerInfoScreen.Presenter mPresenter;

    public CustomerNoteViewHolder(View itemView) {
        super(itemView);
        DaggerService.<CustomerInfoScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);

        TextView mEmptyText = itemView.findViewById(R.id.empty_list_text);
        if (mEmptyText != null) {
            mEmptyText.setText(App.getStringRes(R.string.customer_info_notes_no_notes));
        }
    }

    @Override
    public void setCurrentItem(NoteRealm currentItem) {
        super.setCurrentItem(currentItem);
        binding = CustomerInfoNoteItemBinding.bind(itemView);

        if (currentItem.isValid()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(App.getStringRes(R.string.date_format), Locale.getDefault());
            binding.noteDateText.setText(dateFormat.format(currentItem.getDate()));
            binding.noteDataText.setText(currentItem.getData());
        }
    }
}
