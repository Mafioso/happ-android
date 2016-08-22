package com.happ.models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dante on 7/26/16.
 */
public class Event extends RealmObject {
    @PrimaryKey
    private int id;
    private String title;
    private Interest interest;
    private String description;
    private RealmList<EventImage> images;
    @SerializedName("votes_count")
    private int votesCount;
    @SerializedName("did_vote")
    private boolean didVote;
    @SerializedName("in_favorites")
    private boolean inFavorites;
    @SerializedName("views_count")
    private int viewsCount;
    @SerializedName("start_date")
    private Date startDate;
    @SerializedName("end_date")
    private Date endDate;
    private String place;
    private Currency currency;
    @SerializedName("lowest_price")
    private int lowestPrice;
    @SerializedName("highest_price")
    private int highestPrice;
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

    public int getVotesCount() {
        return votesCount;
    }

    public void setVotesCount(int votesCount) {
        this.votesCount = votesCount;
    }

    public boolean isDidVote() {
        return didVote;
    }

    public void setDidVote(boolean didVote) {
        this.didVote = didVote;
    }

    public boolean isInFavorites() {
        return inFavorites;
    }

    public void setInFavorites(boolean inFavorites) {
        this.inFavorites = inFavorites;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getStartDateFormatted(String format) {
        DateTimeFormatter dtFormatter = DateTimeFormat.forPattern(format);
        DateTime eventDate = new DateTime(startDate);
        return eventDate.toString(dtFormatter);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getEndDateFormatted(String format) {
        DateTimeFormatter dtFormatter = DateTimeFormat.forPattern(format);
        DateTime eventDate = new DateTime(endDate);
        return eventDate.toString(dtFormatter);
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public int getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(int lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public int getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(int highestPrice) {
        this.highestPrice = highestPrice;
    }

    public String getPriceRange() {
        String price = "";
        if (lowestPrice > 0) {
            price = price + currency.getSymbol() + String.valueOf(lowestPrice);
        } else {
            price = "Free";
        }
        if (highestPrice > 0) {
            price = price + " — " + currency.getSymbol() + String.valueOf(highestPrice);
        }
        return price;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}