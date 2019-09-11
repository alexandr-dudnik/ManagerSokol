package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;

public class SendOrderJob extends AbstractJob {

    public SendOrderJob(String orderId) {
        super(orderId
                ,"OrderData"
                ,JobPriority.HIGH
        );
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().sendSingleOrder(jobId));
    }

}
