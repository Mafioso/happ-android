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
import android.support.v7.app.AppCompatDelegate;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Explode;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
public class LoginActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    EditText mEmail, mPassword;
    Button mButtonRegistration;
    FloatingActionButton mButtonFablogin;
    ImageView mImageLogo;
    TextInputLayout mInputLayoutEmail, mInputLayoutPassword;
    LinearLayout mRegisterView;
    boolean isKeyboarShown = false;
    ViewTreeObserver.OnGlobalLayoutListener mKeyboardListener;
    RelativeLayout mFormLayout;
    MaterialProgressBar mProgressBar;
    int[] mInsets = new int[3];

    private BroadcastReceiver loginRequestDoneReceiver;
    private BroadcastReceiver loginFailedReceiver;
    private BroadcastReceiver currentUserDoneReceiver;
    private BroadcastReceiver currentCityDoneReceiver;
    private PrefManager prefManager;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }

        mEmail = (EditText) findViewById(R.id.input_login_email);
        mPassword = (EditText) findViewById(R.id.input_login_password);
        mButtonFablogin = (FloatingActionButton) findViewById(R.id.login_fab);
        mButtonRegistration = (Button) findViewById(R.id.btn_registration_page);
        mImageLogo = (ImageView) findViewById(R.id.img_login_logo);
        mInputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_login_email);
        mInputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_login_password);
//        mVisibility = (ImageButton) findViewById(R.id.btn_login_visibility);
//        mVisibilityOff = (ImageButton) findViewById(R.id.btn_login_visibility_off);
//        mVisibilityOff.setVisibility(View.GONE);
        mRegisterView = (LinearLayout) findViewById(R.id.ll_footer);
        mFormLayout = (RelativeLayout) findViewById(R.id.form_layout2);
        mProgressBar = (MaterialProgressBar) findViewById(R.id.circular_progress_login);


        if (loginRequestDoneReceiver == null) loginRequestDoneReceiver = createLoginSuccessReceiver();
        if (loginFailedReceiver == null) loginFailedReceiver = createLoginFailureReceiver();
        if (currentUserDoneReceiver == null) currentUserDoneReceiver = createGetCurrentUserSuccessReceiver();
        if (currentCityDoneReceiver == null) currentCityDoneReceiver = createGetCurrentCitySuccessReceiver();

        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(loginRequestDoneReceiver, new IntentFilter(BroadcastIntents.LOGIN_REQUEST_OK));
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(loginFailedReceiver, new IntentFilter(BroadcastIntents.LOGIN_REQUEST_FAIL));
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(currentUserDoneReceiver, new IntentFilter(BroadcastIntents.GET_CURRENT_USER_REQUEST_OK));
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(currentCityDoneReceiver, new IntentFilter(BroadcastIntents.CITY_REQUEST_OK));

        checkValidation();

        mEmail.addTextChangedListener(mWatcher);
        mPassword.addTextChangedListener(mWatcher);

        mButtonFablogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideSoftKeyboard(LoginActivity.this, v);
                mButtonFablogin.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                APIService.doLogin(mEmail.getText().toString(), mPassword.getText().toString());
//                HappRestClient.getInstance().doLogin(mEmail.getText().toString(), mPassword.getText().toString());
            }
        });

        mButtonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_click_registration_page(v);
            }
        });

        setListenerToRootView();

    }



//    private void isLoggedIn() {
//        Claims claims = App.getTokenData();
//        HappRestClient.getInstance().refreshToken();
//    }

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
                            mImageLogo.setVisibility(View.GONE);
                            mRegisterView.setVisibility(View.GONE);
                        }
                        isKeyboarShown = true;
                    }
                    else {
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

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }


    private void checkValidation() {

        if ((TextUtils.isEmpty(mEmail.getText()))
                || (TextUtils.isEmpty(mPassword.getText())))
            mButtonFablogin.setVisibility(View.INVISIBLE);
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
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
    }
//
//    public void btn_click_login_visibility(View view) {
//        mVisibilityOff.setVisibility(View.VISIBLE);
//        mVisibility.setVisibility(View.GONE);
//        mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//    }
//
//    public void btn_click_login_visibility_off(View view) {
//        mVisibility.setVisibility(View.VISIBLE);
//        mVisibilityOff.setVisibility(View.GONE);
//        mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//    }

    private BroadcastReceiver createLoginSuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                APIService.getCurrentUser();
//                HappRestClient.getInstance().getCurrentUser();
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
                    Intent goToFeedIntent = new Intent(LoginActivity.this, CityActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);
                }
            }
        };
    }

    private BroadcastReceiver createGetCurrentCitySuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mProgressBar.setVisibility(View.INVISIBLE);
                User currentUser = App.getCurrentUser();
                if (currentUser.getInterests() != null && currentUser.getInterests().size() > 0) {
                    Intent goToFeedIntent = new Intent(LoginActivity.this, FeedActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0, 0);

                } else {

                    Intent goToFeedIntent = new Intent(LoginActivity.this, SelectInterestsActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0, 0);

                }

            }
        };
    }

    private BroadcastReceiver createLoginFailureReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mButtonFablogin.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, "Wrong Username or Password", Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onDestroy() {
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (mKeyboardListener != null)
            activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(mKeyboardListener);
        if (loginRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(loginRequestDoneReceiver);
            loginRequestDoneReceiver = null;
        }
        if (loginFailedReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(loginFailedReceiver);
            loginFailedReceiver = null;
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
