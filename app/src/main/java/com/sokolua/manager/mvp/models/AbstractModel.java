package com.sokolua.manager.mvp.models;


import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.di.components.DaggerModelComponent;
import com.sokolua.manager.di.components.ModelComponent;
import com.sokolua.manager.di.modules.ModelModule;

import javax.inject.Inject;

public abstract class AbstractModel {
    @Inject
    DataManager mDataManager;
//    @Inject
//    JobManager mJobManager;

    public AbstractModel() {
        ModelComponent component= DaggerService.getComponent(ModelComponent.class);
        if (component==null){
            component = createDaggerComponent();
            DaggerService.registerComponent(ModelComponent.class, component);
        }
        component.inject(this);
    }

    AbstractModel(DataManager dataManager){
        mDataManager = dataManager;
//        mJobManager = jobManager;
    }

    private ModelComponent createDaggerComponent() {
        return DaggerModelComponent.builder()
                .modelModule(new ModelModule())
                .build();
    }

    public DataManager getDataManager() {
        return mDataManager;
    }


//    public JobManager getJobManager() {
//        return mJobManager;
//    }
}
