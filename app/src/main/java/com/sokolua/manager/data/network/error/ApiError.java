package com.sokolua.manager.data.network.error;

import com.sokolua.manager.R;
import com.sokolua.manager.utils.App;

import java.util.Locale;

public class ApiError extends Throwable{
    private int statusCode;
    private String message;

    public ApiError(int statusCode, String message) {
        super(String.format(Locale.getDefault(), "%s: %d - %s", App.getStringRes(R.string.api_error_text), statusCode, message));

        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return String.format(Locale.getDefault(), "%s %d : %s", App.getStringRes(R.string.error_text), statusCode, message);
    }}
