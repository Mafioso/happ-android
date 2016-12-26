package com.happ.models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dante on 7/26/16.
 */
public class Event extends RealmObject implements Serializable {
    @PrimaryKey
    private String id;
    private String title;
    private RealmList<Interest> interests;
    private String description;
    private RealmList<HappImage> images;
    private RealmList<EventPhone> phones;
    @SerializedName("votes_num")
    private int votesCount;
    @SerializedName("is_upvoted")
    private boolean didVote;
    @SerializedName("is_in_favourites")
    private boolean inFavorites;
    @SerializedName("views_count")
    private int viewsCount;
    @SerializedName("start_datetime")
    private Date startDate;
    @SerializedName("end_datetime")
    private Date endDate;
    private RealmList<EventDateTimes> datetimes;
    @SerializedName("address")
    private String place;
    private Currency currency;
    @SerializedName("min_price")
    private int lowestPrice;
    @SerializedName("max_price")
    private int highestPrice;
    private User author;
    private String email;
    @SerializedName("web_site")
    private String webSite;
    private boolean localOnly = false;
    private String localId;
    @SerializedName("city_id")
    private String cityId;
    @SerializedName("currency_id")
    private String currencyId;
    @Ignore
    @SerializedName("interest_ids")
    private ArrayList<String> interestIds;
    @Ignore
    @SerializedName("image_ids")
    private ArrayList<String> imageIds;
    private int status;
    @SerializedName("is_active")
    private boolean isActive;
    @SerializedName("rejection_reasons")
    private RealmList<RejectionReasons> rejectionReasons;
    @SerializedName("registration_link")
    private String registationLink;
    @SerializedName("close_on_start")
    private boolean closeOnStart;

    //geo for googleMap
    private GeopointResponse geopoint;

    public String getId() {
        return id;
    }
    public void setId(String id) {
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

    public RealmList<HappImage> getImages() {
        return images;
    }

    public void setImages(RealmList<HappImage> images) {
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
        if (this.interests != null && this.interests.size() > 0) {
            return interests.get(0);
//            for (Iterator<Interest> interest = this.interests.iterator(); interest.hasNext();) {
//                Interest tmp = interest.next();
//                for (Iterator<Interest> selected = App.getCurrentUser().getInterests().iterator();selected.hasNext();) {
//                    if (tmp.getId().equals(selected.next())) {
//                        return tmp;
//                    }
//                }
//            }
        }
        return null;
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
        String currencyString = "";
//        if (currency != null) {
//            currencyString = currency.getSymbol();
//        }
        if (lowestPrice > 0) {
            price = price + currencyString + " " + String.valueOf(lowestPrice);
        } else {
            price = "Free";
        }
        if (highestPrice > 0) {
            price = price + " — ";
            if (lowestPrice > 0) {
                price = price + String.valueOf(highestPrice);
            } else {
                price = price + currencyString + " " + String.valueOf(highestPrice);
            }

        }
        return price;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public RealmList<Interest> getInterests() {
        return interests;
    }

    public void setInterests(RealmList<Interest> interests) {
        this.interests = interests;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public boolean isLocalOnly() {
        return localOnly;
    }

    public void setLocalOnly(boolean localOnly) {
        this.localOnly = localOnly;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public ArrayList<String> getInterestIds() {
        return interestIds;
    }

    public void setInterestIds(ArrayList<String> interestIds) {
        this.interestIds = interestIds;
    }

    public String getColor() {
        String color = null;
        if (images.size() > 0) {
            color = images.get(0).getColor();
        }
        if (color == null) color = "#595959";
        return color;
    }

    public RealmList<EventPhone> getPhones() {
        return phones;
    }

    public void setPhones(RealmList<EventPhone> phones) {
        this.phones = phones;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public GeopointResponse getGeopoint() {
        return geopoint;
    }

    public void setGeopoint(GeopointResponse geopoint) {
        this.geopoint = geopoint;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public RealmList<RejectionReasons> getRejectionReasons() {
        return rejectionReasons;
    }

    public void setRejectionReasons(RealmList<RejectionReasons> rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
    }

    public ArrayList<String> getImageIds() {
        return imageIds;
    }

    public void setImageIds(ArrayList<String> imageIds) {
        this.imageIds = imageIds;
    }

    public String getRegistationLink() {
        return registationLink;
    }

    public void setRegistationLink(String registationLink) {
        this.registationLink = registationLink;
    }

    public boolean isCloseOnStart() {
        return closeOnStart;
    }

    public void setCloseOnStart(boolean closeOnStart) {
        this.closeOnStart = closeOnStart;
    }

    public RealmList<EventDateTimes> getDatetimes() {
        return datetimes;
    }

    public void setDatetimes(RealmList<EventDateTimes> datetimes) {
        this.datetimes = datetimes;
    }
}