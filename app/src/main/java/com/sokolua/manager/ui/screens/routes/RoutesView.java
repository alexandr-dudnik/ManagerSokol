package com.sokolua.manager.ui.screens.routes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sokolua.manager.R;
import com.sokolua.manager.databinding.ScreenRoutesBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.ReactiveRecyclerAdapter;
import com.sokolua.manager.utils.App;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RoutesView extends AbstractView<RoutesScreen.Presenter, ScreenRoutesBinding> {
    final private List<AppCompatButton> mButtons = new ArrayList<>();


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
    protected ScreenRoutesBinding bindView(View view) {
        return ScreenRoutesBinding.bind(view);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            mButtons.add(binding.btn1);
            mButtons.add(binding.btn2);
            mButtons.add(binding.btn3);
            mButtons.add(binding.btn4);
            mButtons.add(binding.btn5);
            mButtons.add(binding.btn6);
            mButtons.add(binding.btn7);

            OnClickListener buttonClickListener = buttonView -> {
                mPresenter.daySelected(mButtons.indexOf(buttonView));
                for (Button btn : mButtons) {
                    if (btn.equals(buttonView)) {
                        ViewCompat.setBackgroundTintList(btn, ContextCompat.getColorStateList(getContext(), R.color.color_accent));
                    } else {
                        ViewCompat.setBackgroundTintList(btn, ContextCompat.getColorStateList(getContext(), R.color.color_gray_light));
                    }
                }
            };
            for (Button btn : mButtons) {
                btn.setOnClickListener(buttonClickListener);
            }

            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            int dw = cal.get(Calendar.DAY_OF_WEEK) - cal.getFirstDayOfWeek();
            dw = dw < 0 ? (7 + dw) : dw;
            mButtons.get(dw).performClick();
        }
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public void setAdapter(ReactiveRecyclerAdapter mAdapter) {
        binding.customerList.setLayoutManager(new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.customerList.setAdapter(mAdapter);
    }

}
