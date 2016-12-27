package com.happ.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happ.LockableScrollView;
import com.happ.R;
import com.happ.retrofit.HappRestClient;

import java.util.Random;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by dante on 11/15/16.
 */
public class PasswordRecoveActivity extends AppCompatActivity {


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Toolbar toolbar;
    private EditText mEmail;
    private Button mBtnRecovePassword;
    private ImageView mImgLogo;
    private RelativeLayout mRLFooter;
    private TextView mTVPrivacyPolicy, mTVTermsPolicy;
    private AppCompatImageView mIVbg;
    private TextView mTVEnterEmail;
    private MaterialProgressBar mProgressBar;

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

    private boolean isKeyboarShown = false;
    private ViewTreeObserver.OnGlobalLayoutListener mKeyboardListener;
    private RelativeLayout mRLDoneForm;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recove_password);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mEmail = (EditText) findViewById(R.id.input_recove_mail);
        mBtnRecovePassword = (Button) findViewById(R.id.btn_recover_password);
        mImgLogo = (ImageView) findViewById(R.id.img_logo);
        mRLFooter = (RelativeLayout) findViewById(R.id.rl_footer);
        mTVPrivacyPolicy = (TextView) findViewById(R.id.tv_privacy_policy);
        mTVTermsPolicy = (TextView) findViewById(R.id.tv_terms_and_policy);
        mIVbg = (AppCompatImageView) findViewById(R.id.iv_recove_pw_bg);
        mTVEnterEmail = (TextView) findViewById(R.id.tv_enter_email);
        mProgressBar = (MaterialProgressBar) findViewById(R.id.circular_progress_recove_password);
        mRLDoneForm = (RelativeLayout) findViewById(R.id.rl_form_response_done);
        mRLDoneForm.setVisibility(View.GONE);

        LockableScrollView sv = (LockableScrollView)findViewById(R.id.fake_scrollview);
        sv.setScrollingEnabled(false);

        mBtnRecovePassword.setVisibility(View.INVISIBLE);

        mIVbg.setImageResource(randomBg);

        setSupportActionBar(toolbar);
        setTitle("");

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_rigth_arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

        mEmail.addTextChangedListener(mWatcher);
        mBtnRecovePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(PasswordRecoveActivity.this, view);
                mBtnRecovePassword.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                HappRestClient.getInstance().setPasswordReset(mEmail.getText().toString());
                mRLDoneForm.setVisibility(View.VISIBLE);
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
     }

    TextWatcher mWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            if (TextUtils.isEmpty(mEmail.getText()))
                mBtnRecovePassword.setVisibility(View.GONE);
            else
                mBtnRecovePassword.setVisibility(View.VISIBLE);

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


    private void setSpannableString() {
        SpannableString spanPrivacyPolicyString = new SpannableString(mTVPrivacyPolicy.getText());
        spanPrivacyPolicyString.setSpan(new UnderlineSpan(), 0, spanPrivacyPolicyString.length(), 0);
        mTVPrivacyPolicy.setText(spanPrivacyPolicyString);

        SpannableString spanTermsPolicyString = new SpannableString(mTVTermsPolicy.getText());
        spanTermsPolicyString.setSpan(new UnderlineSpan(), 0, spanTermsPolicyString.length(), 0);
        mTVTermsPolicy.setText(spanTermsPolicyString);
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
                            mTVEnterEmail.setVisibility(View.GONE);
                            mRLFooter.setVisibility(View.GONE);
                        }
                        isKeyboarShown = true;
                    }
                    else {
                        mImgLogo.setVisibility(View.VISIBLE);
                        mTVEnterEmail.setVisibility(View.VISIBLE);
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

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}
