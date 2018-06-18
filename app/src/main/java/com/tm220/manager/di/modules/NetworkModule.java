package com.tm220.manager.di.modules;

import dagger.Module;

@Module
public class NetworkModule {
//
//    @Provides
//    @Singleton
//    OkHttpClient provideOkHttpClient(){
//        return createClient();
//    }


//    @Provides
//    @Singleton
//    Retrofit provideRetrofit(OkHttpClient client){
//        return createRetrofit(client);
//    }
//
//    @Provides
//    @Singleton
//    RestService provideRestService(Retrofit retrofit){
//        return retrofit.create(RestService.class);
//    }
//
//    private Retrofit createRetrofit(OkHttpClient client) {
//        return new Retrofit.Builder()
//                .baseUrl(AppConfig.BASE_URL)
//                .addConverterFactory(createConvertFactory())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .client(client)
//                .build();
//    }
//
//
//    private OkHttpClient createClient() {
//        return new OkHttpClient.Builder()
//                //.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//                .connectTimeout(AppConfig.MAX_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
//                .readTimeout(AppConfig.MAX_READ_TIMEOUT, TimeUnit.MILLISECONDS)
//                .writeTimeout(AppConfig.MAX_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
//                .build();
//
//    }
//
//    private Converter.Factory createConvertFactory() {
//        return MoshiConverterFactory.create(new Moshi.Builder().build());
//    }
}
