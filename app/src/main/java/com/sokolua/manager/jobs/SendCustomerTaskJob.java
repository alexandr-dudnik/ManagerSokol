package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.TaskRealm;

public class SendCustomerTaskJob extends AbstractJob<TaskRealm> {

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
