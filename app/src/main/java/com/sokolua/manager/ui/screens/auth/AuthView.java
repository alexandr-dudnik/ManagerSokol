package com.sokolua.manager.ui.screens.auth;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;

import com.sokolua.manager.R;
import com.sokolua.manager.databinding.ScreenAuthBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.mvp.views.IAuthView;
import com.sokolua.manager.ui.custom_views.OnSpinItemSelectedListener;

import java.util.List;

import kotlin.Unit;

public class AuthView extends AbstractView<AuthScreen.Presenter, ScreenAuthBinding> implements IAuthView {

    public AuthView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ScreenAuthBinding bindView(View view) {
        return ScreenAuthBinding.bind(view);
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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        binding.loginForm.loginBtn.setOnClickListener(view -> mPresenter.clickOnLogin());
        binding.loginForm.serverName.setOnItemSelectedListener(
                new OnSpinItemSelectedListener(
                        () -> {
                            mPresenter.updateServer(binding.loginForm.serverName.getSelectedItem().toString());
                            return Unit.INSTANCE;
                        }
                )
        );
    }

    public void setUserName(String userName) {
        binding.loginForm.userName.setText(userName);
    }

    public void setUserPassword(String userPassword) {
        binding.loginForm.userPassword.setText(userPassword);
    }

    public void setServerList(List<String> servers, String currentServer) {
        int idx = servers.indexOf(currentServer);
        binding.loginForm.serverName.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.server_item, servers));
        binding.loginForm.serverName.setSelection(Math.max(idx, 0));
    }


    //region ===================== IAuthView =========================
    @Override
    public String getUserName() {
        return binding.loginForm.userName.getText().toString();
    }

    @Override
    public String getUserPassword() {
        return binding.loginForm.userPassword.getText().toString();
    }

    @Override
    public void showInvalidUserName() {
        binding.loginForm.userName.setError(getContext().getString(R.string.error_empty_login));
    }

    @Override
    public void showInvalidPassword() {
        binding.loginForm.userPassword.setError(getContext().getString(R.string.error_bad_password));
    }

    @Override
    public void login_error() {
        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_animation);
        binding.loginForm.loginBtn.startAnimation(shake);
    }

    //endregion ===================== IAuthView =========================
}
