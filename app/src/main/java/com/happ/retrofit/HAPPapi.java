package com.happ.retrofit;

import com.happ.models.EventsResponse;
import com.happ.models.HappToken;
import com.happ.models.InterestResponse;
import com.happ.models.LoginData;
import com.happ.models.SignUpData;
import com.happ.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


/**
 * Created by dante on 7/26/16.
 */
public interface HAPPapi {

    @GET("events/{page}")
    Call<EventsResponse> getEvents(@Path("page") int page);

    @GET("interests")
    Call<InterestResponse> getInterests();

    @POST("login")
    Call<HappToken> doLogin(@Body LoginData data);

    @GET("user/{username}")
    Call<User> getUser(@Path("username") String username);

    @POST("registration")
    Call<HappToken> doSignUp(@Body SignUpData data);

}