package com.happ.admin.happ.models;

import io.realm.RealmObject;

/**
 * Created by dante on 7/26/16.
 */
public class Interest extends RealmObject {

    private Interest parent;
    private String title;
    private String icon_url;
    private String color;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Interest getParent() {
        return parent;
    }

    public void setParent(Interest parent) {
        this.parent = parent;
    }
}
