package com.happ.controllers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.controllers_drawer.FeedActivity;
import com.happ.controllers_drawer.SelectInterestsActivity;
import com.happ.models.User;
import com.happ.retrofit.APIService;

import java.util.Random;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by dante on 8/29/16.
 */
public class LoginActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private EditText mEmail, mPassword;
    private TextView mTvBtnCreateAccount, mTvBtnForgotPw;
    private Button mBtnLogin;
    private ImageView mImageLogo;
    private RelativeLayout mFooterView;
    private boolean isKeyboarShown = false;
    private ViewTreeObserver.OnGlobalLayoutListener mKeyboardListener;
    private RelativeLayout mFormLayout;
    private MaterialProgressBar mProgressBar;
    private RelativeLayout mRLbg;
    private int[] mInsets = new int[3];

    private int[] login_bg = {
            R.drawable.login_bg_1,
            R.drawable.login_bg_2,
            R.drawable.login_bg_3,
            R.drawable.login_bg_4,
            R.drawable.login_bg_5,
            R.drawable.login_bg_6,
            R.drawable.login_bg_7,
            R.drawable.login_bg_8,
            R.drawable.login_bg_9,
            R.drawable.login_bg_10,
    };


    private BroadcastReceiver loginRequestDoneReceiver;
    private BroadcastReceiver loginFailedReceiver;
    private BroadcastReceiver currentUserDoneReceiver;
    private BroadcastReceiver currentCityDoneReceiver;
    private PrefManager prefManager;
    int idx = new Random().nextInt(login_bg.length);
    int randomBg = login_bg[idx];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_form);

        mRLbg = (RelativeLayout) findViewById(R.id.rl_login_bg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }

        mEmail = (EditText) findViewById(R.id.input_login_username);
        mPassword = (EditText) findViewById(R.id.input_login_password);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mTvBtnCreateAccount = (TextView) findViewById(R.id.btn_create_new_account);
        mTvBtnForgotPw = (TextView) findViewById(R.id.btn_forgot_password);
        mImageLogo = (ImageView) findViewById(R.id.img_logo);
        mFooterView = (RelativeLayout) findViewById(R.id.rl_footer);
        mFormLayout = (RelativeLayout) findViewById(R.id.form_layout2);
        mProgressBar = (MaterialProgressBar) findViewById(R.id.circular_progress_login);

        mBtnLogin.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);


        mRLbg.setBackground(ContextCompat.getDrawable(App.getContext(), randomBg));

        if (loginRequestDoneReceiver == null) {
            loginRequestDoneReceiver = createLoginSuccessReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(loginRequestDoneReceiver, new IntentFilter(BroadcastIntents.LOGIN_REQUEST_OK));
        }

        if (loginFailedReceiver == null) {
            loginFailedReceiver = createLoginFailureReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(loginFailedReceiver, new IntentFilter(BroadcastIntents.LOGIN_REQUEST_FAIL));
        }

        if (currentUserDoneReceiver == null) {
            currentUserDoneReceiver = createGetCurrentUserSuccessReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(currentUserDoneReceiver, new IntentFilter(BroadcastIntents.GET_CURRENT_USER_REQUEST_OK));
        }

        if (currentCityDoneReceiver == null) {
            currentCityDoneReceiver = createGetCurrentCitySuccessReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(currentCityDoneReceiver, new IntentFilter(BroadcastIntents.CITY_REQUEST_OK));
        }

        mEmail.addTextChangedListener(mWatcher);
        mPassword.addTextChangedListener(mWatcher);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideSoftKeyboard(LoginActivity.this, v);
                mBtnLogin.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                APIService.doLogin(mEmail.getText().toString(), mPassword.getText().toString());
//                HappRestClient.getInstance().doLogin(mEmail.getText().toString(), mPassword.getText().toString());
            }
        });

        mTvBtnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
            }
        });

        mTvBtnForgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PasswordRecoveActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
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
                            mFooterView.setVisibility(View.GONE);
                        }
                        isKeyboarShown = true;
                    }
                    else {
                        mImageLogo.setVisibility(View.VISIBLE);
                        mFooterView.setVisibility(View.VISIBLE);
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

    TextWatcher mWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            if ((TextUtils.isEmpty(mEmail.getText()))
                    && (TextUtils.isEmpty(mPassword.getText())))
                mBtnLogin.setVisibility(View.INVISIBLE);
            else
                mBtnLogin.setVisibility(View.VISIBLE);
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
                mProgressBar.setVisibility(View.GONE);
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
                mProgressBar.setVisibility(View.GONE);
                mBtnLogin.setVisibility(View.VISIBLE);
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

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mRLbg.setBackground(ContextCompat.getDrawable(App.getContext(), randomBg));
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        mRLbg.setBackground(ContextCompat.getDrawable(App.getContext(), randomBg));
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mRLbg.setBackground(ContextCompat.getDrawable(App.getContext(), randomBg));
//    }
}
