package com.happ.models;

import org.parceler.Parcel;

import io.realm.GeopointArrayResponceRealmProxy;
import io.realm.RealmObject;

/**
 * Created by dante on 12/25/16.
 */
@Parcel(implementations = {GeopointArrayResponceRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {GeopointArrayResponce.class})
public class GeopointArrayResponce extends RealmObject {

        private float lat;
        private float lng;

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }
}
