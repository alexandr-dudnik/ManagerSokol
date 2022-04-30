package com.sokolua.manager.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.reactivex.Observable;

public class NetworkStatusChecker {
    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }

    public static Observable<Boolean> isInternetAvailableObs() {
        return Observable.just(isNetworkAvailable());
    }
}
