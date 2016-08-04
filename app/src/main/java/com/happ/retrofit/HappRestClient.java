package com.happ.retrofit;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.models.Event;
import com.happ.models.EventsResponse;

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
                .baseUrl("http://happ.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(localclient)
                .build();

        happApi = retrofit.create(HAPPapi.class);
    }

    public void getEvents() {
        this.getEvents(1);
    }

    public void getEvents(int page) {
        happApi.getEvents(page).enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                Log.d("RETROFIT RESPONSE >>>>>", response.message());

                if (response.isSuccessful()){

                    List<Event> events = response.body().getEvents();

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    realm.copyToRealmOrUpdate(events);

                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.EVENTS_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);

                }
                else {
                    Log.d("RETROFIT REQUEST >>>>>>", response.message());
                    Intent intent = new Intent(BroadcastIntents.EVENTS_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    intent.putExtra("BODY", response.body().toString());
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                Log.d("RETROFIT RESPONSE >>>>>", t.getMessage());
                Intent intent = new Intent(BroadcastIntents.EVENTS_REQUEST_FAIL);
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
//                intent.putExtra("MESSAGE", t.getMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

}
