package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.controllers_drawer.OrganizerModeActivity;
import com.happ.retrofit.APIService;
import com.happ.retrofit.HappRestClient;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by dante on 12/27/16.
 */
public class NewPasswordActivity extends AppCompatActivity {

    private Button mBtnChangePassword;
    private EditText mEditTextNewPW, mEditTextRepeatNewPW;
    private MaterialProgressBar mpb;
    private BroadcastReceiver passwordResetNewPasswordOkRequest, confirmEmailOkRequest;
    private boolean isKeyboarShown = false;
    private ViewTreeObserver.OnGlobalLayoutListener mKeyboardListener;
    private RelativeLayout mFooterView;
    private ImageView mImageLogo;
    private TextView mTVPrivacyPolicy, mTVTermsPolicy;
    private TextView mTVInformation;
    private String uidb64 = "";
    private String token = "";
    private String key = "";

    private RelativeLayout mRLNewPassword, mRLReapeatNewPassword;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpassword);

        mBtnChangePassword = (Button) findViewById(R.id.btn_change_password);
        mpb = (MaterialProgressBar) findViewById(R.id.circular_progress_newpw);
        mEditTextNewPW = (EditText) findViewById(R.id.input_newpw);
        mEditTextRepeatNewPW = (EditText) findViewById(R.id.input_repeat_newpw);
        mImageLogo = (ImageView) findViewById(R.id.img_logo);
        mFooterView = (RelativeLayout) findViewById(R.id.rl_footer);
        mTVPrivacyPolicy = (TextView) findViewById(R.id.tv_privacy_policy);
        mTVTermsPolicy = (TextView) findViewById(R.id.tv_terms_and_policy);
        mBtnChangePassword.setVisibility(View.GONE);
        mTVInformation = (TextView) findViewById(R.id.tv_enter_newpw);
        mRLNewPassword = (RelativeLayout) findViewById(R.id.rl_newpw_form);
        mRLReapeatNewPassword = (RelativeLayout) findViewById(R.id.rl_repeat_newpw_form);

        Uri uriData = getIntent().getData();
        if (uriData.getQueryParameter("uidb64") != null && uriData.getQueryParameter("token") != null) {
            uidb64 = getIntent().getData().getQueryParameter("uidb64");
            token = getIntent().getData().getQueryParameter("token");

            Log.e("Log >>>>", "uidb64: " + uidb64);
            Log.e("Log >>>>", "token: " + token);

            mBtnChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mpb.setVisibility(View.VISIBLE);
                    if (mEditTextNewPW.getText().toString().equals(mEditTextRepeatNewPW.getText().toString())) {
                        HappRestClient.getInstance().setNewPassword(uidb64, token, mEditTextNewPW.getText().toString());
                    } else {
                        Toast.makeText(NewPasswordActivity.this, getResources().getString(R.string.password_dont_match), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else if (uriData.getQueryParameter("key") != null) {
            key = getIntent().getData().getQueryParameter("key");
            Log.e("Log >>>>", "key: " + key);
            mRLNewPassword.setVisibility(View.GONE);
            mRLReapeatNewPassword.setVisibility(View.GONE);
            mTVInformation.setVisibility(View.GONE);
            mBtnChangePassword.setVisibility(View.VISIBLE);
            mBtnChangePassword.setText(getResources().getString(R.string.go_to_organizer));

            mBtnChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBtnChangePassword.setVisibility(View.GONE);
                    HappRestClient.getInstance().setConfirmEmail(key);
                }
            });

        }

        setSpannableString();
        setListenerToRootView();
        mEditTextNewPW.addTextChangedListener(mWatcher);
        mEditTextRepeatNewPW.addTextChangedListener(mWatcher);

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


        if (passwordResetNewPasswordOkRequest == null) {
            passwordResetNewPasswordOkRequest = changeNewPasswordOkReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(passwordResetNewPasswordOkRequest, new IntentFilter(BroadcastIntents.PASSWORD_RESET_NEWPW_OK));
        }

        if (confirmEmailOkRequest == null) {
            confirmEmailOkRequest = confirmEmailKeyOkReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(confirmEmailOkRequest, new IntentFilter(BroadcastIntents.SET_CONFIRM_EMAIL_KEY_OK));
        }

    }

    private void setSpannableString() {
        SpannableString spanPrivacyPolicyString = new SpannableString(mTVPrivacyPolicy.getText());
        spanPrivacyPolicyString.setSpan(new UnderlineSpan(), 0, spanPrivacyPolicyString.length(), 0);
        mTVPrivacyPolicy.setText(spanPrivacyPolicyString);

        SpannableString spanTermsPolicyString = new SpannableString(mTVTermsPolicy.getText());
        spanTermsPolicyString.setSpan(new UnderlineSpan(), 0, spanTermsPolicyString.length(), 0);
        mTVTermsPolicy.setText(spanTermsPolicyString);

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

    TextWatcher mWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (mEditTextNewPW.getText().toString().equals("") || mEditTextRepeatNewPW.getText().toString().equals("")) {

                mBtnChangePassword.setVisibility(View.GONE);
            } else {
                mBtnChangePassword.setVisibility(View.VISIBLE);
            }
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

    private BroadcastReceiver changeNewPasswordOkReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mpb.setVisibility(View.GONE);
                mBtnChangePassword.setVisibility(View.VISIBLE);

                Intent goToLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                goToLoginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToLoginActivity);
                overridePendingTransition(0, 0);
            }
        };
    }

    private BroadcastReceiver confirmEmailKeyOkReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mpb.setVisibility(View.GONE);
                mBtnChangePassword.setVisibility(View.VISIBLE);
                APIService.getCurrentUser();

                Intent goToOrganizerMode = new Intent(getApplicationContext(), OrganizerModeActivity.class);
                goToOrganizerMode.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToOrganizerMode);
                overridePendingTransition(0, 0);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (passwordResetNewPasswordOkRequest != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(passwordResetNewPasswordOkRequest);
            passwordResetNewPasswordOkRequest = null;
        }

        if(confirmEmailOkRequest != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(confirmEmailOkRequest);
            confirmEmailOkRequest = null;
        }

    }
}
