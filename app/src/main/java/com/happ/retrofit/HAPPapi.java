package com.happ.retrofit;

import com.happ.models.EventsResponse;
import com.happ.models.InterestResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


/**
 * Created by dante on 7/26/16.
 */
public interface HAPPapi {

    @GET("events/{page}")
    Call<EventsResponse> getEvents(@Path("page") int page);

    @GET("interests")
    Call<InterestResponse> getInterests();
}
