package com.sokolua.manager.utils;

import android.app.Application;
import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;

import com.sokolua.manager.BuildConfig;
import com.sokolua.manager.data.managers.DebugModule;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.components.AppComponent;
import com.sokolua.manager.di.components.DaggerAppComponent;
import com.sokolua.manager.di.modules.AppModule;
import com.sokolua.manager.di.modules.PicassoCacheModule;
import com.sokolua.manager.di.modules.RootModule;
import com.sokolua.manager.mortar.ScreenScoper;
import com.sokolua.manager.ui.activities.DaggerRootActivity_RootComponent;
import com.sokolua.manager.ui.activities.RootActivity;

import java.text.ParseException;

import io.realm.Realm;
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


        Realm.init(this);

        if (BuildConfig.DEBUG) {
            try {
                DebugModule.mock_RealmDB();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


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

    public static String getStringRes(int res_id){
       return sContext.getResources().getString(res_id);
    }
    

    public static int getColorRes(int res_id){
        return ResourcesCompat.getColor(sContext.getResources(), res_id, sContext.getTheme());
    }

}