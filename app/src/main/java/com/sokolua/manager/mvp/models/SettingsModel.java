package com.sokolua.manager.mvp.models;

public class SettingsModel extends AbstractModel {
    public String getServerAddress() {
        return mDataManager.getServerAddress();
    }

    public void updateServerAddress(String address) {
        mDataManager.updateServerAddress(address);
    }

    public Boolean getAutoSynchronize() {
        return mDataManager.getAutoSynchronize();
    }

    public void updateAutoSynchronize(Boolean sync) {
        mDataManager.updateAutoSynchronize(sync);
    }
}
