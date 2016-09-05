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
import android.support.v7.app.AppCompatDelegate;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
public class LoginActivity extends AppCompatActivity {

    static
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    EditText mEmail, mPassword;
    ImageButton mVisibility, mVisibilityOff;
    FloatingActionButton mButtonFablogin;
//    TextView mButtonRegistration;
    ImageView mImageLogo;
    TextInputLayout mInputLayoutEmail, mInputLayoutPassword;

    private BroadcastReceiver loginRequestDoneReceiver;
    private BroadcastReceiver loginFaildReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);

        mEmail = (EditText) findViewById(R.id.input_login_email);
        mPassword = (EditText) findViewById(R.id.input_login_password);
        mButtonFablogin = (FloatingActionButton) findViewById(R.id.login_fab);

//        mButtonRegistration = (TextView) findViewById(R.id.btn_registration_page);
        mImageLogo = (ImageView) findViewById(R.id.img_login_logo);
        mInputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_login_email);
        mInputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_login_password);
        mVisibility = (ImageButton) findViewById(R.id.btn_login_visibility);
        mVisibilityOff = (ImageButton) findViewById(R.id.btn_login_visibility_off);
        mVisibilityOff.setVisibility(View.GONE);


        if (loginRequestDoneReceiver == null) loginRequestDoneReceiver = createLoginSuccessReceiver();
        if (loginFaildReceiver == null) loginFaildReceiver = createLoginFailureReceiver();

        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(loginRequestDoneReceiver, new IntentFilter(BroadcastIntents.LOGIN_REQUEST_OK));
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(loginFaildReceiver, new IntentFilter(BroadcastIntents.LOGIN_REQUEST_FAIL));

        checkValidation();

        mEmail.addTextChangedListener(mWatcher);
        mPassword.addTextChangedListener(mWatcher);

        mButtonFablogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                APIService.doLogin(mEmail.getText().toString(), mPassword.getText().toString());
            }
        });

    }



    private void checkValidation() {

        if ((TextUtils.isEmpty(mEmail.getText()))
                || (TextUtils.isEmpty(mPassword.getText())))
            mButtonFablogin.setVisibility(View.GONE);
        else
            mButtonFablogin.setVisibility(View.VISIBLE);

    }

    TextWatcher mWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub
            checkValidation();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };

    public void btn_click_registration_page(View view) {

        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void btn_click_login_visibility(View view) {
        mVisibilityOff.setVisibility(View.VISIBLE);
        mVisibility.setVisibility(View.GONE);
        mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    public void btn_click_login_visibility_off(View view) {
        mVisibility.setVisibility(View.VISIBLE);
        mVisibilityOff.setVisibility(View.GONE);
        mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private BroadcastReceiver createLoginSuccessReceiver() {
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

    private BroadcastReceiver createLoginFailureReceiver() {
        return  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(LoginActivity.this, "Введен не верный логин или пароль", Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onDestroy() {
        if (loginRequestDoneReceiver != null) loginRequestDoneReceiver = null;
        if(loginFaildReceiver != null) loginFaildReceiver = null;
        super.onDestroy();
    }

}
