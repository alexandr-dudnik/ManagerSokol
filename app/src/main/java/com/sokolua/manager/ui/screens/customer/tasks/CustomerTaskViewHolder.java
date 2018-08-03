package com.sokolua.manager.ui.screens.customer.tasks;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.screens.customer.info.CustomerInfoScreen;
import com.sokolua.manager.utils.ReactiveRecyclerAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerTaskViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<CustomerTaskItem> {

    @Nullable @BindView(R.id.task_text)      TextView mTaskText;
    @Nullable @BindView(R.id.task_type_text) TextView mTaskTypeText;

    @Inject
    CustomerTasksScreen.Presenter mPresenter;

    public CustomerTaskViewHolder(View itemView) {
        super(itemView);
        DaggerService.<CustomerTasksScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);
    }


    @Override
    public void setCurrentItem(CustomerTaskItem currentItem) {
        super.setCurrentItem(currentItem);

        if (currentItem.isHeader()){
            if (mTaskTypeText != null) {
                mTaskTypeText.setText(currentItem.getHeaderText());
            }
        }else{
            if (mTaskText != null && currentItem.getTask() != null) {
                mTaskText.setText(currentItem.getTask().getText());
            }
        }

    }


}
