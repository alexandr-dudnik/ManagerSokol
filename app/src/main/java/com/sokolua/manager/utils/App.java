package com.sokolua.manager.utils;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.components.AppComponent;
import com.sokolua.manager.di.components.DaggerAppComponent;
import com.sokolua.manager.di.modules.AppModule;
import com.sokolua.manager.di.modules.PicassoCacheModule;
import com.sokolua.manager.di.modules.RootModule;
import com.sokolua.manager.mortar.ScreenScoper;
import com.sokolua.manager.ui.activities.DaggerRootActivity_RootComponent;
import com.sokolua.manager.ui.activities.RootActivity;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
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

        MultiDex.install(this);

        Fabric.with(this, new Crashlytics());

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

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



        Realm.init(sContext);
        final RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("sokol.manager.realm")
                .compactOnLaunch()
                .schemaVersion(1)
                //.migration(new RealmMigrations())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);

        ScreenScoper.registerScope(mRootScope);
        ScreenScoper.registerScope(mRootActivityScope);

    }

    @Override
    public void onTerminate() {
        Realm.getDefaultInstance().close();
        super.onTerminate();
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

    public static String getStringRes(int res_id){
       return sContext.getResources().getString(res_id);
    }
    

    public static int getColorRes(int res_id){
        return ResourcesCompat.getColor(sContext.getResources(), res_id, sContext.getTheme());
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}