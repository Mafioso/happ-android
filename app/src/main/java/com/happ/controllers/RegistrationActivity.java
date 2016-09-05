package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.HappToken;
import com.happ.retrofit.APIService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.realm.Realm;

/**
 * Created by dante on 8/29/16.
 */
public class RegistrationActivity extends AppCompatActivity {

    EditText mEmail, mUsername, mPassword, mRepeatPassword;
    TextInputLayout mInputLayoutUsername, mInputLayoutEmail, mInputLayoutRepeatPassword, mInputLayoutPassword;
    FloatingActionButton mSignUpFab;
    ImageButton mPWVisibility, mPWVisibilityOff, mPWRVisibility, mPWRVisibilityOff;
    private BroadcastReceiver signUpRequestDoneReceiver;
    private BroadcastReceiver getSignUpRequestFail;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mEmail = (EditText) findViewById(R.id.input_signup_email);
        mEmail.setVisibility(View.GONE);

        mUsername = (EditText) findViewById(R.id.input_signup_username);
        mPassword = (EditText) findViewById(R.id.input_signup_password);
        mRepeatPassword = (EditText) findViewById(R.id.input_signup_repeat_password);

        mInputLayoutUsername = (TextInputLayout) findViewById(R.id.input_layout_signup_username);
        mInputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_signup_email);
        mInputLayoutRepeatPassword = (TextInputLayout) findViewById(R.id.input_layout_signup_repeat_password);
        mInputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_signup_password);

        mPWVisibility = (ImageButton) findViewById(R.id.btn_signup_pw_visibility);
        mPWVisibilityOff = (ImageButton) findViewById(R.id.btn_signup_pw_visibility_off);
        mPWRVisibility = (ImageButton) findViewById(R.id.btn_signup_pwr_visibility);
        mPWRVisibilityOff = (ImageButton) findViewById(R.id.btn_signup_pwr_visibility_off);

        mPWRVisibilityOff.setVisibility(View.GONE);
        mPWVisibilityOff.setVisibility(View.GONE);

        mSignUpFab = (FloatingActionButton) findViewById(R.id.signup_fab);

        signUpRequestDoneReceiver = createSignUpSuccessReceiver();

        if (signUpRequestDoneReceiver == null) signUpRequestDoneReceiver = createSignUpSuccessReceiver();
        if (getSignUpRequestFail == null) getSignUpRequestFail = createSignUpFailureReceiver();

        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(signUpRequestDoneReceiver, new IntentFilter(BroadcastIntents.SIGNUP_REQUEST_OK));

        mSignUpFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPassword.getText().toString().equals(mRepeatPassword.getText().toString())) {
                    APIService.doSignUp(mUsername.getText().toString(), mPassword.getText().toString());
                } else {
                    Toast.makeText(RegistrationActivity.this, "Пароли не совпадают", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void btn_click_signup_pw_visibility(View view) {
        mPWVisibilityOff.setVisibility(View.VISIBLE);
        mPWVisibility.setVisibility(View.GONE);
        mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    public void btn_click_signup_pw_visibility_off(View view) {
        mPWVisibility.setVisibility(View.VISIBLE);
        mPWVisibilityOff.setVisibility(View.GONE);
        mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    public void btn_click_signup_pwr_visibility(View view) {
        mPWRVisibilityOff.setVisibility(View.VISIBLE);
        mPWRVisibility.setVisibility(View.GONE);
        mRepeatPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    public void btn_click_signup_pwr_visibility_off(View view) {
        mPWRVisibility.setVisibility(View.VISIBLE);
        mPWRVisibilityOff.setVisibility(View.GONE);
        mRepeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private BroadcastReceiver createSignUpSuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Realm realm = Realm.getDefaultInstance();
                HappToken token = realm.where(HappToken.class).findFirst();
                token = realm.copyFromRealm(token);
                realm.close();

                int i = token.getToken().lastIndexOf('.');
                String unsignedToken = token.getToken().substring(0,i+1);
                Jwt<Header,Claims> tokenData = Jwts.parser().parseClaimsJwt(unsignedToken);
                String username = tokenData.getBody().getSubject();

                APIService.getUser(username);
            }
        };
    }

    private BroadcastReceiver createSignUpFailureReceiver() {
        return  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(RegistrationActivity.this, "Введен не верный логин или пароль", Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onDestroy() {
        if (signUpRequestDoneReceiver != null) signUpRequestDoneReceiver = null;
        if(getSignUpRequestFail != null) getSignUpRequestFail = null;
        super.onDestroy();
    }
}
