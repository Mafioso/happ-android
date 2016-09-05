package com.happ.models;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by iztiev on 9/5/16.
 */
public class Settings extends RealmObject{
    private int id = 1;
    private String language;
    private String city;


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public City getCityObject() {
        City city = null;
        Realm realm = Realm.getDefaultInstance();
        try {
            city = realm.where(City.class).equalTo("id", this.city).findFirst();
            city = realm.copyFromRealm(city);
        } catch (Exception ex) {
            Log.e("MODELS", ex.getLocalizedMessage());
        } finally {
            realm.close();
            return city;
        }
    }

    public String getCityName() {
        City city = getCityObject();
        if (city != null) return city.getName();
        return "";
    }
}
