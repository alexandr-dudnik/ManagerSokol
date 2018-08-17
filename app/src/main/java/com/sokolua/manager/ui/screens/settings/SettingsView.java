package com.sokolua.manager.ui.screens.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.mvp.views.IAuthView;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class SettingsView extends AbstractView<SettingsScreen.Presenter> implements IAuthView{

    @BindView(R.id.server_address_text)     EditText mServerAddress;
    @BindView(R.id.auto_sync_switch)        Switch mAutoSyncSwitch;
    @BindView(R.id.user_name)               EditText mUserName;
    @BindView(R.id.user_password)           EditText mUserPassword;
    @BindView(R.id.login_btn)               Button mLoginBtn;

    public SettingsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<SettingsScreen.Component>getDaggerComponent(context).inject(this);
        }


    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public boolean viewOnBackPressed() {
        return false ;
    }


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

    //region ===================== Synchronize =========================

    public void setServerAddress(String serverAddress) {
        mServerAddress.setText(serverAddress);
    }

    public void setAutoSynchronize(Boolean autoSynchronize) {
        mAutoSyncSwitch.setChecked(autoSynchronize);
    }

    public void setUserName(String name) {
        mUserName.setText(name);
    }

    public void setUserPassword(String password) {
        mUserPassword.setText(password);
    }

    //endregion ================== Synchronize =========================



    //region ===================== Events =========================

    @OnTextChanged(R.id.server_address_text)
    void serverAddressChanged(CharSequence s, int start, int count, int after){
        mPresenter.updateServerAddress(mServerAddress.getText().toString());
    }

    @OnCheckedChanged(R.id.auto_sync_switch)
    void serverSyncChanged(CompoundButton buttonView, boolean isChecked){
        mPresenter.updateAutoSynchronize(mAutoSyncSwitch.isChecked());
    }

    @OnClick(R.id.login_btn)
    void authBtnClick(View v){
        mPresenter.checkAuth();
    }
    //endregion ================== Events =========================


}
