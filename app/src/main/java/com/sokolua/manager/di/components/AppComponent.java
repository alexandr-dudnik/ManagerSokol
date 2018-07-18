package com.sokolua.manager.di.components;



import android.content.Context;


import com.sokolua.manager.di.modules.AppModule;

import dagger.Component;

@Component(modules = AppModule.class)
public interface AppComponent {
    Context getContext();
}
