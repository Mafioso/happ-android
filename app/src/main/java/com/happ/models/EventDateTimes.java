package com.happ.models;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by dante on 12/26/16.
 */
public class EventDateTimes extends RealmObject {

    private Date date;
//    private Date start_time;
//    private Date end_time;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

//    public Date getStart_time() {
//        return start_time;
//    }
//
//    public void setStart_time(Date start_time) {
//        this.start_time = start_time;
//    }
//
//    public Date getEnd_time() {
//        return end_time;
//    }
//
//    public void setEnd_time(Date end_time) {
//        this.end_time = end_time;
//    }
}
