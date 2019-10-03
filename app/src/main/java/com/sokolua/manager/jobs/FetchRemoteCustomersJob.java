package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;

public class FetchRemoteCustomersJob extends AbstractJob {

    public FetchRemoteCustomersJob() {
        super("FetchCustomers"
                , "FetchRemoteLists"
                , JobPriority.HIGH);
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateCustomersFromRemote());
    }

}
