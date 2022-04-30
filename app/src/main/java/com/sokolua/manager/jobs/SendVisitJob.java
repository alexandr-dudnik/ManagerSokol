package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.VisitRealm;

public class SendVisitJob extends AbstractJob<VisitRealm> {

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
