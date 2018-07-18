package com.sokolua.manager.di.modules;

import android.content.Context;


import com.sokolua.manager.data.managers.PreferencesManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LocalModule {
    @Provides
    @Singleton
    PreferencesManager providePreferencesManager(Context context){
        return new PreferencesManager(context);
    }

//    @Provides
//    @Singleton
//    RealmManager provideRealmManager(Context context) {
//        return new RealmManager();
//    }
}
