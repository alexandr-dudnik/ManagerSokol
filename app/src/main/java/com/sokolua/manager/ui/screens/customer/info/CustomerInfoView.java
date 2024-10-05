package com.sokolua.manager.ui.screens.customer.info;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sokolua.manager.databinding.ScreenCustomerInfoBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.SwipeToDeleteCallback;

public class CustomerInfoView extends AbstractView<CustomerInfoScreen.Presenter, ScreenCustomerInfoBinding> {

    public CustomerInfoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ScreenCustomerInfoBinding bindView(View view) {
        return ScreenCustomerInfoBinding.bind(view);
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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new SwipeToDeleteCallback(getContext()) {
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        mPresenter.deleteNote(((CustomerNoteViewHolder) viewHolder).getCurrentItem().getNoteId());
                    }
                }
        );
        itemTouchHelper.attachToRecyclerView(binding.customerNotesList);

        binding.noteAddImage.setOnClickListener(view -> mPresenter.addNewNote());
    }

    public void setNoteAdapter(ReactiveRecyclerAdapter mNoteAdapter) {
        binding.customerNotesList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.customerNotesList.setAdapter(mNoteAdapter);
        binding.customerNotesList.setHasFixedSize(true);
    }

    public void setDataAdapter(CustomerInfoDataAdapter mDataAdapter) {
        binding.customerInfoList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.customerInfoList.setAdapter(mDataAdapter);
        binding.customerInfoList.setHasFixedSize(true);
    }
}
