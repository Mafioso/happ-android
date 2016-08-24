package com.happ.retrofit;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.models.Event;
import com.happ.models.EventsResponse;
import com.happ.models.Interest;
import com.happ.models.InterestResponse;

import java.util.List;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private OkHttpClient httpClient;

    private static HappRestClient instance;

    public static synchronized HappRestClient getInstance() {
        if (instance == null) {
            instance = new HappRestClient();
        }
        return instance;
    }

    private HappRestClient() {
        gson = new GsonBuilder().create();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        httpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new LocalResponseInterceptor(App.getContext()))
                .addInterceptor(logging)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://127.0.0.1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
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
                    Intent intent = new Intent(BroadcastIntents.EVENTS_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    intent.putExtra("BODY", response.body().toString());
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.EVENTS_REQUEST_FAIL);
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
//                intent.putExtra("MESSAGE", t.getMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void getInterests() {
        happApi.getInterests().enqueue(new Callback<InterestResponse>() {
            @Override
            public void onResponse(Call<InterestResponse> call, Response<InterestResponse> response) {

                if (response.isSuccessful()){
                    List<Interest> interests = response.body().getInterest();

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    realm.copyToRealmOrUpdate(interests);

                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.INTERESTS_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);

                }
                else {
                    Intent intent = new Intent(BroadcastIntents.INTERESTS_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    intent.putExtra("BODY", response.body().toString());
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<InterestResponse> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.INTERESTS_REQUEST_FAIL);
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
//                intent.putExtra("MESSAGE", t.getMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }


}
