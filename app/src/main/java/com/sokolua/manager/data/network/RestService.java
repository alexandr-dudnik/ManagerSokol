package com.sokolua.manager.data.network;

import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.network.req.UserLoginReq;
import com.sokolua.manager.data.network.res.GoodGroupRes;
import com.sokolua.manager.data.network.res.UserRes;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RestService {
    @POST("auth")
    Observable<Response<UserRes>> loginUser(@Body UserLoginReq userLoginReq);

    @GET("groups")
    Observable<Response<List<GoodGroupRes>>> getGoodsGroupList(@Header(ConstantManager.HEADER_TOKEN)String token);
}
