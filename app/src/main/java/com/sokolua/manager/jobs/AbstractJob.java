package com.sokolua.manager.jobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.utils.AppConfig;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

abstract class AbstractJob extends Job {
    String jobId;

    //region ============================== Getters =================

    public String getJobId() {
        return jobId;
    }

    //endregion =========================== Getters =================


    AbstractJob(String jobId, String jobGroup, int priority) {
        super(new Params(priority)
                .requireNetwork()
                .persist()
                .singleInstanceBy(jobId)
                .groupBy(jobGroup)
                .addTags(ConstantManager.UPDATE_JOB_TAG)
                //.overrideDeadlineToCancelInMs(AppConfig.JOB_TIMEOUT)
        );
        this.jobId = jobId;
    }

    @Override
    public void onAdded() {

    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.createExponentialBackoff(runCount, AppConfig.INITIAL_BACK_OFF_IN_MS);
    }

    @Override
    abstract public void onRun() throws Throwable;

    void runJob(Observable jobObs) throws Throwable {
        jobObs.observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .blockingSubscribe();
    }
}
