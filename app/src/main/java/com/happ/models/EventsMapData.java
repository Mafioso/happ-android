package com.happ.models;

/**
 * Created by dante on 12/25/16.
 */
public class EventsMapData {

    private GeopointArrayResponce center;
    private int radius;


    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public GeopointArrayResponce getCenter() {
        return center;
    }

    public void setCenter(GeopointArrayResponce center) {
        this.center = center;
    }
}
