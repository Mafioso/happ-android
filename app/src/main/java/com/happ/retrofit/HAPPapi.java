package com.happ.retrofit;

import com.happ.models.ChangePwData;
import com.happ.models.CitiesResponse;
import com.happ.models.City;
import com.happ.models.Event;
import com.happ.models.EventsResponse;
import com.happ.models.HappToken;
import com.happ.models.InterestResponse;
import com.happ.models.LoginData;
import com.happ.models.SignUpData;
import com.happ.models.User;
import com.happ.models.UserEditData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by dante on 7/26/16.
 */
public interface HAPPapi {


//    @GET("/maps/api/directions/json")
//    RouteResponse getRoute(
//            @Query(value = "origin", encoded = false) String position,
//            @Query(value = "destination", encoded = false) String destination,
//            @Query("sensor") boolean sensor,
//            @Query("language") String language);

    @GET("events/feed/")
    Call<EventsResponse> getEvents(@Query("page") int page);
    @GET("events/feed/")
    Call<EventsResponse> getFilteredEvents(@Query("page") int page,
                                           @Query("feed_search") String feedSearchText,
                                           @Query("start_date") String startDate,
                                           @Query("end_date") String endDate,
                                           @Query("max_price") String price);

    @GET("interests/")
    Call<InterestResponse> getInterests(@Query("page") int page);

    @POST("auth/login/")
    Call<HappToken> doLogin(@Body LoginData data);

    @POST("auth/register/")
    Call<HappToken> doSignUp(@Body SignUpData data);

    @POST("auth/password/change/")
    Call<HappToken> doChangePassword(@Body ChangePwData data);

    @POST("users/current/edit/")
    Call<User> doUserEdit(@Body UserEditData data);

    @POST("auth/refresh/")
    Call<HappToken> refreshToken(@Body HappToken data);

    @GET("user/{username}")
    Call<User> getUser(@Path("username") String username);

    @GET("users/current/")
    Call<User> getCurrentUser();

    @PATCH("events/{id}/")
    Call<Event> doEventEdit(@Path("id") String eventEditID, @Body Event event);

    @DELETE("events/{id}/")
    Call<Event> doEventDelete(@Path("id") String eventID);

    @POST("events/{id}/upvote")
    Call<Void> doUpVote(@Path("id") String eventID, @Body EmptyBody body);

    @POST("events/{id}/downvote")
    Call<Void> doDownVote(@Path("id") String eventID, @Body EmptyBody body);

    @POST("events/{id}/fav/")
    Call<Void> doFav(@Path("id") String eventID, @Body EmptyBody body);

    @POST("events/{id}/unfav")
    Call<Void> doUnFav(@Path("id") String eventID, @Body EmptyBody body);

    @GET("cities/{id}/")
    Call<City> getCity(@Path("id") String id);

    @GET("cities/")
    Call<CitiesResponse> getCities(@Query("page") int page, @Query("search") String searchText);

    @POST("interests/set/")
    Call<Void> setInterests(@Body List<String> data);

    @POST("cities/{id}/set/")
    Call<Void> setCity(@Path("id") String cityId);

    @GET("events/favourites/")
    Call<EventsResponse> getFavourites(@Query("page") int page);

    @GET("events/favourites/")
    Call<EventsResponse> getFilteredFavourites(@Query("page") int page,
                                               @Query("feed_search") String feedSearchText,
                                               @Query("start_date") String startDate,
                                               @Query("end_date") String endDate,
                                               @Query("max_price") String price);

    @POST("events/")
    Call<Event> createEvent(@Body Event event);

}