package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;

public class FetchRemoteGoodGroupsJob extends AbstractJob {


    public FetchRemoteGoodGroupsJob() {
        super( "FetchGroups"
                , "FetchRemoteLists"
                , JobPriority.HIGH);
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateGroupsFromRemote());
    }

}
