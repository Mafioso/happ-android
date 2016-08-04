package com.happ.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by dante on 7/26/16.
 */
public class Interest extends RealmObject {

    private Interest parent;
    private String title;
    @SerializedName("icon_url")
    private String iconUrl;
    private String color;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
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
