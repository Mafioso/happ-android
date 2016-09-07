package com.happ;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;

import com.happ.controllers.LoginActivity;
import com.happ.models.City;
import com.happ.models.Event;
import com.happ.models.HappToken;
import com.happ.models.User;
import com.happ.retrofit.APIService;

import net.danlew.android.joda.JodaTimeAndroid;

import java.net.HttpURLConnection;
import java.net.URL;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
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

    public static User getCurrentUser() {
        Realm realm = Realm.getDefaultInstance();
        User currentUser = null;
        try {
            String username = getTokenData().get("username", String.class);
            User user = realm.where(User.class).equalTo("username", username).findFirst();
            currentUser = realm.copyFromRealm(user);
        } catch (Exception ex) {
        } finally {
            realm.close();
            return currentUser;
        }
    }

    public static City getCurrentCity() {
        return getCurrentUser().getSettings().getCityObject();
    }

    public static Claims getTokenData() {
        Realm realm = Realm.getDefaultInstance();
        Claims claims = null;
        try {
            HappToken token = realm.where(HappToken.class).findFirst();
            int i = token.getToken().lastIndexOf('.');
            String unsignedToken = token.getToken().substring(0,i+1);
            Jwt<Header,Claims> tokenData = Jwts.parser().parseClaimsJwt(unsignedToken);
            claims = tokenData.getBody();
        } catch (Exception ex) {

        } finally {
            realm.close();
            return claims;
        }
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

    public static void doLogout(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(0,0);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        realm.close();
    }


    public static Context getContext() {
        return context;
    }
}
