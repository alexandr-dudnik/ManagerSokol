package com.sokolua.manager.ui.screens.customer.tasks;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;

import butterknife.BindView;

public class CustomerTasksView extends AbstractView<CustomerTasksScreen.Presenter>{
    @BindView(R.id.customer_name_text)
    TextView mCustomerNameText;

    public CustomerTasksView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CustomerTasksScreen.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!isInEditMode()) {
            mPresenter.updateFields();
        }
    }


    public void setCustomerNameText(String name) {
        mCustomerNameText.setText(name);
    }
}
