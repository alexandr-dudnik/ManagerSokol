package com.sokolua.manager.data.network.req;

import androidx.annotation.Keep;

import com.sokolua.manager.data.storage.realm.VisitRealm;
import com.sokolua.manager.utils.UiHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Keep
public class SendVisitReq {
    private String id;
    private String latitude;
    private String longitude;
    private String visited;
    private String image;
    private boolean done;


    public SendVisitReq(String id, float latitude, float longitude, Date visited, String imageURI, boolean done) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm':00'", Locale.getDefault());

        this.id = id;
        this.latitude = String.format(Locale.getDefault(),"%.8f",latitude);
        this.longitude = String.format(Locale.getDefault(),"%.8f",longitude);
        this.visited = sdf.format(visited);
        this.image = UiHelper.getBase64FromImage(imageURI);
        this.done = done;
    }

    public SendVisitReq(VisitRealm visit) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm':00'", Locale.getDefault());
        this.id = visit.getId();
        this.latitude = String.format(Locale.getDefault(),"%.8f",visit.getLatitude());
        this.longitude = String.format(Locale.getDefault(),"%.8f",visit.getLongitude());
        this.visited = sdf.format(visit.getVisited());
        this.image = UiHelper.getBase64FromImage(visit.getImageURI());
        this.done = visit.isDone();
    }




}
