package com.happ.controllers;

import android.content.Intent;
import android.os.Bundle;
import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.happ.R;
import com.happ.models.Event;

import io.realm.Realm;

/**
 * Created by dante on 8/8/16.
 */
public class EventActivity extends AppCompatActivity {


    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private int eventId;
    private Event event;

    ViewPager viewPager;
    EventImagesSwipeAdapter mEventImagesSwipeAdapter;

    private TextView mPlace;
    private TextView mAuthor;
    private TextView mDescription;
    private TextView mStartDate;
    private TextView mEndDate;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        eventId = intent.getIntExtra("event_id", 1);
        Realm realm = Realm.getDefaultInstance();
        event = realm.where(Event.class).equalTo("id", eventId).findFirst();
        event = realm.copyFromRealm(event);
        realm.close();

//        setTitle(event.getTitle());
        setContentView(R.layout.activity_event);

//         for (int i = 0; i < event.getImages().size(); i++) {
//            event.getImages().get(i).getUrl();
//         }
//
//        mPlace = (TextView)findViewById(R.id.event_place);
//        mPlace.setText(event.getPlace());
//
//        mAuthor = (TextView)findViewById(R.id.event_author);
//        mAuthor.setText(event.getAuthor().getFullName());
//
//        mDescription = (TextView)findViewById(R.id.event_description);
//        mDescription.setText(event.getDescription());
//
//        mStartDate = (TextView)findViewById(R.id.event_start_date);
//        mStartDate.setText(event.getStartDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));
//
//        mEndDate = (TextView)findViewById(R.id.event_end_date);
//        mEndDate.setText(event.getEndDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));

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
                finish();
            }
        });



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                Toast.makeText(EventActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
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
