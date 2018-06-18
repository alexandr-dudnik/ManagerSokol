package com.tm220.manager.mvp.models;



import javax.inject.Inject;

public abstract class AbstractModel {
//    @Inject
//    DataManager mDataManager;

    public AbstractModel() {
//        ModelComponent component= DaggerService.getComponent(ModelComponent.class);
//        if (component==null){
//            component = createDaggerComponent();
//            DaggerService.registerComponent(ModelComponent.class, component);
//        }
//        component.inject(this);
    }

//    public AbstractModel(DataManager dataManager, JobManager jobManager) {
//        mDataManager = dataManager;
//        mJobManager = jobManager;
//    }

//    private ModelComponent createDaggerComponent() {
//        return DaggerModelComponent.builder()
//                .modelModule(new ModelModule())
//                .build();
//    }
//
//    public DataManager getDataManager() {
//        return mDataManager;
//    }
//
//    public JobManager getJobManager() {
//        return mJobManager;
//    }
}
