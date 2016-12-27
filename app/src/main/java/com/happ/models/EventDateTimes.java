package com.happ.models;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by dante on 12/26/16.
 */
public class EventDateTimes extends RealmObject {


    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
