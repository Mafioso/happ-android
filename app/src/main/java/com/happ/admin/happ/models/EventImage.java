package com.happ.admin.happ.models;

import io.realm.RealmObject;

/**
 * Created by dante on 7/26/16.
 */
public class EventImage extends RealmObject {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
