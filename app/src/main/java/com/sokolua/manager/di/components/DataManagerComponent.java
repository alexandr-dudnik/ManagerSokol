package com.sokolua.manager.di.components;


import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.managers.PreferencesManager;
import com.sokolua.manager.data.managers.RealmManager;
import com.sokolua.manager.di.modules.LocalModule;
import com.sokolua.manager.di.modules.NetworkModule;
import com.sokolua.manager.utils.App;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Provides;

@Component(dependencies = AppComponent.class, modules = {LocalModule.class, NetworkModule.class})
@Singleton
public interface DataManagerComponent {
    void inject(DataManager dataManager);
    void inject(RealmManager realmManager);
}
