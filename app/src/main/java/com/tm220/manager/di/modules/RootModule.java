package com.tm220.manager.di.modules;


import com.tm220.manager.di.scopes.DaggerScope;
import com.tm220.manager.mvp.presenters.RootPresenter;
import com.tm220.manager.ui.activities.RootActivity;

import dagger.Provides;

//import com.skill_branch.graduate.mvp.models.AccountModel;


@dagger.Module
public class RootModule {
    @Provides
    @DaggerScope(RootActivity.class)
    RootPresenter provideRootPresenter() {
        return new RootPresenter();
    }

//    @Provides
//    @RootScope
//    AccountModel provideAccountModel(){
//        return new AccountModel();
//    }
}
