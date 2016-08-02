package com.happ.admin.happ.retrofit;

import com.happ.admin.happ.models.EventsResponse;

import retrofit2.Call;
import retrofit2.http.GET;


/**
 * Created by dante on 7/26/16.
 */
public interface HAPPapi {

    @GET("events")
//    Call<RealmList<Events>> getEvents(@FieldMap Map<String,String> map);
    Call<EventsResponse> getEvent();
}
