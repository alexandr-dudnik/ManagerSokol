package com.sokolua.manager.ui.screens.auth;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.mvp.views.IAuthView;

import butterknife.BindView;
import butterknife.OnClick;


public class AuthView extends AbstractView<AuthScreen.Presenter> implements IAuthView {

    @BindView(R.id.login_btn)       Button mLoginBtn;
    @BindView(R.id.user_name)       EditText mUserName;
    @BindView(R.id.user_password)   EditText mUserPassword;


    public AuthView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<AuthScreen.Component>getDaggerComponent(context).inject(this);
        }
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }



    public void setUserName(String userName) {
        this.mUserName.setText(userName);
    }

    public void setUserPassword(String userPassword) {
        this.mUserPassword.setText(userPassword);
    }


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
    public void login_error(){
        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_animation);
        mLoginBtn.startAnimation(shake);
    }

    //endregion ===================== IAuthView =========================
}
