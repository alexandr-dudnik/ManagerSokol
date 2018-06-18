package com.tm220.manager.utils;

import android.app.Application;
import android.content.Context;

import com.tm220.manager.di.DaggerService;
import com.tm220.manager.di.components.AppComponent;
import com.tm220.manager.di.components.DaggerAppComponent;
import com.tm220.manager.di.modules.AppModule;
import com.tm220.manager.di.modules.PicassoCacheModule;
import com.tm220.manager.di.modules.RootModule;
import com.tm220.manager.mortar.ScreenScoper;
import com.tm220.manager.ui.activities.DaggerRootActivity_RootComponent;
import com.tm220.manager.ui.activities.RootActivity;

import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

public class App extends Application {
    public static AppComponent sAppComponent;
    private static Context sContext;
    private MortarScope mRootScope;
    private MortarScope mRootActivityScope;
    private static RootActivity.RootComponent mRootActivityRootComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        createAppComponent();
        createRootActivityComponent();
        sContext = getApplicationContext();

        mRootScope = MortarScope.buildRootScope()
                .withService(DaggerService.SERVICE_NAME, sAppComponent)
                .build("Root");
        mRootActivityScope = mRootScope.buildChild()
                .withService(DaggerService.SERVICE_NAME, mRootActivityRootComponent)
                .withService(BundleServiceRunner.SERVICE_NAME, new BundleServiceRunner())
                .build(RootActivity.class.getName());

        ScreenScoper.registerScope(mRootScope);
        ScreenScoper.registerScope(mRootActivityScope);
    }

    @Override
    public Object getSystemService(String name) {
        if (mRootScope != null) {
            return mRootScope.hasService(name) ? mRootScope.getService(name) : super.getSystemService(name);
        }
        return super.getSystemService(name);


    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

    private void createAppComponent() {
        sAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(getApplicationContext()))
                .build();
    }

    private void createRootActivityComponent() {
        mRootActivityRootComponent = DaggerRootActivity_RootComponent.builder()
                .appComponent(sAppComponent)
                .rootModule(new RootModule())
                .picassoCacheModule(new PicassoCacheModule())
                .build();

    }

    public static RootActivity.RootComponent getRootActivityRootComponent() {
        return mRootActivityRootComponent;
    }

    public static Context getContext() {
        return sContext;
    }

}