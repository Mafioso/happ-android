package com.happ.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dante on 8/24/16.
 */
public class InterestResponse {
    @SerializedName("results")
    private List<Interest> interests;
    private int count;
    private String next;

    public List<Interest> getInterests() {
        return interests;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
}
