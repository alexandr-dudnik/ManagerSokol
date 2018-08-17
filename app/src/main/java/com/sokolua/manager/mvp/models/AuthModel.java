package com.sokolua.manager.mvp.models;

import java.util.UUID;

public class AuthModel extends AbstractModel {

    public AuthModel() {

    }

    public boolean isUserNameValid(String userName) {

        //return email.matches("^[a-z0-9_]([a-z0-9_-]+\\.*)+[a-z0-9_]@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$");
        return !userName.isEmpty();
    }

    public boolean isPasswordValid(String pass) {
        return pass.length() >= 8;
    }


    public boolean isUserAuth(){
        return mDataManager.isUserAuth();
    }

    public void loginUser(String login, String pass) {
        mDataManager.updateUserName(login);
        mDataManager.updateUserPassword(pass);
        mDataManager.setUserAuthToken(UUID.randomUUID().toString());
    }

    public String getUserName() {
        return mDataManager.getUserName();
    }

    public String getUserPassword() {
        return mDataManager.getUserPassword();
    }
}
