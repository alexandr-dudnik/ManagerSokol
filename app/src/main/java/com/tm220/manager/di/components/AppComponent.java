package com.tm220.manager.di.components;



import android.content.Context;


import com.tm220.manager.di.modules.AppModule;

import dagger.Component;

@Component(modules = AppModule.class)
public interface AppComponent {
    Context getContext();
}
