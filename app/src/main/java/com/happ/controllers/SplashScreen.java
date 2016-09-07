package com.happ.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.happ.App;
import com.happ.R;
import com.happ.models.User;
import com.happ.retrofit.HappRestClient;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.realm.Realm;

/**
 * Created by dante on 8/26/16.
 */
public class SplashScreen extends AppCompatActivity {
    ImageView mLogo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        mLogo = (ImageView)findViewById(R.id.img_login_logo);

        if (checkIsLoggedIn()) {
            if (checkCurrentUserExistence()) {
                HappRestClient.getInstance().refreshToken();
                if (checkCurrentCityExistence()) {
                    if (checkInterestsSelected()) {
                        goToFeed();
                    } else {
                        // Select Interests Page;
                    }
                } else {
                    // Select Current City Page
                }
            } else {
                goToLogin();
            }
        } else {
            goToLogin();
        }
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

    private boolean checkInterestsSelected() {
        return true;
    }

    private boolean checkCurrentCityExistence() {
        return true;
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
