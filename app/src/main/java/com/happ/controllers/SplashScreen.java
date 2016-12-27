package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.controllers_drawer.FeedActivity;
import com.happ.models.User;
import com.happ.retrofit.APIService;
import com.happ.retrofit.HappRestClient;

import java.util.Date;

import io.jsonwebtoken.Claims;

/**
 * Created by dante on 8/26/16.
 */
public class SplashScreen extends AppCompatActivity {

    BroadcastReceiver mCityLoadedBroadcastReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.splash_screen);

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mCityLoadedBroadcastReceiver == null) {
            mCityLoadedBroadcastReceiver = createCityLoadedBroadcastReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(mCityLoadedBroadcastReceiver, new IntentFilter(BroadcastIntents.CITY_REQUEST_OK));
        }
        checkIsEverythingOk();
    }

    @Override
    protected void onDestroy() {
        if (mCityLoadedBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(mCityLoadedBroadcastReceiver);
        super.onDestroy();
    }

    private void checkIsEverythingOk() {
        if (checkIsLoggedIn()) {
            if (checkCurrentUserExistence()) {
                HappRestClient.getInstance().refreshToken();
                if (checkCurrentCityExistence()) {
                    if (checkCityObjectExistence()) {
                        if (checkInterestsSelected()) {
                            goToFeed();
                        } else {
                            // Select Interests Page;
                        }
                    } else {
                        APIService.getCurrentCity();
                    }
                } else {
                    Intent intent = new Intent(this, CityActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }
            } else {
                goToLogin();
            }
        } else {
            goToLogin();
        }
    }

    private BroadcastReceiver createCityLoadedBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkIsEverythingOk();
            }
        };
    }

    private void goToFeed() {
        Intent intent = new Intent(this, FeedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    private boolean checkCityObjectExistence() {
        return App.getCurrentCity() != null;
    }

    private boolean checkInterestsSelected() {

        return true;
    }

    private boolean checkCurrentCityExistence() {
        return App.getCurrentUser().getSettings().getCity() != null;
    }

    private boolean checkCurrentUserExistence() {
        User user = App.getCurrentUser();
        return (user != null);
    }

    private boolean checkIsLoggedIn() {
        Claims claims = App.getTokenData();
        if (claims == null) return false;

        Date now = new Date();
        Date exp = claims.getExpiration();
        if (exp.before(now)) {
            App.doLogout(SplashScreen.this);
            return false;
        }
        return true;
    }
}
