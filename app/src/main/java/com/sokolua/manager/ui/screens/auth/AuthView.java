package com.sokolua.manager.ui.screens.auth;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.mvp.views.IAuthView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;


public class AuthView extends AbstractView<AuthScreen.Presenter> implements IAuthView {

    @BindView(R.id.login_btn)       Button mLoginBtn;
    @BindView(R.id.user_name)       TextInputEditText mUserName;
    @BindView(R.id.user_password)   TextInputEditText mUserPassword;
    @BindView(R.id.server_name)     Spinner mServerName;


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

    public void setServerList(List<String> servers, String currentServer){
        mServerName.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.server_item, servers));
        int idx = servers.indexOf(currentServer);
        mServerName.setSelection(Math.max(idx, 0));
    }

    //region ===================== Events =========================


    @OnClick(R.id.login_btn)
    void loginClick(){
        mPresenter.clickOnLogin();
    }

    @OnItemSelected(R.id.server_name)
    void serverChange(View view){
        mPresenter.updateServer(mServerName.getSelectedItem().toString());
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
