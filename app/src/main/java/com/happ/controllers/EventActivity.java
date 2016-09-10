package com.happ.controllers;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happ.App;
import com.happ.R;
import com.happ.models.Event;
import com.happ.models.Interest;
import com.happ.models.User;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;
//import android.support.design.widget.CollapsingToolbarLayout;


import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by dante on 8/8/16.
 */
public class EventActivity extends AppCompatActivity {


    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private String eventId;
    private Event event;

    ViewPager viewPager;
    EventImagesSwipeAdapter mEventImagesSwipeAdapter;

    private TextView mPlace;
    private TextView mAuthor;
    private TextView mDescription;
    private TextView mStartDate;
    private TextView mEndDate;
    private TextView mEventTitle;
    private TextView mEventInterestTitle;
    private LinearLayout mEventInterestBg;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            EventActivity.this.supportFinishAfterTransition();
        } else {
            finish();
            EventActivity.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        eventId = intent.getStringExtra("event_id");
        Realm realm = Realm.getDefaultInstance();
        event = realm.where(Event.class).equalTo("id", eventId).findFirst();
        event = realm.copyFromRealm(event);
        realm.close();

//        setTitle(event.getTitle());
        setContentView(R.layout.activity_event);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }

//        mEventTitle = (TextView) findViewById(R.id.header_text);
//        mEventTitle.setText(event.getTitle());
//        mEventTitle.setVisibility(View.INVISIBLE);

        mEventInterestBg = (LinearLayout) findViewById(R.id.event_interest_bg);
        Interest interest = event.getInterest();
        String color = interest.getColor();
        if (color != null) {
            mEventInterestBg.setBackgroundColor(Color.parseColor("#"+color));
        }

        mEventInterestTitle = (TextView) findViewById(R.id.event_interest_title);
        ArrayList<String> fullTitle = event.getInterest().getFullTitle();

        String fullTitleString = fullTitle.get(0);
        if (fullTitle.size() > 1) {
            fullTitleString = fullTitleString + " / " + fullTitle.get(1);
        }
        mEventInterestTitle.setText(fullTitleString.toUpperCase());

        mPlace = (TextView)findViewById(R.id.event_place);
        mPlace.setText(event.getPlace());
//
        mAuthor = (TextView)findViewById(R.id.event_author);
        User author = event.getAuthor();
        String fullName = author.getFullName();
        mAuthor.setText(event.getAuthor().getFullName());
//
        mDescription = (TextView)findViewById(R.id.event_description);
        mDescription.setText(event.getDescription());
//
        mStartDate = (TextView)findViewById(R.id.event_start_date);
        mStartDate.setText(event.getStartDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));
//
        mEndDate = (TextView)findViewById(R.id.event_end_date);
        mEndDate.setText(event.getEndDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));



        viewPager=(ViewPager)findViewById(R.id.slider_viewpager);
        mEventImagesSwipeAdapter = new EventImagesSwipeAdapter(getSupportFragmentManager());
        mEventImagesSwipeAdapter.setImageList(event.getImages());
        viewPager.setAdapter(mEventImagesSwipeAdapter);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout ctl = (CollapsingToolbarLayout)findViewById(R.id.event_collapsing_layout);
        ctl.setTitle(event.getTitle());

//        toolbar.setNavigationIcon(R.drawable.ic_rigth_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    EventActivity.this.supportFinishAfterTransition();
                } else {
                    finish();
                    EventActivity.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
                }
            }
        });



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    App.doLogout(EventActivity.this);
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
