package com.happ.controllers;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.happ.App;
import com.happ.R;
import com.happ.models.Event;
import com.happ.models.EventImage;
import com.happ.models.Interest;
import com.happ.models.User;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;

//import android.support.design.widget.CollapsingToolbarLayout;

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


    private TextView mWebSite, mEmail, mPlace, mAuthor, mDescription,mStartDate,mEndDate,mEventInterestTitle ;
    private LinearLayout mEventInterestBg, mEventAuthor, mEventWEbSite, mEventEmail;

    private FloatingActionButton mFab;

    private boolean isOrg;
    CollapsingToolbarLayout ctl;
    private NavigationView navigationView;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDrawerLayout = null;
        toolbar = null;
        eventId = null;
        event = null;
        viewPager = null;
        mEventImagesSwipeAdapter = null;
        mWebSite = null;
        mEmail = null;
        mPlace = null;
        mAuthor = null;
        mDescription = null;
        mStartDate = null;
        mEndDate = null;
        mEventInterestTitle = null;
        mEventInterestBg = null;
        mEventAuthor = null;
        mEventWEbSite = null;
        mEventEmail = null;
        mFab = null;
        ctl = null;
    }

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

    private void repopulateEvent() {
        Realm realm = Realm.getDefaultInstance();
        event = realm.where(Event.class).equalTo("id", eventId).findFirst();
        event = realm.copyFromRealm(event);
        realm.close();

        mEventImagesSwipeAdapter.setImageList(event.getImages());

        Interest interest = event.getInterest();


        if (interest != null) {
            if (interest.getColor() != null) {
                String color = interest.getColor();
                if (color.length() > 6) {
                    color = color.substring(color.length()-6);
                }
                mEventInterestBg.setBackgroundColor(Color.parseColor("#" + color));
            } else {
                mEventInterestBg.setBackgroundColor(Color.parseColor("#FF1493"));
            }

            ArrayList<String> fullTitle = event.getInterest().getFullTitle();
            String fullTitleString = fullTitle.get(0);
            if (fullTitle.size() > 1) {
                fullTitleString = fullTitleString + " / " + fullTitle.get(1);
            }
            mEventInterestTitle.setText(fullTitleString.toUpperCase());
        } else {
            mEventInterestTitle.setText("Null");
        }
        mPlace.setText(event.getPlace());
        User author = event.getAuthor();
        if (author != null) {
            String fullName = author.getFullName();
            mAuthor.setText(event.getAuthor().getFullName());
        } else {
            mEventAuthor.setVisibility(View.GONE);
        }

        mDescription.setText(event.getDescription());
        if (event.getWebSite() != null ) {
            mWebSite.setText(event.getWebSite());
        } else {
            mEventWEbSite.setVisibility(View.GONE);
        }

        mEmail = (TextView) findViewById(R.id.event_email);
        if (event.getEmail() != null ) {
            mEmail.setText(event.getEmail());
        } else {
            mEventEmail.setVisibility(View.GONE);
        }
        mStartDate.setText(event.getStartDateFormatted("dd MMMM, yyyy 'a''t' h:mm"));
        mEndDate.setText(event.getEndDateFormatted("dd MMMM, yyyy 'a''t' h:mm"));

        ctl.setTitle(event.getTitle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        isOrg = intent.getBooleanExtra("is_organizer", false);
        eventId = intent.getStringExtra("event_id");

        setContentView(R.layout.activity_event);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setVisibility(View.GONE);
        if (isOrg) {
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editIntent = new Intent(EventActivity.this, EditActivity.class);
                    editIntent.putExtra("event_id", eventId);
                    EventActivity.this.startActivity(editIntent);
                }
            });
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }


        mEventInterestTitle = (TextView) findViewById(R.id.event_interest_title);
        mEventInterestBg = (LinearLayout) findViewById(R.id.event_interest_bg);
        mEventAuthor = (LinearLayout) findViewById(R.id.event_author_form);
        mPlace = (TextView)findViewById(R.id.event_place);
        mAuthor = (TextView)findViewById(R.id.event_author);
        mDescription = (TextView)findViewById(R.id.event_description);
        mWebSite = (TextView) findViewById(R.id.event_website);
        mEventWEbSite = (LinearLayout) findViewById(R.id.event_website_form);
        mEventEmail = (LinearLayout) findViewById(R.id.event_email_form);
        mStartDate = (TextView)findViewById(R.id.event_start_date);
        mEndDate = (TextView)findViewById(R.id.event_end_date);

        viewPager=(ViewPager)findViewById(R.id.slider_viewpager);
        mEventImagesSwipeAdapter = new EventImagesSwipeAdapter(getSupportFragmentManager());
        mEventImagesSwipeAdapter.setImageList(new RealmList<EventImage>());
        viewPager.setAdapter(mEventImagesSwipeAdapter);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ctl = (CollapsingToolbarLayout)findViewById(R.id.event_collapsing_layout);

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

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    App.doLogout(EventActivity.this);
                }


                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    Intent goToFeedIntent = new Intent(EventActivity.this, SettingsActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);

                }
                if (menuItem.getItemId() == R.id.nav_item_organizer) {
                    Intent goToFeedIntent = new Intent(EventActivity.this, OrganizerModeActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);
                }

                if (menuItem.getItemId() == R.id.nav_item_interests) {
                    Intent intent = new Intent(EventActivity.this, SelectInterestsActivity.class);
                    intent.putExtra("is_full", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }

                if (menuItem.getItemId() == R.id.nav_item_feed) {
                    Intent intent = new Intent(EventActivity.this, FeedActivity.class);
                    intent.putExtra("is_full", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }

                if (menuItem.getItemId() == R.id.nav_item_privacy_policy) {
                    Intent intent = new Intent(EventActivity.this, OrganizerRulesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        navigationView.getMenu().findItem(R.id.nav_item_feed).setChecked(true);

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar)).setImageDrawable(getResources().getDrawable(R.drawable.avatar));

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.drawer_username)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
        ((CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

        repopulateEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        repopulateEvent();
        if (isOrg && mFab.getVisibility() != View.VISIBLE) {
            mFab.show();
        }
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar)).setImageDrawable(getResources().getDrawable(R.drawable.avatar));
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
