package com.sokolua.manager.data.network.res;

public class UserRes {
    public String token;
    public String fullname;
    public String dueDate;

    public UserRes(String token, String fullname, String dueDate) {
        this.token = token;
        this.fullname = fullname;
        this.dueDate = dueDate;
    }

    public String getToken() {
        return token;
    }

    public String getFullname() {
        return fullname;
    }

    public String getDueDate() {
        return dueDate;
    }
}
