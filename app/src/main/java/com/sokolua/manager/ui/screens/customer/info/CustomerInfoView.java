package com.sokolua.manager.ui.screens.customer.info;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;

public class CustomerInfoView extends AbstractView<CustomerInfoScreen.Presenter>{
    public CustomerInfoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CustomerInfoScreen.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }
}
