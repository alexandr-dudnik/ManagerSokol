package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;

public class FetchRemoteGoodItemsJob extends AbstractJob {

    public FetchRemoteGoodItemsJob() {
        super( "FetchItems"
                , "FetchRemoteLists"
                , JobPriority.HIGH);
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateItemsFromRemote());
    }

}
