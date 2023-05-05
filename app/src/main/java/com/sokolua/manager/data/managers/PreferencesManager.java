package com.sokolua.manager.data.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Keep;

import com.sokolua.manager.R;
import com.sokolua.manager.utils.App;
import com.sokolua.manager.utils.AppConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
    private static final String API_SERVER_LIST = "server_list";
    private static final String API_URL = "api_url";

    private final SharedPreferences mSharedPreferences;


    public PreferencesManager(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public String getLastUpdate(String module) {
        return mSharedPreferences.getString(LAST_UPDATE_KEY+"_"+module, "1900-01-01 00:00:00");
    }

    public void saveLastUpdate(String module, String lastModified){
        mSharedPreferences.edit()
                .putString(LAST_UPDATE_KEY+"_"+module, lastModified)
                .apply();
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
        return mSharedPreferences.getString(SERVER_ADDRESS_STRING, AppConfig.getDefaultServer());
    }

    public void updateServerAddress(String serverAddress){
        mSharedPreferences.edit()
                .putString(SERVER_ADDRESS_STRING, serverAddress)
                .apply();
    }

    public Boolean getAutoSynchronize() {
        return mSharedPreferences.getBoolean(AUTO_SYNCHRONIZE, true);
    }

    public void updateAutoSynchronize(Boolean sync) {
        mSharedPreferences.edit()
                .putBoolean(AUTO_SYNCHRONIZE, sync)
                .apply();
    }


    public String getUserName() {
        return mSharedPreferences.getString(USER_NAME, "");
    }

    public void updateUserName(String login) {
        mSharedPreferences.edit()
                .putString(USER_NAME, login)
                .apply();
    }

    public String getUserPassword() {
        return mSharedPreferences.getString(USER_PASSWORD, "");
    }

    public void updateUserPassword(String pass) {
        mSharedPreferences.edit()
                .putString(USER_PASSWORD, pass )
                .apply();
    }

    public String getUserAuthToken() {
        return mSharedPreferences.getString(USER_AUTH_TOKEN, "");
    }

    public String getUserAuthTokenExpiration() {
        return mSharedPreferences.getString(USER_AUTH_TOKEN_EXPIRATION, "1900-01-01 00:00:00");
    }

    public void updateUserAuthToken(String token, String expires) {
        mSharedPreferences.edit()
                .putString(USER_AUTH_TOKEN, token )
                .putString(USER_AUTH_TOKEN_EXPIRATION, expires.isEmpty()?"1900-01-01 00:00:00":expires )
                .apply();
    }

    public String getManagerName() {
        return mSharedPreferences.getString(USER_MANAGER_NAME, App.getStringRes(R.string.default_manager_name));
    }

    public void updateManagerName(String managerName) {
        mSharedPreferences.edit()
                .putString(USER_MANAGER_NAME, managerName)
                .apply();
    }

    public void storeApiUrl(String apiUrl) {
        mSharedPreferences.edit()
                .putString(API_URL, apiUrl)
                .apply();
    }

    public void storeApiServers(List<String> apiServers) {
        mSharedPreferences.edit()
                .putStringSet(API_SERVER_LIST, new HashSet<>(apiServers))
                .apply();
    }

    public String getApiUrl() {
        return mSharedPreferences.getString(API_URL, "");
    }


    public List<String> getApiServers() {
        return new ArrayList<>(
                mSharedPreferences.getStringSet(API_URL, new HashSet<>())
        );
    }
}
