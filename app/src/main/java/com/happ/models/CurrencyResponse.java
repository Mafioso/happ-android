package com.happ.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by dante on 11/24/16.
 */
public class CurrencyResponse {

    @SerializedName("results")
    private ArrayList<Currency> currencies;

    public ArrayList<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(ArrayList<Currency> currencies) {
        this.currencies = currencies;
    }
}
