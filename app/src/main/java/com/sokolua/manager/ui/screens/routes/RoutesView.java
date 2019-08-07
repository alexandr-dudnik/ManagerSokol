package com.sokolua.manager.ui.screens.routes;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

public class RoutesView extends AbstractView<RoutesScreen.Presenter> {
    @BindView(R.id.customer_list)    RecyclerView mCustomerList;
    @BindViews({R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7}) List<AppCompatButton> mButtons;

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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int dw = cal.get(Calendar.DAY_OF_WEEK)-cal.getFirstDayOfWeek();
        dw = dw<0?(7+dw):dw;
        dayClick(mButtons.get(dw));
    }

    @Override
    public boolean viewOnBackPressed() {
        return false ;
    }


    public void setAdapter(ReactiveRecyclerAdapter mAdapter) {
        mCustomerList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL,false));
        mCustomerList.setAdapter(mAdapter);
    }

    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7})
    void dayClick(AppCompatButton view){
        mPresenter.daySelected(mButtons.indexOf(view));
        for (Button btn : mButtons){
            if (btn.equals(view)){
                ViewCompat.setBackgroundTintList(btn, ContextCompat.getColorStateList(getContext(), R.color.color_accent));
            }else{
                ViewCompat.setBackgroundTintList(btn, ContextCompat.getColorStateList(getContext(), R.color.color_gray_light));
            }
        }
    }
}
