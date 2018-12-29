package com.sokolua.manager.utils;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class IntentStarter {
    public static boolean openMap(String address){
        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+ Uri.encode(address));
        Intent googleMaps = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        googleMaps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        googleMaps.setPackage("com.google.android.apps.maps");
//        if (googleMaps.resolveActivity(App.getContext().getPackageManager()) != null) {
        try{
            App.getContext().startActivity(googleMaps);
            return true;
        }catch(Throwable exc) {
            Log.e("Manager intent",exc.getMessage(),exc);
            return false;
        }
    }

    public static boolean openCaller(String phone){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (intent.resolveActivity(App.getContext().getPackageManager()) != null) {
        try{
            App.getContext().startActivity(intent);
            return true;
        }catch (Throwable exc) {
            Log.e("Manager intent",exc.getMessage(),exc);
            return false;
        }
    }

    public static boolean composeEmail(String email){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("mailto:"+email));
        try{
            App.getContext().startActivity(intent);
            return true;
        }catch (Throwable exc){
            Log.e("Manager intent",exc.getMessage(),exc);
            return false;
        }
    }

}
