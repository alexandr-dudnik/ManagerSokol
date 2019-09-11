package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;

public class SendVisitJob extends AbstractJob {

    public SendVisitJob(String visitId) {
        super(visitId
                ,"CustomersData"
                ,JobPriority.HIGH
        );
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().sendSingleVisit(jobId));
    }

}
