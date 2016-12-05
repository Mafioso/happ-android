package com.happ;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.bugsnag.android.Bugsnag;
import com.happ.controllers.LoginActivity;
import com.happ.models.City;
import com.happ.models.Event;
import com.happ.models.HappToken;
import com.happ.models.User;

import net.danlew.android.joda.JodaTimeAndroid;

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
public class App extends MultiDexApplication {
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        JodaTimeAndroid.init(context);
        Bugsnag.init(this);

        String language = getApplicationContext().getResources().getConfiguration().locale.getLanguage();
        System.out.print(language);

        RealmConfiguration realmConfiguration = new RealmConfiguration
                .Builder(this)
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);

//        if (hasInternet()) {
//            deleleFromRealm();
//        }

        if (hasConnection(context)) {
            deleleFromRealm();
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

//    public static boolean hasInternet()  {
//        try{
//            String url = "http://google.com/";
//
//            HttpURLConnection.setFollowRedirects(false);
//            HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
//            // conn.setRequestMethod("HEAD");
//            conn.setRequestMethod("GET");
//            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                Toast.makeText(App.getContext(), "has internet", Toast.LENGTH_LONG).show();
//                return true; // интернет есть
//            }
//        }catch(Exception e) {
//            Log.d("Log:", "error: " + e); // на ошибку можно не обращать внимание
//        }
//        return false;
//    }

    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            Toast.makeText(context, "WIFI COnntected", Toast.LENGTH_SHORT).show();
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            Toast.makeText(context, "3g is Conntected", Toast.LENGTH_SHORT).show();
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
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
