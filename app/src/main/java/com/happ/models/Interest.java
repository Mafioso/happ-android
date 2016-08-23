package com.happ.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dante on 7/26/16.
 */
public class Interest extends RealmObject {

    @PrimaryKey
    private int id;
    private int parent_id;
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

    public ArrayList<String> getFullTitle() {
        ArrayList<String> result = new ArrayList<>();
        Interest parent = getParent();
        if (parent != null) result.add(parent.title);
        result.add(title);
        return result;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getColor() {
        Interest parent = getParent();
        if (parent != null && parent.color != null) return parent.color;
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Interest getParent() {
        Realm realm = Realm.getDefaultInstance();
        Interest parent = realm.where(Interest.class).equalTo("id", this.parent_id).findFirst();
        parent = realm.copyFromRealm(parent);
        realm.close();

        return parent;
    }

    public int getParentId() {
        return parent_id;
    }

    public void setParentId(Interest parent) {
        this.parent_id = parent_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
