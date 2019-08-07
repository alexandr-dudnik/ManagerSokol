package com.sokolua.manager.mvp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sokolua.manager.mvp.presenters.AbstractPresenter;

import javax.inject.Inject;

import butterknife.ButterKnife;

public abstract class AbstractView <P extends AbstractPresenter> extends FrameLayout implements IView{
    @Inject
    protected P mPresenter;

    public AbstractView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()){
            initDagger(context);
        }
    }

    protected abstract void initDagger(Context context);

    //region ===================== Life Cycle =========================

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()){
            mPresenter.takeView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode()){
            mPresenter.dropView(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
    //endregion ================== Life Cycle =========================


}
