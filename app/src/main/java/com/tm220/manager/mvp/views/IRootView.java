package com.tm220.manager.mvp.views;


import android.support.annotation.Nullable;


public interface IRootView extends IView{
    void showMessage(String message);
    void showError(Throwable e);

    void showLoad();
    void hideLoad();

    @Nullable
    IView getCurrentScreen();

}