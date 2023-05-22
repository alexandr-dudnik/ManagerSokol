package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.OrderRealm;

public class FetchRemoteOrdersJob extends AbstractJob<OrderRealm> {

    public FetchRemoteOrdersJob() {
        super( "FetchOrders"
                , "FetchRemoteLists"
                , JobPriority.HIGH);
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateOrdersFromRemote());
    }

    @Override
    protected int getRetryLimit() {
        return 5;
    }
}
