package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;

public class FetchRemoteTradesJob extends AbstractJob {

    public FetchRemoteTradesJob() {
        super( "FetchTrades"
                , "FetchRemoteLists"
                , JobPriority.HIGH);
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateTradesFromRemote());
    }
}
