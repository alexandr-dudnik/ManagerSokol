package com.sokolua.manager.ui.screens.routes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.utils.ReactiveRecyclerAdapter;

import javax.inject.Inject;

public class RoutesView extends AbstractView<RoutesScreen.Presenter> {
    @Inject
    RoutesScreen.Presenter mPresenter;


    public RoutesView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<RoutesScreen.Component>getDaggerComponent(context).inject(this);
        }


    }

    @Override
    public boolean viewOnBackPressed() {
        return false ;
    }


    public void setAdapter(ReactiveRecyclerAdapter mAdapter) {
    }

}
