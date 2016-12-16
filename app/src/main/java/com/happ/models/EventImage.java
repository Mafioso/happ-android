package com.happ.models;

import com.happ.App;
import com.happ.R;

import io.realm.RealmObject;

/**
 * Created by dante on 7/26/16.
 */
public class EventImage extends RealmObject {

    private String id;
    private String path;
    private String color;

    public String getUrl() {
        String host = App.getContext().getString(R.string.HOST);
        String url = host.substring(0,host.length()-1) + this.path;
        return url;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
