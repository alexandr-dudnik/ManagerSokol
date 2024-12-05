package com.sokolua.manager.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.hardware.Camera;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.sokolua.manager.data.storage.realm.RealmMigrations;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.components.AppComponent;
import com.sokolua.manager.di.components.DaggerAppComponent;
import com.sokolua.manager.di.modules.AppModule;
import com.sokolua.manager.di.modules.RootModule;
import com.sokolua.manager.mortar.ScreenScoper;
import com.sokolua.manager.services.UpdateService;
import com.sokolua.manager.ui.activities.DaggerRootActivity_RootComponent;
import com.sokolua.manager.ui.activities.RootActivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

public class App extends Application {
    public static AppComponent sAppComponent;
    @SuppressLint("StaticFieldLeak")
    private static Context sContext;
    private MortarScope mRootScope;
    private MortarScope mRootActivityScope;
    private static RootActivity.RootComponent mRootActivityRootComponent;
    private static Camera cam;

    public static Camera getCameraInstance() {
        if (cam != null) return cam;
        try {
            cam = Camera.open(); // attempt to get a Camera instance
            cam.setDisplayOrientation(90);
        } catch (Throwable ignore) {
        }
        return cam; // returns null if camera is unavailable
    }

    public static void releaseCamera() {
        if (cam != null) {
            try {
                cam.release();
                cam = null;
            } catch (Throwable ignore) {
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

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
                .schemaVersion(3)
                .migration(new RealmMigrations())
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(configuration);

        ScreenScoper.registerScope(mRootScope);
        ScreenScoper.registerScope(mRootActivityScope);

        startUpdateService();

        WorkManager.initialize(
                this.getApplicationContext(),
                new Configuration.Builder()
                        .build()
        );
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
                .build();
    }

    public static RootActivity.RootComponent getRootActivityRootComponent() {
        return mRootActivityRootComponent;
    }

    public static Context getContext() {
        return sContext;
    }

    public static String getStringRes(int res_id) {
        return sContext.getResources().getString(res_id);
    }


    public static int getColorRes(int res_id) {
        return ResourcesCompat.getColor(sContext.getResources(), res_id, sContext.getTheme());
    }

    public static boolean checkUpdateServiceRunning() {
        if (sContext != null) {
            final ActivityManager manager = (ActivityManager) sContext.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (service.getClass().getName().equals(UpdateService.class.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void startUpdateService() {
//        final PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(UpdateWorker.class, 15, TimeUnit.MINUTES);
//
//        WorkManager.initialize(
//                this,
//                new Configuration.Builder()
//                        .setMinimumLoggingLevel(android.util.Log.INFO)
//                        .setExecutor(Executors.newFixedThreadPool(8))
//                        .build());
//
//        WorkManager.getInstance(this)
//                .enqueueUniquePeriodicWork(
//                        "UpdateWork",
//                        ExistingPeriodicWorkPolicy.KEEP,
//                        workBuilder.build()
//                );
    }
}
