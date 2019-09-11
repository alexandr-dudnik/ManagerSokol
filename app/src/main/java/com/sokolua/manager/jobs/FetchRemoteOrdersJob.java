package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;

public class FetchRemoteOrdersJob extends AbstractJob {

    public FetchRemoteOrdersJob() {
        super( "FetchOrders"
                , "FetchRemoteLists"
                , JobPriority.HIGH);
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateOrdersFromRemote());
    }

}
