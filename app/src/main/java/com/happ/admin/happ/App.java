package com.happ.admin.happ;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.happ.admin.happ.models.Events;

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
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
        if (hasInternet()) {
            deleleFromRealm();
        }
    }

    public void deleleFromRealm(){
        // obtain the results of a query
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Events> results = realm.where(Events.class).findAll();

    // All changes to data must happen in a transaction
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
//                // remove single match
//                results.deleteFirstFromRealm();
//                results.deleteLastFromRealm();
//
//                // remove a single object
//                Dog dog = results.get(5);
//                dog.deleteFromRealm();

                // Delete all matches
                results.deleteAllFromRealm();
            }
        });
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
