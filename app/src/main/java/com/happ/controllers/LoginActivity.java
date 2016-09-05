package com.happ.controllers;

import android.app.Service;
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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.HappToken;
import com.happ.retrofit.APIService;
import com.happ.retrofit.HappRestClient;

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
    FloatingActionButton mButtonFablogin;
    ImageButton mVisibility, mVisibilityOff;
    Button mButtonRegistration;
    ImageView mImageLogo;
    TextInputLayout mInputLayoutEmail, mInputLayoutPassword;
    LinearLayout mRegisterView;
    boolean isKeyboarShown = false;
    ViewTreeObserver.OnGlobalLayoutListener mKeyboardListener;

    private BroadcastReceiver loginRequestDoneReceiver;
    private BroadcastReceiver loginFailedReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mEmail = (EditText) findViewById(R.id.input_login_email);
        mPassword = (EditText) findViewById(R.id.input_login_password);
        mButtonFablogin = (FloatingActionButton) findViewById(R.id.login_fab);
        mButtonRegistration = (Button) findViewById(R.id.btn_registration_page);
        mImageLogo = (ImageView) findViewById(R.id.img_login_logo);
        mInputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_login_email);
        mInputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_login_password);
        mVisibility = (ImageButton) findViewById(R.id.btn_login_visibility);
        mVisibilityOff = (ImageButton) findViewById(R.id.btn_login_visibility_off);
        mVisibilityOff.setVisibility(View.GONE);
        mRegisterView = (LinearLayout) findViewById(R.id.ll_footer);

        if (loginRequestDoneReceiver == null) loginRequestDoneReceiver = createLoginSuccessReceiver();
        if (loginFailedReceiver == null) loginFailedReceiver = createLoginFailureReceiver();

        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(loginRequestDoneReceiver, new IntentFilter(BroadcastIntents.LOGIN_REQUEST_OK));
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(loginFailedReceiver, new IntentFilter(BroadcastIntents.LOGIN_REQUEST_FAIL));

        mButtonFablogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                HappRestClient client = HappRestClient.getInstance();
//                client.doLogin(mEmail.getText().toString(),mPassword.getText().toString());
                APIService.doLogin(mEmail.getText().toString(), mPassword.getText().toString());
            }
        });

        setListenerToRootView();

    }

    public void setListenerToRootView() {
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (mKeyboardListener == null) {
            mKeyboardListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                    if (heightDiff > 100) {
                        if (!isKeyboarShown) {
                            mImageLogo.setVisibility(View.GONE);
                            mRegisterView.setVisibility(View.GONE);
                        }
                        isKeyboarShown = true;
                    } else if (isKeyboarShown) {
                        mImageLogo.setVisibility(View.VISIBLE);
                        mRegisterView.setVisibility(View.VISIBLE);
                        isKeyboarShown = false;
                    }
                }
            };
        } else {
            activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(mKeyboardListener);
        }
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(mKeyboardListener);
    }


    public void btn_click_registration_page(View view) {

        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void btn_login_visibility(View view) {
        mVisibilityOff.setVisibility(View.VISIBLE);
        mVisibility.setVisibility(View.GONE);
        mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    public void btn_login_visibility_off(View view) {
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

//                APIService.getCurrentUser();
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
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (mKeyboardListener != null) activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(mKeyboardListener);
        if (loginRequestDoneReceiver != null) loginRequestDoneReceiver = null;
        if(loginFailedReceiver != null) loginFailedReceiver = null;
        super.onDestroy();
    }

}
