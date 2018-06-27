package com.sokolua.manager.data.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesManager {
    private static final String AUTH_STRING_KEY = "auth_string";
    private static final String PRICE_LAST_UPDATE_KEY = "last_update";

    private final SharedPreferences mSharedPreferences;


    public PreferencesManager(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    String getAuthString(){
        return mSharedPreferences.getString(AUTH_STRING_KEY, "xxxx-xxxx-xxxx");
    }

    void saveAuthStringKey(String authString){
        SharedPreferences.Editor spEditor = mSharedPreferences.edit();
        spEditor.putString(AUTH_STRING_KEY, authString);
        spEditor.apply();
    }

    public String getLastProductUpdate(){
        // TODO: 23.03.2017 uncomment this
        //return mSharedPreferences.getString(PRODUCT_LAST_UPDATE_KEY, "Thu, 01 Jan 1970 00:00:00 GMT");
        return "Thu, 01 Jan 1970 00:00:00 GMT";
    }

    public void saveLastProductUpdate(String lastModified){
        SharedPreferences.Editor spEditor = mSharedPreferences.edit();
        spEditor.putString(PRICE_LAST_UPDATE_KEY, lastModified);
        spEditor.apply();
    }
}
