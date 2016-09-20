package com.happ.retrofit;

import com.happ.models.CitiesResponse;
import com.happ.models.City;
import com.happ.models.EventsResponse;
import com.happ.models.HappToken;
import com.happ.models.InterestResponse;
import com.happ.models.LoginData;
import com.happ.models.SignUpData;
import com.happ.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by dante on 7/26/16.
 */
public interface HAPPapi {

    @GET("events/")
    Call<EventsResponse> getEvents(@Query("page") int page);

    @GET("interests/")
    Call<InterestResponse> getInterests(@Query("page") int page);

    @POST("auth/login/")
    Call<HappToken> doLogin(@Body LoginData data);

    @POST("auth/refresh/")
    Call<HappToken> refreshToken(@Body HappToken data);

    @GET("user/{username}")
    Call<User> getUser(@Path("username") String username);

    @GET("users/current/")
    Call<User> getCurrentUser();

    @POST("registration/")
    Call<HappToken> doSignUp(@Body SignUpData data);

    @GET("cities/{id}/")
    Call<City> getCity(@Path("id") String id);

    @GET("cities/")
    Call<CitiesResponse> getCities(@Query("page") int page, @Query("search") String searchText);

    @POST("interests/set/")
    Call<Void> setInterests(@Body List<String> data);

    @GET("cities/{id}/set/")
    Call<Void> setCity(@Path("id") String cityId);

    @GET("events/favourites/")
    Call<EventsResponse> getFavourites(@Query("page") int page);
}