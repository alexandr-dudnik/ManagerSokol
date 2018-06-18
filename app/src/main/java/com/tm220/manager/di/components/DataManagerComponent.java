package com.tm220.manager.di.components;


import com.tm220.manager.data.managers.DataManager;
import com.tm220.manager.di.modules.LocalModule;
import com.tm220.manager.di.modules.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(dependencies = AppComponent.class, modules = {LocalModule.class, NetworkModule.class})
@Singleton
public interface DataManagerComponent {
    void inject(DataManager dataManager);
}
