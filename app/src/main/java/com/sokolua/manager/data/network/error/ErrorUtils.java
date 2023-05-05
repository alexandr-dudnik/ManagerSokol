package com.sokolua.manager.data.network.error;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.utils.App;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;

public class ErrorUtils {
    public static ApiError parseError(Response<?> response){
        ApiError error;

        if (DataManager.getInstance().isRetrofitValid()) {
            Retrofit retrofit = DataManager.getInstance().getRetrofit();
            try {
                error = (ApiError) retrofit
                        .responseBodyConverter(ApiError.class, ApiError.class.getAnnotations())
                        .convert(response.errorBody());
            } catch (IOException e) {
                e.printStackTrace();
                return customError(response.code(), response.message());
            }
        } else {
            return customError(0, App.getStringRes(R.string.configuration_error));
        }

        return error;
    }

    public static ApiError customError(int statusCode, String message){
        return new ApiError(statusCode, message);
    }
}
