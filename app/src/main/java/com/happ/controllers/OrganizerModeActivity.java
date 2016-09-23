package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.fragments.EventInterestFragment;
import com.happ.models.Event;
import com.happ.models.Interest;
import com.happ.retrofit.APIService;
import com.happ.retrofit.HappRestClient;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by dante on 9/14/16.
 */
public class OrganizerModeActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;

    protected ArrayList<Event> events;
    protected RecyclerView eventsListView;
    protected LinearLayoutManager eventsListLayoutManager;
    private BroadcastReceiver eventsRequestDoneReceiver;

    private int eventsFeedPageSize;
    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold;
    private FloatingActionButton mOrganizerFab;
    private Interest selectedInterest;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_mode);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.organizer_title);


//        APIService.doEventDelete(event.getId());


        mOrganizerFab = (FloatingActionButton) findViewById(R.id.organizer_fab);
        mOrganizerFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               Intent i = new Intent(getApplicationContext(), EditActivity.class);
                startActivity(i);
            }
        });


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    App.doLogout(OrganizerModeActivity.this);
                }
                if (menuItem.getItemId() == R.id.nav_item_feed) {
                    Intent goToFeedIntent = new Intent(OrganizerModeActivity.this, FeedActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);
                }
                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    Intent goToFeedIntent = new Intent(OrganizerModeActivity.this, UserActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);

                }
                if (menuItem.getItemId() == R.id.nav_item_interests) {
//                    Intent i = new Intent(getApplicationContext(), SelectInterestsActivity.class);
//                    startActivity(i);

                    FragmentManager fm = getSupportFragmentManager();
                    EventInterestFragment editNameDialogFragment = EventInterestFragment.newInstance();
                    editNameDialogFragment.setOnInterestSelectListener(new EventInterestFragment.OnInterestSelectListener() {
                        @Override
                        public void onInterestSelected(Interest interest) {
                            selectedInterest = interest;
                            RealmList<Interest> interests = new RealmList<Interest>();
                            interests.add(interest);
                            event.setInterests(interests);
//                            mEditInterests.setText(selectedInterest.getTitle());
                        }
                    });
                    editNameDialogFragment.show(fm, "fragment_select_interest");
                }



                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.nav_item_organizer).setChecked(true);

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar)).setImageDrawable(getResources().getDrawable(R.drawable.avatar));

        eventsFeedPageSize = Integer.parseInt(this.getString(R.string.event_feeds_page_size));
        visibleThreshold = Integer.parseInt(this.getString(R.string.event_feeds_visible_treshold_for_loading_next_items));


        eventsListView = (RecyclerView) findViewById(R.id.events_organizer_list_view);
        eventsListLayoutManager = new LinearLayoutManager(this);
        eventsListView.setLayoutManager(eventsListLayoutManager);
        events = new ArrayList<>();

        EventsListAdapter ela = new EventsListAdapter(this, events);
        ela.setOnSelectItemListener(new EventsListAdapter.SelectEventItemListener() {
            @Override
            public void onEventItemSelected(String eventId, ActivityOptionsCompat options) {
                Intent intent = new Intent(OrganizerModeActivity.this, EventActivity.class);
                intent.putExtra("event_id", eventId);
                intent.putExtra("is_organizer", true);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || options == null) {
                    OrganizerModeActivity.this.startActivity(intent);
                    (OrganizerModeActivity.this).overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
                } else {
                    OrganizerModeActivity.this.startActivity(intent, options.toBundle());
                }
            }

            @Override
            public void onEventEditSelected(String eventId) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("event_id", eventId);
                startActivity(intent);
            }
        });
        ela.setIsOrganizer(true);
        eventsListView.setAdapter(ela);

        eventsRequestDoneReceiver = createEventsRequestDoneReceiver();
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(eventsRequestDoneReceiver, new IntentFilter(BroadcastIntents.EVENTS_REQUEST_OK));

        HappRestClient.getInstance().getEvents(false);

        createScrollListener();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateEventsList();
    }


    protected void updateEventsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventRealmResults = realm.where(Event.class).equalTo("author.id", App.getCurrentUser().getId()).equalTo("localOnly", false).findAllSorted("startDate", Sort.ASCENDING);
        events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults);
        ((EventsListAdapter)eventsListView.getAdapter()).updateData(events);
        realm.close();
    }


    private BroadcastReceiver createEventsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateEventsList();

            }
        };
    }


    protected void createScrollListener() {
        eventsListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = eventsListLayoutManager.getChildCount();
                    totalItemCount = eventsListLayoutManager.getItemCount();
                    firstVisibleItem = eventsListLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }

                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {
                        loading = true;
                        int nextPage = (totalItemCount / eventsFeedPageSize) + 1;
                        getEvents(nextPage);
                    }
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    protected void getEvents(int page) {
        APIService.getEvents(page, false);
    }

    @Override
    public void onDestroy() {
        if (eventsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(eventsRequestDoneReceiver);
        }
        super.onDestroy();
    }


}
