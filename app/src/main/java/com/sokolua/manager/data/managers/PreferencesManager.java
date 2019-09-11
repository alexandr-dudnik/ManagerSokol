package com.sokolua.manager.data.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Keep;

import com.sokolua.manager.R;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.AppConfig;

@Keep
public class PreferencesManager {
    private static final String LAST_UPDATE_KEY = "pref_last_update";
    private static final String SERVER_ADDRESS_STRING = "pref_server_address";
    private static final String AUTO_SYNCHRONIZE = "auto_synchronize";
    private static final String USER_NAME = "user_name";
    private static final String USER_PASSWORD = "user_password";
    private static final String USER_AUTH_TOKEN = "user_auth_token";
    private static final String USER_AUTH_TOKEN_EXPIRATION = "user_auth_token_exp";
    private static final String USER_MANAGER_NAME = "manager_name";

    private final SharedPreferences mSharedPreferences;


    public PreferencesManager(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public String getLastUpdate(String module){
        return mSharedPreferences.getString(LAST_UPDATE_KEY+"_"+module, "1900-01-01 00:00:00");
    }

    public void saveLastUpdate(String module, String lastModified){
        SharedPreferences.Editor spEditor = mSharedPreferences.edit();
        spEditor.putString(LAST_UPDATE_KEY+"_"+module, lastModified);
        spEditor.apply();
    }

    public void clearLastUpdate() {
        SharedPreferences.Editor spEditor = mSharedPreferences.edit();
        for (String key : mSharedPreferences.getAll().keySet()) {
            if (key.startsWith(LAST_UPDATE_KEY)){
                spEditor.remove(key);
            }
        }
        spEditor.apply();
    }

    
    
    public String getServerAddress() {
        return mSharedPreferences.getString(SERVER_ADDRESS_STRING, AppConfig.API_SERVERS[0]);
    }

    public void updateServerAddress(String serverAddress){
        SharedPreferences.Editor spEditor = mSharedPreferences.edit();
        spEditor.putString(SERVER_ADDRESS_STRING, serverAddress);
        spEditor.apply();
    }

    public Boolean getAutoSynchronize() {
        return mSharedPreferences.getBoolean(AUTO_SYNCHRONIZE, true);
    }

    public void updateAutoSynchronize(Boolean sync) {
        SharedPreferences.Editor spEditor = mSharedPreferences.edit();
        spEditor.putBoolean(AUTO_SYNCHRONIZE, sync);
        spEditor.apply();
    }


    public String getUserName() {
        return mSharedPreferences.getString(USER_NAME, "");
    }

    public void updateUserName(String login) {
        SharedPreferences.Editor spEditor = mSharedPreferences.edit();
        spEditor.putString(USER_NAME, login);
        spEditor.apply();
    }

    public String getUserPassword() {
        return mSharedPreferences.getString(USER_PASSWORD, "");
    }

    public void updateUserPassword(String pass) {
        SharedPreferences.Editor spEditor = mSharedPreferences.edit();
        spEditor.putString(USER_PASSWORD, pass );
        spEditor.apply();
    }

    public String getUserAuthToken() {
        return mSharedPreferences.getString(USER_AUTH_TOKEN, "");
    }

    public String getUserAuthTokenExpiration() {
        return mSharedPreferences.getString(USER_AUTH_TOKEN_EXPIRATION, "1900-01-01 00:00:00");
    }

    public void updateUserAuthToken(String token, String expires) {
        SharedPreferences.Editor spEditor = mSharedPreferences.edit();
        spEditor.putString(USER_AUTH_TOKEN, token );
        spEditor.putString(USER_AUTH_TOKEN_EXPIRATION, expires.isEmpty()?"1900-01-01 00:00:00":expires );
        spEditor.apply();
    }

    public String getManagerName() {
        return mSharedPreferences.getString(USER_MANAGER_NAME, App.getStringRes(R.string.default_manager_name));
    }

    public void updateManagerName(String managerName) {
        SharedPreferences.Editor spEditor = mSharedPreferences.edit();
        spEditor.putString(USER_MANAGER_NAME, managerName );
        spEditor.apply();
    }

}
