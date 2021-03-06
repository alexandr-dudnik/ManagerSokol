package com.sokolua.manager.data.network.res;


import androidx.annotation.Keep;

@Keep
public class UserRes {
    private String token;
    private String fullname;
    private String dueDate;

    public UserRes(String token, String fullname, String dueDate) {
        this.token = token;
        this.fullname = fullname;
        this.dueDate = dueDate;
    }

    public String getToken() {
        return token;
    }

    public String getFullName() {
        return fullname;
    }

    public String getDueDate() {
        return dueDate;
    }
}
