package com.happ.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by dante on 12/26/16.
 */
public class EventDateTimes extends RealmObject {

    @SerializedName("date")
    @Expose
    private Date date;

    @SerializedName("start_time")
    @Expose
    private Date startTime;

    @SerializedName("end_time")
    @Expose
    private Date endTime;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
