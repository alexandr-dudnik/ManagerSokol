package com.sokolua.manager.ui.screens.customer.tasks;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.TaskRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnLongClick;
import butterknife.Optional;
import io.realm.RealmObjectChangeListener;

public class CustomerTaskViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<CustomerTaskItem> {

    @Nullable    @BindView(R.id.task_text)          TextView mTaskText;
    @Nullable    @BindView(R.id.task_type_text)     TextView mTaskType;
    @Nullable    @BindView(R.id.task_done_chk)      CheckBox mTaskDone;
    @Nullable    @BindView(R.id.task_comment_text)  TextView mTaskComment;
    @Nullable    @BindView(R.id.task_comment_edit)  EditText mEditComment;
    @Nullable    @BindView(R.id.empty_list_text)    TextView mEmptyText;

    @Inject
    CustomerTasksScreen.Presenter mPresenter;

    private RealmObjectChangeListener<TaskRealm> mTaskChangeListener = null;


    public CustomerTaskViewHolder(View itemView) {
        super(itemView);
        DaggerService.<CustomerTasksScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);

        if (mEmptyText != null){
            mEmptyText.setText(App.getStringRes(R.string.customer_task_no_tasks));
        }
    }


    @Override
    public void setCurrentItem(CustomerTaskItem currentItem) {
        super.setCurrentItem(currentItem);

        if (currentItem.isHeader()) {
            if (mTaskType != null) {
                mTaskType.setText(currentItem.getHeaderText());
            }
        } else {
            if (currentItem.getTask() != null) {
                if (!currentItem.getTask().isValid() || !currentItem.getTask().isLoaded()){
                    currentItem.getTask().removeAllChangeListeners();
                } else {
                    mTaskChangeListener = (taskRealm, changeSet) -> {
                        if (mTaskChangeListener != null) {
                            taskRealm.removeChangeListener(mTaskChangeListener);
                        }
                        setCurrentItem(new CustomerTaskItem(taskRealm));
                    };
                    currentItem.getTask().addChangeListener(mTaskChangeListener);

                    if (mTaskText != null) {
                        mTaskText.setText(currentItem.getTask().getText());
                    }
                    if (mTaskDone != null) {
                        mTaskDone.setChecked(currentItem.getTask().isDone());
                    }
                    if (mTaskComment != null) {
                        mTaskComment.setText(currentItem.getTask().getResult());
                    }
                }
            }
        }


    }

    @Optional
    @OnLongClick(R.id.task_comment_text)
    public boolean startEditComment(View view) {
        if (currentItem.getTask() != null && currentItem.getTask().isValid() && mEditComment != null && mTaskComment != null &&  mTaskDone != null) {
            mEditComment.setVisibility(View.VISIBLE);
            mTaskComment.setVisibility(View.GONE);
            mEditComment.setText(currentItem.getTask().getResult());
            mEditComment.requestFocus();
        }
        return false;
    }




    @Optional
    @OnFocusChange(R.id.task_comment_edit)
    void focusChange(View view, boolean focus){
        if (!focus && currentItem.getTask() != null && currentItem.getTask().isValid() && mEditComment != null && mTaskComment != null &&  mTaskDone != null) {
            mEditComment.clearFocus();
            mTaskComment.setText(mEditComment.getText().toString());
            mTaskComment.setVisibility(View.VISIBLE);
            mEditComment.setVisibility(View.GONE);
            mPresenter.updateTask(currentItem.getTask().getTaskId(), mTaskDone.isChecked(), mEditComment.getText().toString());
        }
    }


    @Optional
    @OnClick(R.id.task_done_chk)
    void doneChanged(View chkBox){
        if (currentItem.getTask() != null && currentItem.getTask().isValid() && mTaskDone != null) {
            if (mTaskDone.isChecked()) {
                startEditComment(mTaskComment);
            } else {
                mPresenter.updateTask(currentItem.getTask().getTaskId(), mTaskDone.isChecked(),currentItem.getTask().getResult());
            }
        }
    }


}
