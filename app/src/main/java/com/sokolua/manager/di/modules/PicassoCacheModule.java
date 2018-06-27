package com.sokolua.manager.di.modules;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.ui.activities.RootActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class PicassoCacheModule {
    @Provides
    @DaggerScope(RootActivity.class)
    Picasso providePicasso(Context context){
        OkHttp3Downloader okHttpDownloader = new OkHttp3Downloader(context, Integer.MAX_VALUE);
        Picasso picasso = new Picasso.Builder(context)
                .downloader(okHttpDownloader)
                //.debugging(true)
                .indicatorsEnabled(true)
                .build();
        Picasso.setSingletonInstance(picasso);
        return picasso;
    }
}
