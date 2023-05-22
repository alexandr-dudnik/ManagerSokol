package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.CurrencyRealm;

public class FetchRemoteCurrencyJob extends AbstractJob<CurrencyRealm> {

    public FetchRemoteCurrencyJob() {
        super( "FetchCurrency"
                , "FetchRemoteLists"
                , JobPriority.HIGH);
    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().updateCurrencyFromRemote());
    }

    @Override
    protected int getRetryLimit() {
        return 10;
    }
}
