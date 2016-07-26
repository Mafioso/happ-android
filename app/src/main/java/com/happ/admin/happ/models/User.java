package com.happ.admin.happ.models;

import io.realm.RealmObject;

/**
 * Created by dante on 7/26/16.
 */
public class User extends RealmObject {

    private String username;
    private String full_name;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }
}
