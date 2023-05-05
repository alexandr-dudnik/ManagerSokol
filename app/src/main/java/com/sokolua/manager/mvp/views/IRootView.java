package com.sokolua.manager.mvp.views;


import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;


public interface IRootView extends IView{
    void showMessage(String message);
    void showMessage(String message, @StringRes int button, View.OnClickListener callback);
    void showError(Throwable e);
    void showLoad();
    void showLoad(int progressBarMax);
    void updateProgress(int currentProgress);
    void hideLoad();

    void setBottomBarVisibility(boolean state);
    boolean getBottomBarVisibility();

    @Nullable
    IView getCurrentScreen();

}