package com.happ.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by dante on 9/7/16.
 */
public class CitiesResponse {

    @SerializedName("results")
    private ArrayList<City> cities;

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }
}
