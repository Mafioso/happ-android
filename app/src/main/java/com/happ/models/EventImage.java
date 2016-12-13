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

    public String getUrl() {
        return path;
//        String host = App.getContext().getString(R.string.HOST);
//        String url = host.substring(0,host.length()-1) + this.path;
//        return url;
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
}
