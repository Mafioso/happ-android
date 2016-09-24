package com.happ.controllers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.User;
import com.happ.retrofit.APIService;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by dante on 8/29/16.
 */
public class RegistrationActivity extends AppCompatActivity {

    EditText mUsername, mPassword, mRepeatPassword;
    TextInputLayout mInputLayoutUsername, mInputLayoutRepeatPassword, mInputLayoutPassword;
    FloatingActionButton mSignUpFab;
    ImageButton mPWVisibility, mPWVisibilityOff, mPWRVisibility, mPWRVisibilityOff;
    private BroadcastReceiver signUpRequestDoneReceiver;
    private BroadcastReceiver getSignUpRequestFail;
    private BroadcastReceiver currentUserDoneReceiver;
    private BroadcastReceiver currentCityDoneReceiver;

    boolean isKeyboarShown = false;
    ViewTreeObserver.OnGlobalLayoutListener mKeyboardListener;
    RelativeLayout mFormLayout;
    MaterialProgressBar mProgressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form);

        if (signUpRequestDoneReceiver == null) signUpRequestDoneReceiver = createSignUpSuccessReceiver();
        if (currentUserDoneReceiver == null) currentUserDoneReceiver = createGetCurrentUserSuccessReceiver();

        if (getSignUpRequestFail == null) getSignUpRequestFail = createSignUpFailureReceiver();
        if (currentCityDoneReceiver == null) currentCityDoneReceiver = createGetCurrentCitySuccessReceiver();

        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(signUpRequestDoneReceiver, new IntentFilter(BroadcastIntents.SIGNUP_REQUEST_OK));
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(getSignUpRequestFail, new IntentFilter(BroadcastIntents.SIGNUP_REQUEST_FAIL));
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(currentUserDoneReceiver, new IntentFilter(BroadcastIntents.GET_CURRENT_USER_REQUEST_OK));
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(currentCityDoneReceiver, new IntentFilter(BroadcastIntents.CITY_REQUEST_OK));


        mUsername = (EditText) findViewById(R.id.input_signup_username);
        mPassword = (EditText) findViewById(R.id.input_signup_password);
        mRepeatPassword = (EditText) findViewById(R.id.input_signup_repeat_password);
        mInputLayoutUsername = (TextInputLayout) findViewById(R.id.input_layout_signup_username);
        mInputLayoutRepeatPassword = (TextInputLayout) findViewById(R.id.input_layout_signup_repeat_password);
        mInputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_signup_password);
        mPWVisibility = (ImageButton) findViewById(R.id.btn_signup_pw_visibility);
        mPWVisibilityOff = (ImageButton) findViewById(R.id.btn_signup_pw_visibility_off);
        mPWRVisibility = (ImageButton) findViewById(R.id.btn_signup_pwr_visibility);
        mPWRVisibilityOff = (ImageButton) findViewById(R.id.btn_signup_pwr_visibility_off);
        mPWRVisibilityOff.setVisibility(View.GONE);
        mPWVisibilityOff.setVisibility(View.GONE);
        mSignUpFab = (FloatingActionButton) findViewById(R.id.signup_fab);
        mSignUpFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIService.doSignUp(mUsername.getText().toString(), mPassword.getText().toString());
            }
        });
        signUpRequestDoneReceiver = createSignUpSuccessReceiver();
        mProgressBar = (MaterialProgressBar) findViewById(R.id.circular_progress_signup);
        mFormLayout = (RelativeLayout) findViewById(R.id.form_layout);

        checkValidation();

        mUsername.addTextChangedListener(mWatcher);
        mPassword.addTextChangedListener(mWatcher);
        mRepeatPassword.addTextChangedListener(mWatcher);

        mSignUpFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPassword.getText().toString().equals(mRepeatPassword.getText().toString())) {
                    hideSoftKeyboard(RegistrationActivity.this, v);
                    mSignUpFab.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    APIService.doSignUp(mUsername.getText().toString(), mPassword.getText().toString());
                } else {
                    Toast.makeText(RegistrationActivity.this, "Пароли не совпадают", Toast.LENGTH_LONG).show();
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.getBackground().setAlpha(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                RegistrationActivity.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
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
                    Rect r = new Rect();
                    activityRootView.getWindowVisibleDisplayFrame(r);
                    int screenHeight = activityRootView.getRootView().getHeight();

                    int keypadHeight = screenHeight - r.bottom;
                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        if (!isKeyboarShown) {
//                            mImageLogo.setVisibility(View.GONE);
//                            mRegisterView.setVisibility(View.GONE);
                        }
                        isKeyboarShown = true;
                    }
                    else {
//                        mImageLogo.setVisibility(View.VISIBLE);
//                        mRegisterView.setVisibility(View.VISIBLE);
                        isKeyboarShown = false;
                    }
                }
            };
        } else {
            activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(mKeyboardListener);
        }
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(mKeyboardListener);
    }

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private void checkValidation() {

        if ((TextUtils.isEmpty(mUsername.getText()))
                || (TextUtils.isEmpty(mPassword.getText()))
                || (TextUtils.isEmpty(mRepeatPassword.getText()))
                )
            mSignUpFab.setVisibility(View.INVISIBLE);
        else
            mSignUpFab.setVisibility(View.VISIBLE);

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

//                Realm realm = Realm.getDefaultInstance();
//                HappToken token = realm.where(HappToken.class).findFirst();
//                token = realm.copyFromRealm(token);
//                realm.close();
                APIService.getCurrentUser();
            }
        };
    }

    private BroadcastReceiver createSignUpFailureReceiver() {
        return  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mSignUpFab.setVisibility(View.VISIBLE);
                Toast.makeText(RegistrationActivity.this, "Wrong Username or Password", Toast.LENGTH_LONG).show();
            }
        };
    }


    private BroadcastReceiver createGetCurrentCitySuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mProgressBar.setVisibility(View.INVISIBLE);

                Intent goToFeedIntent = new Intent(RegistrationActivity.this, CityActivity.class);
                goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToFeedIntent);
                overridePendingTransition(0,0);

            }
        };
    }

    private BroadcastReceiver createGetCurrentUserSuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                User currentUser = App.getCurrentUser();
                if (currentUser.getSettings().getCity() != null) {
                    APIService.getCurrentCity();
//                    HappRestClient.getInstance().getCurrentCity();
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Intent goToFeedIntent = new Intent(RegistrationActivity.this, CityActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (mKeyboardListener != null)
            activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(mKeyboardListener);
        if (signUpRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(signUpRequestDoneReceiver);
            signUpRequestDoneReceiver = null;
        }

        if (getSignUpRequestFail != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(getSignUpRequestFail);
            getSignUpRequestFail = null;
        }
        if (currentUserDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(currentUserDoneReceiver);
            currentUserDoneReceiver = null;
        }
        if (currentCityDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(currentCityDoneReceiver);
            currentCityDoneReceiver = null;
        }
        super.onDestroy();
    }
}
