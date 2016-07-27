package com.happ.admin.happ.controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.happ.admin.happ.R;
import com.happ.admin.happ.App;
import com.happ.admin.happ.models.Events;
import com.happ.admin.happ.models.EventsResponse;
import com.happ.admin.happ.retrofit.HAPPapi;
import com.happ.admin.happ.retrofit.LocalResponseInterceptor;

import java.util.List;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FeedActivity extends AppCompatActivity {
    private Gson gson;
    private OkHttpClient localclient;
    private Retrofit retrofit;
    private TextView tw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gson = new GsonBuilder().create();
        localclient = new OkHttpClient().newBuilder().addInterceptor(new LocalResponseInterceptor(App.getContext())).build();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.11.50/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(localclient)
                .build();


        HAPPapi service = retrofit.create(HAPPapi.class);
        Call<EventsResponse> call = service.getEvents();
        call.enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {

                Log.e("OnResponce", "OK");

                if (response.isSuccessful()){

                    List<Events> managers = response.body().getEvents();
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(managers);
                    realm.commitTransaction();


                    Events evt = realm.where(Events.class).findFirst();
                    evt = realm.copyFromRealm(evt);

                    tw = (TextView) findViewById(R.id.textView2);
                    tw.setText(String.valueOf(evt.getTitle() ));
                    realm.close();

                    System.out.println(managers.toString());

                }
                else {
                    Log.e("Response Successful?", "NO");
                    Log.e("response.message",response.message());
                    Log.e("response.code", String.valueOf(response.code()));
                    Log.e("response.body", String.valueOf(response.body()));



                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                Log.e("OnFailure", "Fail");
                Log.e((t.getMessage()), t.getMessage());
            }
        });

        setContentView(R.layout.activity_feed);
    }


}
