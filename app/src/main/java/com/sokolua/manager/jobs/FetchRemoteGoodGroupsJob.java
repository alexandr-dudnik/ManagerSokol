package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;

public class FetchRemoteGoodGroupsJob extends AbstractJob<GoodsGroupRealm> {


    public FetchRemoteGoodGroupsJob() {
        super( "FetchGroups"
                , "FetchRemoteLists"
                , JobPriority.HIGH);
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateGroupsFromRemote());
    }

    @Override
    protected int getRetryLimit() {
        return 5;
    }
}
