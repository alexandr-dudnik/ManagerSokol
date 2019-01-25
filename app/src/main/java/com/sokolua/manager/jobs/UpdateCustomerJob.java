package com.sokolua.manager.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.utils.AppConfig;

import io.reactivex.schedulers.Schedulers;

public class UpdateCustomerJob extends Job {
    private final String customerId;

    public UpdateCustomerJob(String customerId) {
        super(new Params(JobPriority.MIDDLE)
            .requireNetwork()
            .persist()
            .singleInstanceBy(customerId)
            .groupBy("Customers")
        );

        this.customerId = customerId;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        DataManager.getInstance()
                .updateCustomerFromRemote(this.customerId)
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