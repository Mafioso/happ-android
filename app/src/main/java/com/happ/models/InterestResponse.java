package com.happ.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dante on 8/24/16.
 */
public class InterestResponse {
    @SerializedName("results")
    private List<Interest> interests;

    public List<Interest> getInterests() {
        return interests;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
    }
}
