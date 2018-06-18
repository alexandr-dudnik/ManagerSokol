package com.tm220.manager.di.components;

import com.tm220.manager.di.modules.ModelModule;
import com.tm220.manager.mvp.models.AbstractModel;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = ModelModule.class)
@Singleton
public interface ModelComponent {
    void inject(AbstractModel abstractModel);
}
