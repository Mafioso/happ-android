package com.happ;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.happ.models.Event;
import com.happ.retrofit.APIService;

import net.danlew.android.joda.JodaTimeAndroid;

import java.net.HttpURLConnection;
import java.net.URL;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by dante on 7/27/16.
 */
public class App extends Application {
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        JodaTimeAndroid.init(context);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);

        APIService.getInterests();

        if (hasInternet()) {
//            deleleFromRealm();
        }
    }

    public void deleleFromRealm(){
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Event> results = realm.where(Event.class).notEqualTo("inFavorites", true).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
        realm.close();
    }

    public static boolean hasInternet()  {
        try{
            String url = "http://google.com/";

            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
            // conn.setRequestMethod("HEAD");
            conn.setRequestMethod("GET");
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                return true; // интернет есть
            }
        }catch(Exception e) {
            Log.d("Log:", "error: " + e); // на ошибку можно не обращать внимание
        }
        return false;
    }


    public static Context getContext() {
        return context;
    }
}
