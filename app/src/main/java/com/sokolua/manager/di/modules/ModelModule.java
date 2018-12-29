package com.sokolua.manager.di.modules;


import com.sokolua.manager.data.managers.DataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ModelModule{
    @Provides
    @Singleton
    DataManager provideDataManager(){
        return DataManager.getInstance();
    }



}
