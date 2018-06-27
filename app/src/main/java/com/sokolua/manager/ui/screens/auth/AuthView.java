package com.sokolua.manager.ui.screens.auth;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.IAuthView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import flow.Flow;


public class AuthView extends RelativeLayout implements IAuthView {
    @Inject
    AuthScreen.AuthPresenter mPresenter;

    private AuthScreen mScreen;

    @BindView(R.id.login_btn)
    Button mLoginBtn;

    @BindView(R.id.user_name)
    EditText mUserName;

    @BindView(R.id.user_password)
    EditText mUserPassword;


    public AuthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            mScreen = Flow.getKey(this);
            DaggerService.<AuthScreen.Component>getDaggerComponent(context).inject(this);
        }
    }

    public void login_error(){
        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_animation);
        mLoginBtn.startAnimation(shake);
    }

    //region ===================== Life Cycle =========================

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            mPresenter.takeView(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode()) {
            mPresenter.dropView(this);
        }
    }

    //endregion ================== Life Cycle =========================


    //region ===================== Events =========================


    @OnClick(R.id.login_btn)
    void loginClick(){
        mPresenter.clickOnLogin();
    }

    //endregion ================== Events =========================



    //region ===================== IAuthView =========================
    @Override
    public String getUserName() {
        return mUserName.getText().toString();
    }

    @Override
    public String getUserPassword() {
        return mUserPassword.getText().toString();
    }

    @Override
    public void showInvalidUserName() {
        mUserName.setError(getContext().getString(R.string.error_empty_login));
    }

    @Override
    public void showInvalidPassword() {
        mUserPassword.setError(getContext().getString(R.string.error_bad_password));
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }
    //endregion ===================== IAuthView =========================
}
