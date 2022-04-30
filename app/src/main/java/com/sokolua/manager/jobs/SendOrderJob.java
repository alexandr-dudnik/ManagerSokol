package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.OrderRealm;

public class SendOrderJob extends AbstractJob<OrderRealm> {

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
