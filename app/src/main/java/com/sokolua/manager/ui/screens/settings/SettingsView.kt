package com.sokolua.manager.ui.screens.settings

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import com.sokolua.manager.BuildConfig
import com.sokolua.manager.R
import com.sokolua.manager.databinding.ScreenSettingsBinding
import com.sokolua.manager.di.DaggerService
import com.sokolua.manager.mvp.views.AbstractView
import com.sokolua.manager.mvp.views.IAuthView
import com.sokolua.manager.ui.custom_views.OnSpinItemSelectedListener

class SettingsView(context: Context, attrs: AttributeSet?) :
    AbstractView<SettingsScreen.Presenter, ScreenSettingsBinding>(context, attrs), IAuthView {

    override fun initDagger(context: Context) {
        if (!isInEditMode) {
            DaggerService.getDaggerComponent<SettingsScreen.Component>(context).inject(this)
        }
    }

    override fun bindView(view: View) = ScreenSettingsBinding.bind(view)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val ver = resources.getString(R.string.version) + ": " + BuildConfig.VERSION_NAME
        with(binding) {
            tvAppVersion.text = ver
            loginForm.serverName.onItemSelectedListener = OnSpinItemSelectedListener {
                mPresenter.updateServer(loginForm.serverName.selectedItem.toString())
            }
            autoSyncSwitch.setOnCheckedChangeListener { _, isChecked ->
                mPresenter.updateAutoSynchronize(isChecked)
            }
            loginForm.loginBtn.setOnClickListener {
                mPresenter.checkAuth()
            }
        }
    }

    override fun viewOnBackPressed(): Boolean {
        return false
    }

    fun setServerList(servers: List<String>, currentServer: String) {
        val idx = servers.indexOf(currentServer).coerceAtLeast(0)
        with(binding.loginForm.serverName) {
            adapter = ArrayAdapter(this.context, R.layout.server_item, servers)
            setSelection(idx)
        }
    }


    //region ===================== IAuthView =========================
    override fun getUserName(): String = binding.loginForm.userName.text.toString()

    override fun getUserPassword(): String = binding.loginForm.userPassword.text.toString()

    override fun showInvalidUserName() {
        binding.loginForm.userName.error = context.getString(R.string.error_empty_login)
    }

    override fun showInvalidPassword() {
        binding.loginForm.userPassword.error = context.getString(R.string.error_bad_password)
    }

    override fun login_error() {
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake_animation)
        binding.loginForm.loginBtn.startAnimation(shake)
    }

    //endregion ===================== IAuthView =========================

    //region ===================== Synchronize =========================
    fun setAutoSynchronize(autoSynchronize: Boolean) {
        binding.autoSyncSwitch.isChecked = autoSynchronize
    }

    fun setUserName(name: String) {
        binding.loginForm.userName.setText(name)
    }

    fun setUserPassword(password: String) {
        binding.loginForm.userPassword.setText(password)
    }
    //endregion ================== Synchronize =========================

}
