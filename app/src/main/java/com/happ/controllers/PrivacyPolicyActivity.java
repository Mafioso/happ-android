package com.happ.controllers;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.happ.R;

/**
 * Created by dante on 10/1/16.
 */
public class PrivacyPolicyActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private Toolbar toolbar;
    private boolean fromSettings = false;
    private NestedScrollView mScrollView;
    private TextView mTextPrivacyPolicy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromSettings = getIntent().getBooleanExtra("from_settings", false);

        setContentView(R.layout.activity_privacy_policy);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (fromSettings) {
            toolbar.setNavigationIcon(R.drawable.ic_rigth_arrow);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_close_white);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (fromSettings) {
                    PrivacyPolicyActivity.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
                }
            }
        });

        mScrollView = (NestedScrollView) findViewById(R.id.event_edit_srollview);
        mTextPrivacyPolicy = (TextView) findViewById(R.id.text_privacy_policy);

     }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        if (fromSettings) {
            PrivacyPolicyActivity.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
