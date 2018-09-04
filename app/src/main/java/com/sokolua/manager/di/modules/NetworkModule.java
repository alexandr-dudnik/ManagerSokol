package com.sokolua.manager.di.modules;

import com.sokolua.manager.data.network.RestService;
import com.sokolua.manager.utils.AppConfig;
import com.squareup.moshi.Moshi;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
public class NetworkModule {

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(){
        return createClient();
    }


    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient client){
        return createRetrofit(client);
    }

    @Provides
    @Singleton
    RestService provideRestService(Retrofit retrofit){
        return retrofit.create(RestService.class);
    }

    private Retrofit createRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(createConvertFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }




    private OkHttpClient createClient() {
        final Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(AppConfig.MAX_CONCURRENT_REQUESTS);

        ConnectionPool connectionPool = new ConnectionPool();

        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(AppConfig.MAX_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .dispatcher(dispatcher)
                .readTimeout(AppConfig.MAX_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(AppConfig.MAX_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectionPool(connectionPool)
              .build();

    }

    private Converter.Factory createConvertFactory() {
        return MoshiConverterFactory.create(new Moshi.Builder().build());
    }
}
