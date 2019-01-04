package com.sokolua.manager.ui.screens.customer.info;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.SwipeToDeleteCallback;

import butterknife.BindView;
import butterknife.OnClick;

public class CustomerInfoView extends AbstractView<CustomerInfoScreen.Presenter>{
    @BindView(R.id.customer_info_list)
    RecyclerView mCustomerInfoList;
    @BindView(R.id.customer_notes_list)
    RecyclerView mCustomerNotesList;


    public CustomerInfoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CustomerInfoScreen.Component>getDaggerComponent(context).inject(this);
    }


    @Override
    public boolean viewOnBackPressed() {
        return false;
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mPresenter.deleteNote(((CustomerNoteViewHolder) viewHolder).getCurrentItem().getNoteId());
            }

        });
        itemTouchHelper.attachToRecyclerView(mCustomerNotesList);

    }


    public void setNoteAdapter(ReactiveRecyclerAdapter mNoteAdapter) {
        mCustomerNotesList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL,false));
        mCustomerNotesList.setAdapter(mNoteAdapter);
        mCustomerNotesList.setHasFixedSize(true);
    }

    public void setDataAdapter(CustomerInfoDataAdapter mDataAdapter) {
        mCustomerInfoList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL,false));
        mCustomerInfoList.setAdapter(mDataAdapter);
        mCustomerNotesList.setHasFixedSize(true);
    }

    @OnClick(R.id.note_add_image)
    void clickAddNote(View v){
        mPresenter.addNewNote();
    }

}
