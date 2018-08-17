package com.sokolua.manager.mvp.views;

public interface IAuthView extends IView {
    String getUserName();
    String getUserPassword();

    void showInvalidUserName();
    void showInvalidPassword();

    void login_error();
}
