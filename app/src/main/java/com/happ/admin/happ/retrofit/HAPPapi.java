package com.happ.admin.happ.retrofit;

import com.happ.admin.happ.models.Events;

import java.util.Map;

import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;


/**
 * Created by dante on 7/26/16.
 */
public interface HAPPapi {

    @GET("/events")
    Call<RealmList<Events>> event (@FieldMap Map<String,String> map);
}
