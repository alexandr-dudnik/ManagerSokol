package com.sokolua.manager.jobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.utils.AppConfig;

import io.reactivex.schedulers.Schedulers;

public class SendCustomerTaskJob extends Job {
    private final String taskId;

    public SendCustomerTaskJob(String taskId) {
        super(new Params(JobPriority.HIGH)
            .requireNetwork()
            .persist()
            .singleInstanceBy(taskId)
            .groupBy("Customers")
                .addTags(ConstantManager.UPDATE_JOB_TAG)
        );

        this.taskId = taskId;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        DataManager.getInstance()
                .sendSingleTask(this.taskId)
                .observeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .blockingSubscribe()
        ;
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.createExponentialBackoff(runCount, AppConfig.INITIAL_BACK_OFF_IN_MS);
    }
}
