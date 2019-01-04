package com.sokolua.manager.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.utils.AppConfig;

import io.reactivex.schedulers.Schedulers;

public class FetchRemoteGoodGroupsJob extends Job {

    public FetchRemoteGoodGroupsJob() {
        super(new Params(JobPriority.HIGH)
            .requireNetwork()
            .persist()
            .singleInstanceBy("FetchGroups")
            .groupBy("FetchRemoteLists")
        );
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        DataManager.getInstance()
                .updateGroupsFromRemote()
                .observeOn(Schedulers.single())
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
