package com.sokolua.manager.di.components;


import com.squareup.picasso.Picasso;
import com.sokolua.manager.di.modules.PicassoCacheModule;
import com.sokolua.manager.di.scopes.DaggerScope;
import com.sokolua.manager.ui.activities.RootActivity;

import dagger.Component;

@Component(dependencies = AppComponent.class, modules = PicassoCacheModule.class)
@DaggerScope(RootActivity.class)
public interface PicassoComponent {
    Picasso getPicasso();
}
