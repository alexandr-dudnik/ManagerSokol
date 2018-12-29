package com.sokolua.manager.ui.screens.customer.info;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerNoteViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<NoteRealm> {

    @Nullable  @BindView(R.id.note_date_text)    TextView mNoteDate;
    @Nullable  @BindView(R.id.note_data_text)    TextView mNoteData;
    @Nullable @BindView(R.id.empty_list_text)    TextView mEmptyText;

    @Inject
    CustomerInfoScreen.Presenter mPresenter;

    public CustomerNoteViewHolder(View itemView) {
        super(itemView);
        DaggerService.<CustomerInfoScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);

        if (mEmptyText != null){
            mEmptyText.setText(App.getStringRes(R.string.customer_info_notes_no_notes));
        }
    }


    @Override
    public void setCurrentItem(NoteRealm currentItem) {
        super.setCurrentItem(currentItem);

        SimpleDateFormat dateFormat = new SimpleDateFormat(App.getStringRes(R.string.date_format), Locale.getDefault());
        mNoteDate.setText(dateFormat.format(currentItem.getDate()));
        mNoteData.setText(currentItem.getData());

    }


}
