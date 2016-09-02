package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    EditText mUsername, mPassword, mRepeatPassword;
    Button mButton_SignUp;
    private BroadcastReceiver signUpRequestDoneReceiver;
    private BroadcastReceiver getSignUpRequestFail;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form);

        View mEmail = findViewById(R.id.sign_up_email);
        mEmail.setVisibility(View.GONE);

        mUsername = (EditText) findViewById(R.id.sign_up_username);
        mPassword = (EditText) findViewById(R.id.sign_up_password);
        mRepeatPassword = (EditText) findViewById(R.id.sing_up_repeat_password);

        mButton_SignUp = (Button) findViewById(R.id.btn_sign_up);

        signUpRequestDoneReceiver = createSignUpSuccessReceiver();

        if (signUpRequestDoneReceiver == null) signUpRequestDoneReceiver = createSignUpSuccessReceiver();
        if (getSignUpRequestFail == null) getSignUpRequestFail = createSignUpFailureReceiver();

        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(signUpRequestDoneReceiver, new IntentFilter(BroadcastIntents.SIGNUP_REQUEST_OK));

    }


    public void btn_click_sign_up(View view) {

        mButton_SignUp.setOnClickListener(new View.OnClickListener() {
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
