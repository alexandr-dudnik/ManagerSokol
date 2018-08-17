package com.sokolua.manager.di.modules;


import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.mvp.models.AuthModel;
import com.sokolua.manager.mvp.presenters.RootPresenter;
import com.sokolua.manager.ui.activities.RootActivity;

import dagger.Provides;

//import com.skill_branch.graduate.mvp.models.AccountModel;


@dagger.Module
public class RootModule {
    @Provides
    @DaggerScope(RootActivity.class)
    RootPresenter provideRootPresenter() {
        return new RootPresenter();
    }

    @Provides
    AuthModel provideAccountModel(){
        return new AuthModel();
    }
}
