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
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.LockableScrollView;
import com.happ.R;
import com.happ.controllers_drawer.FeedActivity;
import com.happ.controllers_drawer.SelectInterestsActivity;
import com.happ.models.User;
import com.happ.retrofit.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Random;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by dante on 8/29/16.
 */
public class LoginActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private CallbackManager mCallbackManager;
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
    private Button mButtonFacebookLogin;

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
    private BroadcastReceiver facebookLoginRequestDoneReceiver;
    private BroadcastReceiver facebookRegisterRequestDoneReceiver;
    private BroadcastReceiver facebookLoginRequestFAILReceiver;

    private String saved_facebook_id = "";
    private int saved_gender;
    private String saved_email = "";
    private String saved_fullname = "";

    private boolean selectedInterestsAsked;

    private String TAG = "FACEBOOK LOGIN";

    int idx = new Random().nextInt(login_bg.length);
    int randomBg = login_bg[idx];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "kz.happappinfo",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        AppEventsLogger.activateApp(this);
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        Log.e(TAG, "Facebook getApplicationId: " + loginResult.getAccessToken().getApplicationId());
                        Log.d(TAG, "Facebook getToken: " + loginResult.getAccessToken().getToken());
                        Log.d(TAG, "Facebook getUserId: " + loginResult.getAccessToken().getUserId());
                        Log.d(TAG, "Facebook getExpires: " + loginResult.getAccessToken().getExpires());
                        Log.d(TAG, "Facebook getLastRefresh: " + loginResult.getAccessToken().getLastRefresh());

                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        try {

                                            //check is response is not empty
                                            if (response.getError() == null){

                                                //parse json
                                                object = new JSONObject(response.getRawResponse().toString());

                                                String id = object.getString("id");
                                                String email = object.getString("email");
                                                String name = object.getString("name");
                                                String gender = object.getString("gender");

                                                saved_facebook_id = id;
                                                if (gender.equals("male")) {
                                                    saved_gender = 0;
                                                } else {
                                                    saved_gender = 1;
                                                }
                                                saved_email = email;
                                                saved_fullname = name;

                                                Log.e("FB SDK", "id = " + id);
                                                Log.e("FB SDK", "email = " + email);
                                                Log.e("FB SDK", "name = " + name);
                                                Log.e("FB SDK", "gender = " + gender);

                                                APIService.doFacebookLogin(id);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,email,gender,name");
                        request.setParameters(parameters);
                        request.executeAsync();


                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, exception.getMessage());
                    }
                });

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
        mButtonFacebookLogin = (Button) findViewById(R.id.button_fb_login);


        mBtnLogin.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mIVbg.setImageResource(randomBg);

        LockableScrollView sv = (LockableScrollView)findViewById(R.id.fake_scrollview);
        sv.setScrollingEnabled(false);


        mEmail.addTextChangedListener(mWatcher);
        mPassword.addTextChangedListener(mWatcher);

        mButtonFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
                loginManager.logInWithReadPermissions(
                        LoginActivity.this,
                        Arrays.asList("public_profile", "user_friends", "email"));
            }
        });

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

        if (facebookLoginRequestDoneReceiver == null) {
            facebookLoginRequestDoneReceiver = facebookLoginSuccessReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(facebookLoginRequestDoneReceiver, new IntentFilter(BroadcastIntents.FACEBOOK_LOGIN_REQUEST_OK));
        }

        if (facebookLoginRequestFAILReceiver == null) {
            facebookLoginRequestFAILReceiver = facebookLoginFailSuccessReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(facebookLoginRequestFAILReceiver, new IntentFilter(BroadcastIntents.FACEBOOK_LOGIN_REQUEST_FAIL));
        }

        if (facebookRegisterRequestDoneReceiver == null) {
            facebookRegisterRequestDoneReceiver = facebookRegisterSuccessReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(facebookRegisterRequestDoneReceiver, new IntentFilter(BroadcastIntents.FACEBOOK_REGISTER_REQUEST_OK));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
//                User currentUser = App.getCurrentUser();
                APIService.getSelectedInterests();
//                if (currentUser.getSettings().getCity() != null) {
//                    APIService.getCurrentCity();
//                } else {
//                    Intent goToFeedIntent = new Intent(LoginActivity.this, CityActivity.class);
//                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    startActivity(goToFeedIntent);
//                    overridePendingTransition(0,0);
//                }
            }
        };
    }

    private BroadcastReceiver getSelectedEventsSuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                selectedInterestsAsked = true;
                User currentUser = App.getCurrentUser();
                if (currentUser.getSettings().getCity() != null) {
                    APIService.getCurrentCity();
                } else {
                    Intent goToFeedIntent = new Intent(LoginActivity.this, CityActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);
                }
//                Intent goToFeedIntent = new Intent(LoginActivity.this, FeedActivity.class);
//                goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(goToFeedIntent);
//                overridePendingTransition(0, 0);
            }
        };
    }

    private BroadcastReceiver facebookLoginSuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                APIService.getCurrentUser();
            }
        };
    }

    private BroadcastReceiver facebookLoginFailSuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                APIService.doFacebookRegister(saved_facebook_id, saved_fullname, saved_gender, saved_email);
            }
        };
    }

    private BroadcastReceiver facebookRegisterSuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                APIService.getCurrentUser();
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
        if (facebookLoginRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(facebookLoginRequestDoneReceiver);
            facebookLoginRequestDoneReceiver = null;
        }

        super.onDestroy();
    }

}
