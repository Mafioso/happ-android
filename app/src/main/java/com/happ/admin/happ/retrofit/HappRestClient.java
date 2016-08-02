package com.happ.admin.happ.retrofit;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.happ.admin.happ.App;
import com.happ.admin.happ.BroadcastIntents;
import com.happ.admin.happ.models.Events;
import com.happ.admin.happ.models.EventsResponse;

import java.util.List;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dante on 8/1/16.
 */
public class HappRestClient {

    private Gson gson;
    private Retrofit retrofit;
    private HAPPapi happApi;
    private OkHttpClient localclient;

    private static HappRestClient instance;

    public static synchronized HappRestClient getInstance() {
        if (instance == null) {
            instance = new HappRestClient();
        }
        return instance;
    }

    private HappRestClient() {
        gson = new GsonBuilder().create();
        localclient = new OkHttpClient().newBuilder().addInterceptor(new LocalResponseInterceptor(App.getContext())).build();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.11.50/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(localclient)
                .build();

        happApi = retrofit.create(HAPPapi.class);
    }

    public void getEvents() {
        happApi.getEvent().enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {

                Log.e("OnResponce", "OK");


                if (response.isSuccessful()){

                    List<Events> events = response.body().getEvents();

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    realm.copyToRealmOrUpdate(events);

                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.EVENTS_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);

                }
                else {
                    Log.e("Response Successful?", "NO");
                    Log.e("response.message",response.message());
                    Log.e("response.code", String.valueOf(response.code()));
                    Log.e("response.body", String.valueOf(response.body()));

                    // TOAST
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                Log.e("OnFailure", "Fail");
                Log.e((t.getMessage()), t.getMessage());
            }
        });
    }

}
