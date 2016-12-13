package com.happ.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dante on 7/26/16.
 */
public class User extends RealmObject {
    @PrimaryKey
    private String username;
    @SerializedName("fullname")
    private String fullName;
    private String fn;
    private String email;
    private String id;
    private String phone;
    private Settings settings;
    private int gender;
    private RealmList<Interest> interests;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        if (fullName == null) {
            if (fn == null) return username;
            return fn;
        }
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public String getFn() {
        return fullName;
    }

    public void setFn(String fn) {
        this.fullName = fn;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public RealmList<Interest> getInterests() {
        return interests;
    }

    public void setInterests(RealmList<Interest> interests) {
        this.interests = interests;
    }

    public ArrayList<String> getInterestIds() {
        ArrayList<String> ids = new ArrayList<>();

        for (int i=0; i<interests.size();i++) {
            ids.add(interests.get(i).getId());
        }

        return ids;
    }

    public String getImageUrl() {
        return "http://nick.mtvnimages.com/nick/video/images/avatar/avatar-118-16x9.jpg";
    }

}
