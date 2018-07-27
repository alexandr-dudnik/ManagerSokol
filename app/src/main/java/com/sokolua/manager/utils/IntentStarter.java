package com.sokolua.manager.utils;

import android.content.Intent;
import android.net.Uri;

public class IntentStarter {
    public static boolean openMap(String address){
        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+ Uri.encode(address));
        Intent googleMaps = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        googleMaps.setPackage("com.google.android.apps.maps");
        if (googleMaps.resolveActivity(App.getContext().getPackageManager()) != null) {
            App.getContext().startActivity(googleMaps);
            return true;
        }
        return false;
    }

    public static boolean openCaller(String phone){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+phone));
        if (intent.resolveActivity(App.getContext().getPackageManager()) != null) {
            App.getContext().startActivity(intent);
            return true;
        }
        return false;
    }

    public static boolean composeEmail(String email){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setData(Uri.parse("mailto:"+email));
        if (intent.resolveActivity(App.getContext().getPackageManager()) != null) {
            App.getContext().startActivity(intent);
            return true;
        }
        return false;
    }

}
