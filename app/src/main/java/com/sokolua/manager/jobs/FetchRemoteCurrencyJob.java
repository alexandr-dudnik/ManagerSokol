package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;

public class FetchRemoteCurrencyJob extends AbstractJob {

    public FetchRemoteCurrencyJob() {
        super( "FetchCurrency"
                , "FetchRemoteLists"
                , JobPriority.HIGH);
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateCurrencyFromRemote());
    }
}
