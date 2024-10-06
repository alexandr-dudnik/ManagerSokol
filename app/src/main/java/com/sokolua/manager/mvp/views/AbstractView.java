package com.sokolua.manager.mvp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.sokolua.manager.mvp.presenters.AbstractPresenter;

import javax.inject.Inject;

public abstract class AbstractView<P extends AbstractPresenter, B extends ViewBinding> extends FrameLayout implements IView {
    @Inject
    protected P mPresenter;
    protected B binding;

    public AbstractView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            initDagger(context);
        }
    }

    protected abstract B bindView(View view);

    protected abstract void initDagger(Context context);

    //region ===================== Life Cycle =========================

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            binding = bindView(this);
            mPresenter.takeView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode()) {
            mPresenter.dropView(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
    //endregion ================== Life Cycle =========================


}
