package com.happ.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dante on 8/29/16.
 */
public class HappToken extends RealmObject {

    @PrimaryKey
    private int id = 1;

    private String token;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
