package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.OrderRealm;

public class UpdateOrderJob extends AbstractJob<OrderRealm> {

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
