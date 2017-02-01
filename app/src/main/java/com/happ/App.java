package com.happ;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bugsnag.android.Bugsnag;
import com.happ.controllers.LoginActivity;
import com.happ.models.City;
import com.happ.models.Event;
import com.happ.models.HappToken;
import com.happ.models.User;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

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

    // Set your QuickBlox application credentials here
    static final String APP_ID = "52527";
    static final String AUTH_KEY = "PZvm6eaMNFrCkKH";
    static final String AUTH_SECRET = "3fsKy4TWzzVMvfT";
    static final String ACCOUNT_KEY = "x3MuTiZGYsbxzSBtsy3t";

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        JodaTimeAndroid.init(context);
        Bugsnag.init(this);

        RealmConfiguration realmConfiguration = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);

        if (hasConnection(context)) {
            deleleFromRealm();
        }


        //        QBSettings.getInstance().fastConfigInit("52527", "PZvm6eaMNFrCkKH", "3fsKy4TWzzVMvfT");
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

//        final QBUser user = new QBUser("dante666lcf", "DEVILsuka666DANTEdmc");
        final QBUser user = new QBUser("rustem", "qwerty123");

//        QBUsers.signUp(user).performAsync(new QBEntityCallback<QBUser>() {
//            @Override
//            public void onSuccess(QBUser user, Bundle args) {
//
//            }
//
//            @Override
//            public void onError(QBResponseException error) {
//
//            }
//        });

        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {

            @Override
            public void onSuccess(QBUser user, Bundle args) {
                Log.e("QBUser", "" + user.getExternalId());
//                String registrationID = user.getId().toString();
//                subscribeToPushNotifications(registrationID);
            }

            @Override
            public void onError(QBResponseException error) {

            }
        });


    }

    public void subscribeToPushNotifications(String registrationID) {
        QBSubscription subscription = new QBSubscription(QBNotificationChannel.GCM);
        subscription.setEnvironment(QBEnvironment.DEVELOPMENT);
        //
        String deviceId;
        final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            deviceId = mTelephony.getDeviceId(); //*** use for mobiles
        } else {
            deviceId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID); //*** use for tablets
        }
        subscription.setDeviceUdid(deviceId);
        subscription.setRegistrationID(registrationID);
        //
        QBPushNotifications.createSubscription(subscription);
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
            Log.d("Log:", "error: " + ex);
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
            Log.d("Log:", "error: " + ex);
        } finally {
            realm.close();
            return claims;
        }
    }

    public static void setStatusBarTranslucent(Window window, boolean makeTranslucent) {
        if (makeTranslucent) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        Toast.makeText(context, com.happ.R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
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
