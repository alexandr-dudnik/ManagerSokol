package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.CustomerRealm;

public class FetchRemoteCustomersJob extends AbstractJob<CustomerRealm> {

    public FetchRemoteCustomersJob() {
        super("FetchCustomers"
                , "FetchRemoteLists"
                , JobPriority.HIGH);
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateCustomersFromRemote());
    }

    @Override
    protected int getRetryLimit() {
        return 3;
    }
}
