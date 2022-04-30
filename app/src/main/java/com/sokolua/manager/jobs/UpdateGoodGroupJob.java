package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;

public class UpdateGoodGroupJob extends AbstractJob<GoodsGroupRealm> {

    public UpdateGoodGroupJob(String goodGroupId) {
        super(goodGroupId
                ,"GoodGroups"
                ,JobPriority.MIDDLE
        );
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateGroupFromRemote(jobId));
    }

}
