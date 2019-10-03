package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;

public class UpdateGoodItemJob extends AbstractJob {

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
