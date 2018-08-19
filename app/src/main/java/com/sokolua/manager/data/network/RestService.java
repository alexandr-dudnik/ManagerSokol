package com.sokolua.manager.data.network;

import com.sokolua.manager.data.network.req.UserLoginReq;
import com.sokolua.manager.data.network.res.UserRes;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RestService {
    @POST("auth/")
    Observable<Response<UserRes>> loginUser(@Body UserLoginReq userLoginReq);
}
