package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.network.res.GoodItemRes;
import com.sokolua.manager.data.storage.realm.ItemRealm;

public class FetchRemoteGoodItemsJob extends AbstractJob<ItemRealm> {

    public FetchRemoteGoodItemsJob() {
        super( "FetchItems"
                , "FetchRemoteLists"
                , JobPriority.HIGH);
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateItemsFromRemote());
    }

    @Override
    protected int getRetryLimit() {
        return 5;
    }
}
