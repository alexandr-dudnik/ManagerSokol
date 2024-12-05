package com.sokolua.manager.data.network.req;

import android.util.Base64;

import androidx.annotation.Keep;


@Keep
public class UserLoginReq {
    private final String username;
    private final String password;

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
