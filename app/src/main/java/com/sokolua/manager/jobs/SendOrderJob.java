package com.sokolua.manager.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.utils.AppConfig;

import io.reactivex.schedulers.Schedulers;

public class SendOrderJob extends Job {
    private final String orderId;

    public SendOrderJob(String orderId) {
        super(new Params(JobPriority.HIGH)
            .requireNetwork()
            .persist()
            .singleInstanceBy(orderId)
            .groupBy("Orders")
        );

        this.orderId = orderId;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        DataManager.getInstance()
                .sendSingleOrder(this.orderId)
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
