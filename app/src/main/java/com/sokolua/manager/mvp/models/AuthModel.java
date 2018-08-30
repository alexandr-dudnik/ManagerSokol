package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.network.res.UserRes;

import io.reactivex.Observable;

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

    public Observable<UserRes> loginUser(String login, String pass) {
        return mDataManager.loginUser(login, pass);

    }

    public String getUserName() {
        return mDataManager.getUserName();
    }

    public String getUserPassword() {
        return mDataManager.getUserPassword();
    }

    public String getManagerName() {
        return mDataManager.getManagerName();
    }

    public void updateUserData(UserRes userRes) {
        mDataManager.updateUserData(userRes);
    }

    public void clearUserData() {
        updateUserData(new UserRes("","",""));
        mDataManager.updateUserPassword("");
        mDataManager.updateUserName("");
        mDataManager.clearDataBase();
    }
}
