package com.happ.chat.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dante on 1/26/17.
 */
public class ChatDialogs extends RealmObject {

    @PrimaryKey
    private String id;
    private String name;
    private int creatorId;
    @SerializedName("last_message_date_sent")
    private long lastMessageDateSent;

    private String lol;
    private String lol1;
    private int lol2;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastMessageDateSent() {
        return lastMessageDateSent;
    }

    public void setLastMessageDateSent(long lastMessageDateSent) {
        this.lastMessageDateSent = lastMessageDateSent;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getLol() {
        return lol;
    }

    public void setLol(String lol) {
        this.lol = lol;
    }

    public String getLol1() {
        return lol1;
    }

    public void setLol1(String lol1) {
        this.lol1 = lol1;
    }

    public int getLol2() {
        return lol2;
    }

    public void setLol2(int lol2) {
        this.lol2 = lol2;
    }
}
