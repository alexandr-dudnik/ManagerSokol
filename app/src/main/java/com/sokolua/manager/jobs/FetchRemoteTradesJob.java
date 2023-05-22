package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.TradeRealm;

public class FetchRemoteTradesJob extends AbstractJob<TradeRealm> {

    public FetchRemoteTradesJob() {
        super( "FetchTrades"
                , "FetchRemoteLists"
                , JobPriority.HIGH);
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateTradesFromRemote());
    }

    @Override
    protected int getRetryLimit() {
        return 5;
    }
}
