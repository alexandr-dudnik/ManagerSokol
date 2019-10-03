package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;

public class UpdateOrderJob extends AbstractJob {

    public UpdateOrderJob(String orderId) {
        super(orderId
                ,"Orders"
                ,JobPriority.MIDDLE
        );
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateOrderFromRemote(jobId));
    }

}
