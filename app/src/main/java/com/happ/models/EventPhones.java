package com.happ.models;

import io.realm.RealmObject;

/**
 * Created by dante on 11/30/16.
 */
public class EventPhones extends RealmObject {

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
