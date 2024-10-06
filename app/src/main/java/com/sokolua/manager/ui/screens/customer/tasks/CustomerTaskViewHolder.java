package com.sokolua.manager.ui.screens.customer.tasks;

import android.view.View;
import android.widget.TextView;

import androidx.viewbinding.ViewBinding;

import com.sokolua.manager.R;
import com.sokolua.manager.data.storage.realm.TaskRealm;
import com.sokolua.manager.databinding.CustomerTaskHeaderBinding;
import com.sokolua.manager.databinding.CustomerTaskItemBinding;
import com.sokolua.manager.databinding.EmptyListItemBinding;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import javax.annotation.Nullable;

import io.realm.RealmObjectChangeListener;
import kotlin.jvm.functions.Function3;

public class CustomerTaskViewHolder<B extends ViewBinding> extends ReactiveRecyclerAdapter.ReactiveViewHolder<CustomerTaskItem> {
    private final B binding;
    private final Function3<String, Boolean, String, Boolean> updateTask;

    private RealmObjectChangeListener<TaskRealm> mTaskChangeListener = null;


    public CustomerTaskViewHolder(View itemView, B binding, @Nullable Function3<String, Boolean, String, Boolean> updateTaskFun) {
        super(itemView);

        this.binding = binding;
        this.updateTask = updateTaskFun;

        if (binding instanceof EmptyListItemBinding) {
            ((EmptyListItemBinding) binding).emptyListText.setText(App.getStringRes(R.string.customer_debt_no_debt));
        }
    }

    @Override
    public void setCurrentItem(CustomerTaskItem currentItem) {
        super.setCurrentItem(currentItem);

        if (currentItem.isHeader()) {
            TextView mTaskType = ((CustomerTaskHeaderBinding) binding).taskTypeText;
            mTaskType.setText(currentItem.getHeaderText());
        } else {
            if (currentItem.getTask() != null && binding instanceof CustomerTaskItemBinding) {
                if (!currentItem.getTask().isValid() || !currentItem.getTask().isLoaded()) {
                    currentItem.getTask().removeAllChangeListeners();
                } else {
                    mTaskChangeListener = (taskRealm, changeSet) -> {
                        if (mTaskChangeListener != null) {
                            taskRealm.removeChangeListener(mTaskChangeListener);
                        }
                        setCurrentItem(new CustomerTaskItem(taskRealm));
                    };
                    currentItem.getTask().addChangeListener(mTaskChangeListener);

                    ((CustomerTaskItemBinding) binding).taskText.setText(currentItem.getTask().getText());
                }
                ((CustomerTaskItemBinding) binding).taskDoneChk.setChecked(currentItem.getTask().isDone());
                ((CustomerTaskItemBinding) binding).taskCommentText.setText(currentItem.getTask().getResult());

                ((CustomerTaskItemBinding) binding).taskCommentText.setOnLongClickListener(view -> {
                    if (currentItem.getTask() != null && currentItem.getTask().isValid()) {
                        ((CustomerTaskItemBinding) binding).taskCommentText.setVisibility(View.GONE);
                        ((CustomerTaskItemBinding) binding).taskCommentEdit.setVisibility(View.VISIBLE);
                        ((CustomerTaskItemBinding) binding).taskCommentEdit.setText(currentItem.getTask().getResult());
                        ((CustomerTaskItemBinding) binding).taskCommentEdit.requestFocus();
                    }
                    return false;
                });

                ((CustomerTaskItemBinding) binding).taskCommentEdit.setOnFocusChangeListener((view, focus) -> {
                    if (!focus && currentItem.getTask() != null && currentItem.getTask().isValid()) {
                        ((CustomerTaskItemBinding) binding).taskCommentEdit.clearFocus();
                        ((CustomerTaskItemBinding) binding).taskCommentText.setText(((CustomerTaskItemBinding) binding).taskCommentEdit.getText().toString());
                        ((CustomerTaskItemBinding) binding).taskCommentText.setVisibility(View.VISIBLE);
                        ((CustomerTaskItemBinding) binding).taskCommentEdit.setVisibility(View.GONE);
                        if (updateTask != null) {
                            updateTask.invoke(
                                    currentItem.getTask().getTaskId(),
                                    ((CustomerTaskItemBinding) binding).taskDoneChk.isChecked(),
                                    ((CustomerTaskItemBinding) binding).taskCommentEdit.getText().toString()
                            );
                        }
                    }
                });

                ((CustomerTaskItemBinding) binding).taskDoneChk.setOnClickListener(view -> {
                    if (currentItem.getTask() != null && currentItem.getTask().isValid()) {
                        if (((CustomerTaskItemBinding) binding).taskDoneChk.isChecked()) {
                            ((CustomerTaskItemBinding) binding).taskCommentText.performLongClick();
                        } else {
                            if (updateTask != null) {
                                updateTask.invoke(
                                        currentItem.getTask().getTaskId(),
                                        ((CustomerTaskItemBinding) binding).taskDoneChk.isChecked(),
                                        currentItem.getTask().getResult()
                                );
                            }
                        }
                    }
                });
            }
        }
    }
}
