package com.sokolua.manager.ui.screens.cust_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;

public class CustomerListView extends AbstractView<CustomerListScreen.Presenter> {
    public CustomerListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<CustomerListScreen.Component>getDaggerComponent(context).inject(this);
        }
    }

    @Override
    public boolean viewOnBackPressed() {
        return false ;
    }


}
