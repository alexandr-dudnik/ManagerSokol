package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;

public class SendCustomerTaskJob extends AbstractJob {

    public SendCustomerTaskJob(String taskId) {
        super(taskId
                ,"CustomersData"
                ,JobPriority.HIGH
        );
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().sendSingleTask(jobId));
    }
}
