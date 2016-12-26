package com.happ.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dante on 7/26/16.
 */
public class User extends RealmObject {
    private String username;
//    @SerializedName("fullname")
    private String fullname;
    private String fn;
    private String email;
    @PrimaryKey
    private String id;
    private String phone;
    private Date date_of_birth;
    private Settings settings;
    private int gender;
    private RealmList<Interest> interests;
    @SerializedName("avatar_id")
    private String avatarId;
    private HappImage avatar;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        if (fullname == null) {
            if (fn == null) return username;
            return fn;
        }
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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
        return fullname;
    }

    public void setFn(String fn) {
        this.fullname = fn;
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
        if (this.avatar != null) return this.avatar.getUrl();
        else return null;
    }

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Date date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public HappImage getAvatar() {
        return avatar;
    }

    public void setAvatar(HappImage avatar) {
        this.avatar = avatar;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }
}
