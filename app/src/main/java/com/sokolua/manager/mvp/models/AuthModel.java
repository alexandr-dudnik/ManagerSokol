package com.sokolua.manager.mvp.models;

public class AuthModel extends AbstractModel {

    public AuthModel() {

    }

    public boolean isUserAuth(){
        return mDataManager.isUserAuth();
    }

    public void loginUser(String login, String pass) {
        mDataManager.setUserAuth(true);
    }

}
