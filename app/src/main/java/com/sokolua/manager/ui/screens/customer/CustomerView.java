package com.sokolua.manager.ui.screens.customer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;

import javax.inject.Inject;

public class CustomerView extends AbstractView<CustomerScreen.Presenter> {

    @Inject
    CustomerScreen.Presenter mPresenter;

    public CustomerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<CustomerScreen.Component>getDaggerComponent(context).inject(this);
        }


    }

    @Override
    public boolean viewOnBackPressed() {
        return false ;
    }

}
