package com.sokolua.manager.ui.screens.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.mvp.views.IAuthView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class SettingsView extends AbstractView<SettingsScreen.Presenter> implements IAuthView{

    @BindView(R.id.auto_sync_switch)        Switch mAutoSyncSwitch;
    @BindView(R.id.user_name)               EditText mUserName;
    @BindView(R.id.user_password)           EditText mUserPassword;
    @BindView(R.id.login_btn)               Button mLoginBtn;
    @BindView(R.id.server_name)             Spinner mServerName;


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

    public void setServerList(String[] servers, String currentServer){
        mServerName.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.server_item, servers));
        List<String> list = Arrays.asList(servers);
        int idx = list.indexOf(currentServer);
        mServerName.setSelection(idx<0?0:idx);
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
    @OnItemSelected(R.id.server_name)
    void serverChange(View view){
        mPresenter.updateServer(mServerName.getSelectedItem().toString());
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
