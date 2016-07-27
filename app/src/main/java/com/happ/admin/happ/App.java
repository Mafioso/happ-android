package com.happ.admin.happ;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by dante on 7/27/16.
 */
public class App extends Application {
    private static Context context;



    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public static Context getContext() {
        return context;
    }
}
