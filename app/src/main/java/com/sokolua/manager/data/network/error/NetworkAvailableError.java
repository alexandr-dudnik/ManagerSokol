package com.sokolua.manager.data.network.error;

public class NetworkAvailableError extends Throwable {
    public NetworkAvailableError() {
        super("Интернет не доступен попробуйте позже!");
    }
}
