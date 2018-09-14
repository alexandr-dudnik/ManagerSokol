package com.sokolua.manager.data.network.error;

public class ApiError extends Throwable{
    private int statusCode;
    private String message;

    public ApiError(int statusCode) {
        super("Ошибка сервера: " + statusCode);
    }

    @Override
    public String getMessage() {
        return "Error "+statusCode+" : "+ message;
    }}
