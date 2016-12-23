package com.happ.controllers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.happ.App;
import com.happ.R;
import com.happ.controllers_drawer.OrganizerModeActivity;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by dante on 10/1/16.
 */
public class HtmlPageAcitivty extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Toolbar toolbar;

    private boolean fromSettings;
    private boolean linkTermsOfService;
    private boolean linkPrivacyPolicy;
    private boolean fromOrgCreateEvent;
    private boolean linkOrganizerRules;
    private boolean linkFAQ;


    private LinearLayout mLLbottom;
    private Button mBtnAgree, mBtnDisagree;

    private MaterialProgressBar materialProgressBar;
    private View viewDarkHtmlPage;
    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromSettings = getIntent().getBooleanExtra("from_settings", false);
        linkTermsOfService = getIntent().getBooleanExtra("link_terms_of_service", false);
        linkPrivacyPolicy = getIntent().getBooleanExtra("link_privacy_policy", false);
        linkOrganizerRules = getIntent().getBooleanExtra("link_organizer_rules", false);
        fromOrgCreateEvent = getIntent().getBooleanExtra("from_organizermode", false);
        linkFAQ = getIntent().getBooleanExtra("link_faq", false);
        setContentView(R.layout.activity_html_page);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mLLbottom = (LinearLayout) findViewById(R.id.ll_bottom);
        mBtnDisagree = (Button) findViewById(R.id.btn_org_rules_disagree);
        mBtnAgree = (Button) findViewById(R.id.btn_org_rules_agree);
        viewDarkHtmlPage = (View) findViewById(R.id.dark_view_html_page_progress);
        materialProgressBar = (MaterialProgressBar) findViewById(R.id.html_page_progress);
        webView = (WebView) findViewById(R.id.webview_html_page);


        setSupportActionBar(toolbar);

        if (fromOrgCreateEvent) {
            mLLbottom.setVisibility(View.VISIBLE);
            toolbar.setNavigationIcon(R.drawable.ic_right_arrow_grey);
        } else {
            mLLbottom.setVisibility(View.GONE);
            toolbar.setNavigationIcon(R.drawable.ic_close_grey);
        }

        if (fromSettings) {
            toolbar.setNavigationIcon(R.drawable.ic_right_arrow_grey);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_close_grey);
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if (fromSettings) {
                    HtmlPageAcitivty.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
                }
            }
        });

        webView.setWebViewClient(new CallBackWebView());
        String host = App.getContext().getResources().getString(R.string.HOST);
        String api_url = App.getContext().getResources().getString(R.string.API_URL);

        if (linkPrivacyPolicy) {
            setTitle(R.string.privacy_policy);
            webView.loadUrl(host + api_url + "privacy-policy/");
        } else if (linkTermsOfService) {
            setTitle(R.string.terms_of_service);
            webView.loadUrl(host + api_url + "terms-of-service/");
        } else if (linkOrganizerRules) {
            setTitle(R.string.organizer_rules);
            webView.loadUrl(host + api_url + "organizer-rules/");
        } else if (linkFAQ) {
            setTitle(R.string.faq_help);
            webView.loadUrl(host + api_url + "faq/");
        }


        mBtnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HtmlPageAcitivty.this, EditCreateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        mBtnDisagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HtmlPageAcitivty.this, OrganizerModeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

    }

    private class CallBackWebView extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading
                (WebView view, String url) {
            return(false);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            viewDarkHtmlPage.setVisibility(View.VISIBLE);
            materialProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            viewDarkHtmlPage.setVisibility(View.GONE);
            materialProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            Log.d("AAAAAAAAAAA","ON PAGE error");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fromOrgCreateEvent) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        if (fromSettings) {
            HtmlPageAcitivty.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
        }
    }

}
