package com.happ.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.happ.R;
import com.happ.controllers_drawer.OrganizerModeActivity;

/**
 * Created by dante on 10/1/16.
 */
public class OrganizerRulesActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private Toolbar toolbar;
    private boolean fromSettings = false;
    private boolean fromOrgFabCreateEvent = false;

    private NestedScrollView mScrollView;
    private TextView mTextOrgRules;
    private LinearLayout mLLorgRulesAgreeDesagree;
    private Button mBtnAgree, mBtnDisagree;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromSettings = getIntent().getBooleanExtra("from_settings", false);
        fromOrgFabCreateEvent = getIntent().getBooleanExtra("from_org_fab_create", false);

        setContentView(R.layout.activity_organizer_rules);

        mLLorgRulesAgreeDesagree = (LinearLayout) findViewById(R.id.ll_or_agree_desagree);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!fromOrgFabCreateEvent) {
            mLLorgRulesAgreeDesagree.setVisibility(View.GONE);
            toolbar.setNavigationIcon(R.drawable.ic_close_white);
        } else {
            if (fromSettings) {
                toolbar.setNavigationIcon(R.drawable.ic_rigth_arrow);
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_close_white);
            }
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (fromSettings) {
                    OrganizerRulesActivity.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
                }
            }
        });

        mScrollView = (NestedScrollView) findViewById(R.id.org_rules_srollview);
        mTextOrgRules = (TextView) findViewById(R.id.text_organizer_rules);

        mBtnAgree = (Button) findViewById(R.id.btn_org_rules_agree);
        mBtnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerRulesActivity.this, EditActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        mBtnDisagree = (Button) findViewById(R.id.btn_org_rules_disagree);
        mBtnDisagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerRulesActivity.this, OrganizerModeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        if (fromSettings) {
            OrganizerRulesActivity.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

