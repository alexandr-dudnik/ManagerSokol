package com.sokolua.manager.di.components;

import com.sokolua.manager.di.modules.ModelModule;
import com.sokolua.manager.mvp.models.AbstractModel;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = ModelModule.class)
@Singleton
public interface ModelComponent {
    void inject(AbstractModel abstractModel);
}
