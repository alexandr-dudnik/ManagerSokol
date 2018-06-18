package com.tm220.manager.di.components;


import com.squareup.picasso.Picasso;
import com.tm220.manager.di.modules.PicassoCacheModule;
import com.tm220.manager.di.scopes.DaggerScope;
import com.tm220.manager.ui.activities.RootActivity;

import dagger.Component;

@Component(dependencies = AppComponent.class, modules = PicassoCacheModule.class)
@DaggerScope(RootActivity.class)
public interface PicassoComponent {
    Picasso getPicasso();
}
