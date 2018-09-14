package com.sokolua.manager.data.network.req;

import android.util.Base64;

public class UserLoginReq {
    private String username;
    private String password;

    public UserLoginReq(String username, String password) {
        this.username = username;
        this.password = Base64.encodeToString(password.getBytes(), Base64.DEFAULT);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
