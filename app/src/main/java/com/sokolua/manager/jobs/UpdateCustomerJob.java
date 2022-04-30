package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.CustomerRealm;

public class UpdateCustomerJob extends AbstractJob<CustomerRealm> {

    public UpdateCustomerJob(String customerId) {
        super(customerId
                ,"Customers"
                ,JobPriority.MIDDLE
        );
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateCustomerFromRemote(jobId));
    }

}
