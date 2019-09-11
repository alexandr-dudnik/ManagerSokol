package com.sokolua.manager.mvp.models;

import android.graphics.Bitmap;

import com.sokolua.manager.data.storage.realm.VisitRealm;
import com.sokolua.manager.utils.UiHelper;

public class CheckInModel extends AbstractModel {
    public VisitRealm getVisitById(String visitId) {
        return mDataManager.getVisitById(visitId);
    }

    public void updateVisitGeolocation(String visitId, float mLat, float mLong) {
        mDataManager.updateVisitGeolocation(visitId, mLat, mLong);
    }

    public void setVisitScreenshot(String visitId, Bitmap scr) {
        String imageURI = UiHelper.saveImageBitmap(scr, visitId);
        if (!imageURI.isEmpty()) {
            mDataManager.updateVisitScreenshot(visitId, imageURI);
        }
    }
}
