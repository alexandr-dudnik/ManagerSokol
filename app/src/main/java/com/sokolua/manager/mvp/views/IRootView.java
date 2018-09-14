package com.sokolua.manager.mvp.views;


import android.support.annotation.Nullable;


public interface IRootView extends IView{
    void showMessage(String message);
    void showError(Throwable e);

    void showLoad();
    void hideLoad();

    void setBottomBarVisibility(boolean state);
    boolean getBottomBarVisibility();

    @Nullable
    IView getCurrentScreen();

}