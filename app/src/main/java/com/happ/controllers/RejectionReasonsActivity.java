package com.happ.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.happ.R;
import com.happ.models.Event;

import io.realm.Realm;

/**
 * Created by dante on 12/23/16.
 */
public class RejectionReasonsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mBtnEditEvent;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager llm;

    private String eventId;
    private Event event;
    private TextView mTextViewRejectionReason;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        eventId = intent.getStringExtra("event_id");

        Realm realm = Realm.getDefaultInstance();
        event = realm.where(Event.class).equalTo("id", eventId).findFirst();
        event = realm.copyFromRealm(event);
        realm.close();
        setContentView(R.layout.activity_rejection_reasons);
        setTitle(R.string.rra_event_details);
        binds();

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_right_arrow_grey);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

        mTextViewRejectionReason.setText(event.getRejectionReason().getText());

//        llm = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(llm);
//        RejectionReasonsListAdapter rejectionReasonsListAdapter = new RejectionReasonsListAdapter(this, event.getRejectionReason());
//        mRecyclerView.setAdapter(rejectionReasonsListAdapter);

        mBtnEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RejectionReasonsActivity.this, EditCreateActivity.class);
                intent.putExtra("event_id", eventId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void binds() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBtnEditEvent = (Button) findViewById(R.id.btn_edit_event);
        mRecyclerView = (RecyclerView) findViewById(R.id.rra_recycler_view);
        mTextViewRejectionReason = (TextView) findViewById(R.id.rra_text);
    }
}
