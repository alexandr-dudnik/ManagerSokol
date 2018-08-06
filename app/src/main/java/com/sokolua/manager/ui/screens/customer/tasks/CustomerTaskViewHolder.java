package com.sokolua.manager.ui.screens.customer.tasks;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.TaskRealm;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.utils.ReactiveRecyclerAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnLongClick;
import butterknife.Optional;
import io.realm.ObjectChangeSet;
import io.realm.RealmObjectChangeListener;

public class CustomerTaskViewHolder extends ReactiveRecyclerAdapter.ReactiveViewHolder<CustomerTaskItem> {

    @Nullable
    @BindView(R.id.task_text)
    TextView mTaskText;
    @Nullable
    @BindView(R.id.task_type_text)
    TextView mTaskType;
    @Nullable
    @BindView(R.id.task_done_chk)
    CheckBox mTaskDone;
    @Nullable
    @BindView(R.id.task_comment_text)
    TextView mTaskComment;
    @Nullable
    @BindView(R.id.task_comment_edit)
    EditText mEditComment;

    @Inject
    CustomerTasksScreen.Presenter mPresenter;

    private RealmObjectChangeListener<TaskRealm> mTaskChangeListener = null;


    public CustomerTaskViewHolder(View itemView) {
        super(itemView);
        DaggerService.<CustomerTasksScreen.Component>getDaggerComponent(itemView.getContext()).inject(this);
        ButterKnife.bind(this, itemView);
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

    @Optional
    @OnLongClick(R.id.task_comment_text)
    public boolean startEditComment(View view) {
        if (currentItem.getTask() != null && mEditComment != null && mTaskComment != null &&  mTaskDone != null) {
            mEditComment.setVisibility(View.VISIBLE);
            mTaskComment.setVisibility(View.GONE);
            mEditComment.setText(currentItem.getTask().getResult());
            mEditComment.requestFocus();
        }
        return false;
    }



    @Optional
    @OnEditorAction(R.id.task_comment_edit)
    public boolean commentEditorAction(TextView view, int actionCode, android.view.KeyEvent event){

        switch (actionCode) {
            case EditorInfo.IME_ACTION_DONE:
            case EditorInfo.IME_ACTION_GO:
            case EditorInfo.IME_ACTION_NEXT:
            case EditorInfo.IME_ACTION_PREVIOUS:
            case EditorInfo.IME_ACTION_SEARCH:
            case EditorInfo.IME_ACTION_SEND:
                if (currentItem.getTask() != null && mEditComment != null && mTaskComment != null &&  mTaskDone != null) {
                    mEditComment.clearFocus();
                    mTaskComment.setText(mEditComment.getText().toString());
                    mTaskComment.setVisibility(View.VISIBLE);
                    mEditComment.setVisibility(View.GONE);
                    mPresenter.updateTask(currentItem.getTask().getTaskId(), mTaskDone.isChecked(), mEditComment.getText().toString());
                }
                break;

            case EditorInfo.IME_ACTION_NONE:
            case EditorInfo.IME_ACTION_UNSPECIFIED:
            default:
                if (mEditComment != null && mTaskComment != null) {
                    mEditComment.clearFocus();
                    mTaskComment.setVisibility(View.VISIBLE);
                    mEditComment.setVisibility(View.GONE);
                }
                break;
        }
        return false;
    }

    @Optional
    @OnClick(R.id.task_done_chk)
    void doneChanged(View chkBox){
        if (currentItem.getTask() != null && mTaskDone != null) {
            if (mTaskDone.isChecked()) {
                startEditComment(mTaskComment);
            } else {
                mPresenter.updateTask(currentItem.getTask().getTaskId(), mTaskDone.isChecked(),currentItem.getTask().getResult());
            }
        }
    }


}
