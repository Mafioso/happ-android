package com.happ.models;

import io.realm.RealmObject;

/**
 * Created by dante on 12/5/16.
 */
public class Geopoints extends RealmObject {

    private float lat;
    private float lng;

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }
}
