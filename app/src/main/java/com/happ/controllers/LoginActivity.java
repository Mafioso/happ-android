package com.happ.controllers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.transition.Explode;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.LockableScrollView;
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
    private TextView mTVPrivacyPolicy, mTVTermsPolicy;
    private Button mBtnLogin;
    private ImageView mImageLogo;
    private RelativeLayout mFooterView;
    private boolean isKeyboarShown = false;
    private ViewTreeObserver.OnGlobalLayoutListener mKeyboardListener;
    private RelativeLayout mFormLayout;
    private MaterialProgressBar mProgressBar;
    private AppCompatImageView mIVbg;
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
    private BroadcastReceiver selectedInterestsReceiver;

    private boolean selectedInterestsAsked;

    int idx = new Random().nextInt(login_bg.length);
    int randomBg = login_bg[idx];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_form);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }

        mIVbg = (AppCompatImageView) findViewById(R.id.iv_login_bg);
        mEmail = (EditText) findViewById(R.id.input_login_username);
        mPassword = (EditText) findViewById(R.id.input_login_password);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mTvBtnCreateAccount = (TextView) findViewById(R.id.btn_create_new_account);
        mTvBtnForgotPw = (TextView) findViewById(R.id.btn_forgot_password);
        mImageLogo = (ImageView) findViewById(R.id.img_logo);
        mFooterView = (RelativeLayout) findViewById(R.id.rl_footer);
        mFormLayout = (RelativeLayout) findViewById(R.id.form_layout2);
        mProgressBar = (MaterialProgressBar) findViewById(R.id.circular_progress_login);
        mTVPrivacyPolicy = (TextView) findViewById(R.id.tv_privacy_policy);
        mTVTermsPolicy = (TextView) findViewById(R.id.tv_terms_and_policy);


        mBtnLogin.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mIVbg.setImageResource(randomBg);

        LockableScrollView sv = (LockableScrollView)findViewById(R.id.fake_scrollview);
        sv.setScrollingEnabled(false);


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
                Intent goToSignUp = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(goToSignUp);
            }
        });

        mTvBtnForgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToRecovePw = new Intent(getApplicationContext(), PasswordRecoveActivity.class);
                startActivity(goToRecovePw);
            }
        });

        mTVTermsPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotToTermsOfService = new Intent(getApplicationContext(), HtmlPageAcitivty.class);
                gotToTermsOfService.putExtra("link_terms_of_service", true);
                startActivity(gotToTermsOfService);
            }
        });

        mTVPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToPrivacyPolicy = new Intent(getApplicationContext(), HtmlPageAcitivty.class);
                goToPrivacyPolicy.putExtra("link_privacy_policy", true);
                startActivity(goToPrivacyPolicy);
            }
        });

        setListenerToRootView();
        setSpannableString();

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

        if (selectedInterestsReceiver == null) {
            selectedInterestsReceiver = getSelectedEventsSuccessReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(selectedInterestsReceiver, new IntentFilter(BroadcastIntents.SELECTED_INTERESTS_REQUEST_OK));
        }

    }


    private void setSpannableString() {
        SpannableString spanPrivacyPolicyString = new SpannableString(mTVPrivacyPolicy.getText());
        spanPrivacyPolicyString.setSpan(new UnderlineSpan(), 0, spanPrivacyPolicyString.length(), 0);
        mTVPrivacyPolicy.setText(spanPrivacyPolicyString);

        SpannableString spanTermsPolicyString = new SpannableString(mTVTermsPolicy.getText());
        spanTermsPolicyString.setSpan(new UnderlineSpan(), 0, spanTermsPolicyString.length(), 0);
        mTVTermsPolicy.setText(spanTermsPolicyString);

        SpannableString spanForgotPwString = new SpannableString(mTvBtnForgotPw.getText());
        spanForgotPwString.setSpan(new UnderlineSpan(), 0, spanForgotPwString.length(), 0);
        mTvBtnForgotPw.setText(spanForgotPwString);

        SpannableString spanCreateAccountString = new SpannableString(mTvBtnCreateAccount.getText());
        spanCreateAccountString.setSpan(new UnderlineSpan(), 0, spanCreateAccountString.length(), 0);
        mTvBtnCreateAccount.setText(spanCreateAccountString);

    }

    private void setListenerToRootView() {
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

    private static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    TextWatcher mWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            if ((TextUtils.isEmpty(mEmail.getText()))
                    || (TextUtils.isEmpty(mPassword.getText())))
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
            }
        };
    }

    private BroadcastReceiver createGetCurrentUserSuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                User currentUser = App.getCurrentUser();
                APIService.getSelectedInterests();
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

    private BroadcastReceiver getSelectedEventsSuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                selectedInterestsAsked = true;
                Intent goToFeedIntent = new Intent(LoginActivity.this, FeedActivity.class);
                goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToFeedIntent);
                overridePendingTransition(0, 0);
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
                } else if (selectedInterestsAsked){
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
        if (selectedInterestsReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(selectedInterestsReceiver);
            selectedInterestsReceiver = null;
        }

        super.onDestroy();
    }

}
