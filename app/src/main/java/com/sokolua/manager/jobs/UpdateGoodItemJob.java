package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.ItemRealm;

public class UpdateGoodItemJob extends AbstractJob<ItemRealm> {

    public UpdateGoodItemJob(String goodItemId) {
        super(goodItemId
                ,"GoodItems"
                ,JobPriority.MIDDLE
        );
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateGoodItemFromRemote(jobId));
    }

}
