package com.tm220.manager.data.managers;

import com.tm220.manager.di.DaggerService;
import com.tm220.manager.di.components.DaggerDataManagerComponent;
import com.tm220.manager.di.components.DataManagerComponent;
import com.tm220.manager.di.modules.LocalModule;
import com.tm220.manager.di.modules.NetworkModule;
import com.tm220.manager.utils.App;

public class DataManager {
    private static DataManager ourInstance;
    private boolean userAuth=false;

    private DataManager() {
        DataManagerComponent dmComponent = DaggerService.getComponent(DataManagerComponent.class);
        if (dmComponent==null){
            dmComponent = DaggerDataManagerComponent.builder()
                    .appComponent(App.getAppComponent())
                    .localModule(new LocalModule())
                    .networkModule(new NetworkModule())
                    .build();
            DaggerService.registerComponent(DataManagerComponent.class, dmComponent);
        }
        dmComponent.inject(this);

        //updateLocalDataWithTimer();
    }

    public static DataManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new DataManager();
        }
        return ourInstance;
    }


    //region ===================== UserInfo =========================

    public boolean isAuthUser() {
        //TODO check auth token in shared preferences
        return userAuth;
    }

    public void setUserAuth(boolean state){
        userAuth = state;
    }
    //endregion ================== UserInfo =========================

}

