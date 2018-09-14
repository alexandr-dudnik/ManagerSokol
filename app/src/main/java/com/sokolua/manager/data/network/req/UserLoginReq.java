package com.sokolua.manager.data.network.req;

public class UserLoginReq {
    private String username;
    private String password;

    public UserLoginReq(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
