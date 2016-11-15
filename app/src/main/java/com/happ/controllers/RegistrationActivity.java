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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.User;
import com.happ.retrofit.APIService;

import java.util.Random;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by dante on 8/29/16.
 */
public class RegistrationActivity extends AppCompatActivity {
    private CallbackManager mCallbackManager;

    private EditText mUsername, mPassword, mRepeatPassword;
    private Button mCreateAccountButton;
    private BroadcastReceiver signUpRequestDoneReceiver;
    private BroadcastReceiver getSignUpRequestFail;
    private BroadcastReceiver currentUserDoneReceiver;
    private BroadcastReceiver currentCityDoneReceiver;
    private RelativeLayout mRLbg;
    private Toolbar toolbar;
    private ImageView mImgLogo;
    private RelativeLayout mRLFooter;

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

    int idx = new Random().nextInt(login_bg.length);
    int randomBg = login_bg[idx];

    boolean isKeyboarShown = false;
    ViewTreeObserver.OnGlobalLayoutListener mKeyboardListener;
    RelativeLayout mFormLayout;
    MaterialProgressBar mProgressBar;
    private static final String TAG = "myLogs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        FacebookSdk.sdkInitialize(this.getApplicationContext());
//        mCallbackManager = CallbackManager.Factory.create();
//
//        LoginManager.getInstance().registerCallback(mCallbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        Log.d("Success", "Login");
//                        Log.e(TAG, "Facebook getApplicationId: " + loginResult.getAccessToken().getApplicationId());
//                        Log.d(TAG, "Facebook getToken: " + loginResult.getAccessToken().getToken());
//                        Log.d(TAG, "Facebook getUserId: " + loginResult.getAccessToken().getUserId());
//                        Log.d(TAG, "Facebook getExpires: " + loginResult.getAccessToken().getExpires());
//                        Log.d(TAG, "Facebook getLastRefresh: " + loginResult.getAccessToken().getLastRefresh());
//
//                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//                        GraphRequest request = GraphRequest.newMeRequest(
//                                accessToken,
//                                new GraphRequest.GraphJSONObjectCallback() {
//                                    @Override
//                                    public void onCompleted(JSONObject object, GraphResponse response) {
//                                        try {
//
//                                            //check is response is not empty
//                                            if (response.getError() == null){
//
//                                                //parse json
//                                                object = new JSONObject(response.getRawResponse().toString());
//
//                                                String id = object.getString("id");
//                                                String email = object.getString("email");
//
//                                                APIService.doSignUp(email, id);
//                                            }
//
//
//
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
//
//                        Bundle parameters = new Bundle();
//                        parameters.putString("fields", "id,email");
//                        request.setParameters(parameters);
//                        request.executeAsync();
//
//
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Toast.makeText(RegistrationActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        Toast.makeText(RegistrationActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
//                        Log.d(TAG, exception.getMessage());
//                    }
//                });

        setContentView(R.layout.registration_form);

        mRLbg = (RelativeLayout) findViewById(R.id.rl_registration_bg);
        mUsername = (EditText) findViewById(R.id.input_signup_username);
        mPassword = (EditText) findViewById(R.id.input_signup_password);
        mRepeatPassword = (EditText) findViewById(R.id.input_signup_repeat_pw);
        mCreateAccountButton = (Button) findViewById(R.id.btn_create_account);
        mProgressBar = (MaterialProgressBar) findViewById(R.id.circular_progress_signup);
        mFormLayout = (RelativeLayout) findViewById(R.id.form_layout);
        mImgLogo = (ImageView) findViewById(R.id.img_logo);
        mRLFooter = (RelativeLayout) findViewById(R.id.rl_footer);

        mRLbg.setBackground(ContextCompat.getDrawable(App.getContext(), randomBg));

        mCreateAccountButton.setVisibility(View.INVISIBLE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_rigth_arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }

        mUsername.addTextChangedListener(mWatcher);
        mPassword.addTextChangedListener(mWatcher);
        mRepeatPassword.addTextChangedListener(mWatcher);

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPassword.getText().toString().equals(mRepeatPassword.getText().toString())) {
                    hideSoftKeyboard(RegistrationActivity.this, view);
                    mCreateAccountButton.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    APIService.doSignUp(mUsername.getText().toString(), mPassword.getText().toString());
                } else {
                    Toast.makeText(RegistrationActivity.this, "Пароли не совпадают", Toast.LENGTH_LONG).show();
                }
            }
        });

//        facebookButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LoginManager loginManager = LoginManager.getInstance();
//                loginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
//                loginManager.logInWithReadPermissions(
//                        RegistrationActivity.this,
//                        Arrays.asList("public_profile", "user_friends", "email"));
//            }
//        });

        setListenerToRootView();

        if (signUpRequestDoneReceiver == null) {
            signUpRequestDoneReceiver = createSignUpSuccessReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(signUpRequestDoneReceiver, new IntentFilter(BroadcastIntents.SIGNUP_REQUEST_OK));
        }
        if (currentUserDoneReceiver == null) {
            currentUserDoneReceiver = createGetCurrentUserSuccessReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(currentUserDoneReceiver, new IntentFilter(BroadcastIntents.GET_CURRENT_USER_REQUEST_OK));
        }
        if (getSignUpRequestFail == null) {
            getSignUpRequestFail = createSignUpFailureReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(getSignUpRequestFail, new IntentFilter(BroadcastIntents.SIGNUP_REQUEST_FAIL));
        }
        if (currentCityDoneReceiver == null) {
            currentCityDoneReceiver = createGetCurrentCitySuccessReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(currentCityDoneReceiver, new IntentFilter(BroadcastIntents.CITY_REQUEST_OK));
        }
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
                            mImgLogo.setVisibility(View.GONE);
                            mRLFooter.setVisibility(View.GONE);

                        }
                        isKeyboarShown = true;
                    }
                    else {
                        mImgLogo.setVisibility(View.VISIBLE);
                        mRLFooter.setVisibility(View.VISIBLE);
                        isKeyboarShown = false;
                    }
                }
            };
        } else {
            activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(mKeyboardListener);
        }
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(mKeyboardListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }


    TextWatcher mWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if ((TextUtils.isEmpty(mUsername.getText()))
                    || (TextUtils.isEmpty(mPassword.getText()))
                    || (TextUtils.isEmpty(mRepeatPassword.getText()))
                    )
                mCreateAccountButton.setVisibility(View.INVISIBLE);
            else
                mCreateAccountButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };


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
                mCreateAccountButton.setVisibility(View.VISIBLE);
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

//    @Override
//    protected void onStart() {
//        mRLbg.setBackground(ContextCompat.getDrawable(App.getContext(), randomBg));
//        super.onStart();
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
