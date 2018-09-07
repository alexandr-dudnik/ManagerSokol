package com.sokolua.manager.data.network;

import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.network.req.SendNoteReq;
import com.sokolua.manager.data.network.req.SendOrderReq;
import com.sokolua.manager.data.network.req.UserLoginReq;
import com.sokolua.manager.data.network.res.CustomerRes;
import com.sokolua.manager.data.network.res.GoodGroupRes;
import com.sokolua.manager.data.network.res.GoodItemRes;
import com.sokolua.manager.data.network.res.OrderRes;
import com.sokolua.manager.data.network.res.SendNewObjectRes;
import com.sokolua.manager.data.network.res.UserRes;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RestService {
    @POST("auth")
    Observable<Response<UserRes>> loginUser(@Body UserLoginReq userLoginReq);

    @GET("groups")
    Observable<Response<List<GoodGroupRes>>> getGoodsGroupList(@Header(ConstantManager.HEADER_TOKEN)String token);

    @GET("groups/{group_id}")
//    Observable<Response<GoodGroupRes>> getGoodsGroup(@Header(ConstantManager.HEADER_TOKEN)String token, @Path("group_id")String groupId);
    Call<GoodGroupRes> getGoodsGroup(@Header(ConstantManager.HEADER_TOKEN)String token, @Path("group_id")String groupId);

    @GET("goods")
    Observable<Response<List<GoodItemRes>>> getGoodsList(@Header(ConstantManager.HEADER_TOKEN)String token);

    @GET("goods/{good_id}")
//    Observable<Response<GoodItemRes>> getGoodItem(@Header(ConstantManager.HEADER_TOKEN)String token, @Path("good_id")String goodId);
    Call<GoodItemRes> getGoodItem(@Header(ConstantManager.HEADER_TOKEN)String token, @Path("good_id")String goodId);


    @GET("customers")
    Observable<Response<List<CustomerRes>>> getCustomerList(@Header(ConstantManager.HEADER_TOKEN)String token);

    @GET("customers/{customer_id}")
//    Observable<Response<CustomerRes>> getCustomer(@Header(ConstantManager.HEADER_TOKEN)String token, @Path("customer_id")String customerId);
    Call<CustomerRes> getCustomer(@Header(ConstantManager.HEADER_TOKEN)String token, @Path("customer_id")String customerId);

    @PUT("customers/{customer_id}/notes")
    Observable<Response<SendNewObjectRes>> sendNote(@Header(ConstantManager.HEADER_TOKEN)String token, @Path("customer_id")String customerId, @Body SendNoteReq noteReq);



    @GET("orders")
    Observable<Response<List<OrderRes>>> getOrderList(@Header(ConstantManager.HEADER_TOKEN)String token);

    @PUT("orders")
    Observable<Response<SendNewObjectRes>> sendOrder(@Header(ConstantManager.HEADER_TOKEN)String token, @Body SendOrderReq orderReq);

    @GET("orders/{order_id}")
//    Observable<Response<CustomerRes>> getCustomer(@Header(ConstantManager.HEADER_TOKEN)String token, @Path("order_id")String order_id);
    Call<OrderRes> getOrder(@Header(ConstantManager.HEADER_TOKEN)String token, @Path("order_id")String order_id);
}
