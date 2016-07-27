package com.happ.admin.happ.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dante on 7/26/16.
 */
public class Events extends RealmObject {
    @PrimaryKey
    private int id;
    private String title;
    private Interest interest;
    private String description;
    private RealmList<EventImage> images;
    private int votes_count;
    private boolean did_vote;
    private boolean in_favorites;
    private int views_count;
    private Date start_date;
    private Date end_date;
    private String place;
    private Currency currency;
    @SerializedName("lowest_price")
    private int lowest_price;
    private int highest_price;
    private User author;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RealmList<EventImage> getImages() {
        return images;
    }

    public void setImages(RealmList<EventImage> images) {
        this.images = images;
    }

    public int getVotes_count() {
        return votes_count;
    }

    public void setVotes_count(int votes_count) {
        this.votes_count = votes_count;
    }

    public boolean isDid_vote() {
        return did_vote;
    }

    public void setDid_vote(boolean did_vote) {
        this.did_vote = did_vote;
    }

    public boolean isIn_favorites() {
        return in_favorites;
    }

    public void setIn_favorites(boolean in_favorites) {
        this.in_favorites = in_favorites;
    }

    public int getViews_count() {
        return views_count;
    }

    public void setViews_count(int views_count) {
        this.views_count = views_count;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Interest getInterest() {
        return interest;
    }

    public void setInterest(Interest interest) {
        this.interest = interest;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public int getLowest_price() {
        return lowest_price;
    }

    public void setLowest_price(int lowest_price) {
        this.lowest_price = lowest_price;
    }

    public int getHighest_price() {
        return highest_price;
    }

    public void setHighest_price(int highest_price) {
        this.highest_price = highest_price;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}