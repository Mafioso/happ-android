package com.happ.controllers;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.happ.R;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by dante on 11/29/16.
 */
public class BrowserActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialProgressBar materialProgressBar;
    private View viewDarkBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewDarkBrowser = (View) findViewById(R.id.dark_view_event_browser_progress);
        materialProgressBar = (MaterialProgressBar) findViewById(R.id.event_browser_progress);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_grey);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                BrowserActivity.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
            }
        });
        Uri url = getIntent().getData();
        WebView webView = (WebView) findViewById(R.id.event_link_webview);
        webView.setWebViewClient(new Callback());
        webView.loadUrl(url.toString());
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading
                (WebView view, String url) {
            return(false);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            viewDarkBrowser.setVisibility(View.VISIBLE);
            materialProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("AAAAAAAAAA","ON PAGE FINISHED");
            viewDarkBrowser.setVisibility(View.GONE);
            materialProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            Log.d("AAAAAAAAAAA","ON PAGE error");
        }
    }


}
