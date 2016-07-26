package com.happ.admin.happ.controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.happ.admin.happ.R;
import com.happ.admin.happ.retrofit.LocalResponseInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FeedActivity extends AppCompatActivity {
    private Gson gson;
    private OkHttpClient localclient;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gson = new GsonBuilder().create();
        localclient = new OkHttpClient().newBuilder().addInterceptor(new LocalResponseInterceptor(this)).build();
        retrofit = new Retrofit.Builder().client(localclient)
                .baseUrl("http//:localhost")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();



        setContentView(R.layout.activity_feed);
    }
}
