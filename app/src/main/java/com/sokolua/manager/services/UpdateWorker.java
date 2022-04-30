package com.sokolua.manager.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.utils.NetworkStatusChecker;

public class UpdateWorker extends Worker {
    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        final DataManager dataManager = DataManager.getInstance();

        if (dataManager.getAutoSynchronize() && NetworkStatusChecker.isNetworkAvailable()){
            dataManager.updateAllAsync();
            dataManager.getJobManager().waitUntilConsumersAreFinished();
        }

        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();

        DataManager.getInstance().cancelAllJobs();
    }
}
