package com.sokolua.manager.data.storage.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import io.realm.internal.Keep;

@Keep
public class VisitRealm extends RealmObject {
    @Required
    @PrimaryKey
    private String id;
    private CustomerRealm customer;
    @Index
    private Date date;
    private boolean done;
    private boolean toSync = false;
    private float latitude = 0;
    private float longitude = 0;
    private Date visited;
    private String imageURI;

    public VisitRealm() {
    }

    public VisitRealm(CustomerRealm customer, String id ,Date date, boolean done) {
        this.id = id;
        this.customer = customer;
        this.date = date;
        this.done = done;
    }

    //region ===================== Getters =========================

    public String getId() {
        return id;
    }

    public CustomerRealm getCustomer() {
        return customer;
    }

    public Date getDate() {
        return date;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isToSync() {
        return toSync;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public Date getVisited() {
        return visited;
    }

    public String getImageURI() {
        return imageURI;
    }

    //endregion ================== Getters =========================

    //region ===================== Setters =========================

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setToSync(boolean toSync) {
        this.toSync = toSync;
    }

    public void setVisited(Date visited, float longitude, float latitude) {
        this.visited = visited;
        this.longitude = longitude;
        this.latitude = latitude;
        this.done = true;
        this.toSync = true;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
        this.toSync = true;
    }

    //endregion ================== Setters =========================

}
