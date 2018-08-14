package com.sokolua.manager.ui.screens.customer.info;

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
import butterknife.OnClick;

public class CustomerNoteViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<NoteRealm> {

    @BindView(R.id.note_date_text)
    TextView mNoteDate;
    @BindView(R.id.note_data_text)
    TextView mNoteData;

    @Inject
    CustomerInfoScreen.Presenter mPresenter;

    public CustomerNoteViewHolder(View itemView) {
        super(itemView);
        DaggerService.<CustomerInfoScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);
    }


    @Override
    public void setCurrentItem(NoteRealm currentItem) {
        super.setCurrentItem(currentItem);

        SimpleDateFormat dateFormat = new SimpleDateFormat(App.getStringRes(R.string.date_format), Locale.getDefault());
        mNoteDate.setText(dateFormat.format(currentItem.getDate()));
        mNoteData.setText(currentItem.getData());

    }

    @OnClick(R.id.delete_note_img)
    public void onIconClick(View view) {
        mPresenter.deleteNote(currentItem);
    }


}
