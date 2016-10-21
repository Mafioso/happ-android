package com.happ.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.OrgEventsListAdapter;
import com.happ.controllers.EditActivity;
import com.happ.controllers.OrganizerRulesActivity;
import com.happ.controllers.UserActivity;
import com.happ.controllers_drawer.EventActivity;
import com.happ.controllers_drawer.FeedActivity;
import com.happ.controllers_drawer.SelectInterestsActivity;
import com.happ.controllers_drawer.SettingsActivity;
import com.happ.models.Event;
import com.happ.retrofit.APIService;
import com.happ.retrofit.HappRestClient;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by dante on 9/14/16.
 */
public class EventsOrganizerFragment extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    protected ArrayList<Event> orgEvents;
    protected RecyclerView orgEventsRecyclerView;
    protected LinearLayoutManager eventsListLayoutManager;
    private FloatingActionButton mOrganizerFab;
    private NavigationView navigationMenu, navigationHeader;
    private ViewPager mDrawerCityFragment;
    private PagerAdapter cityPageAdapter;

    private SharedPreferences sPref;
    final String FIRST_CREATE_EVENT = "first_create_event";

    private int eventsFeedPageSize;
    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold;

    private BroadcastReceiver eventsRequestDoneReceiver;
    private BroadcastReceiver deleteEventRequestDoneReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_mode);


        mDrawerCityFragment = (ViewPager) findViewById(R.id.drawer_viewpager);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationMenu = (NavigationView) findViewById(R.id.navigation_menu);
        navigationHeader = (NavigationView) findViewById(R.id.navigation_header);
        mOrganizerFab = (FloatingActionButton) findViewById(R.id.organizer_fab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        orgEventsRecyclerView = (RecyclerView) findViewById(R.id.events_organizer_list_view);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.organizer_title);

        mOrganizerFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                sPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
                String name = sPref.getString("first_create_event", "");

                if (name == null || name.length() == 0) {
                    sPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
                    sPref.edit().putString("first_create_event", FIRST_CREATE_EVENT).apply();

                    Intent i = new Intent(EventsOrganizerFragment.this, OrganizerRulesActivity.class);
                    i.putExtra("from_org_fab_create", true);
                    startActivity(i);

//                    Intent intent = new Intent(EventsOrganizerFragment.this, SettingsActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    startActivity(intent);
//                    overridePendingTransition(0,0);

                } else {
                    Intent i = new Intent(getApplicationContext(), EditActivity.class);
                    startActivity(i);
                }
            }
        });


        navigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    App.doLogout(EventsOrganizerFragment.this);
                }

                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    Intent goToFeedIntent = new Intent(EventsOrganizerFragment.this, SettingsActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);
                }

                if (menuItem.getItemId() == R.id.nav_item_organizer) {
                    Intent goToFeedIntent = new Intent(EventsOrganizerFragment.this, EventsOrganizerFragment.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);
                }

                if (menuItem.getItemId() == R.id.nav_item_interests) {
                    Intent intent = new Intent(EventsOrganizerFragment.this, SelectInterestsActivity.class);
                    intent.putExtra("is_full", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }

                if (menuItem.getItemId() == R.id.nav_item_feed) {
                    Intent intent = new Intent(EventsOrganizerFragment.this, FeedActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }


                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        navigationMenu.getMenu().findItem(R.id.nav_item_organizer).setChecked(true);
        navigationMenu.getMenu().findItem(R.id.nav_item_organizer).setIcon(R.drawable.happ_drawer_icon);

        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city)).setText(App.getCurrentCity().getName());

        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventsOrganizerFragment.this, UserActivity.class);
                startActivity(intent);
            }
        });

        cityPageAdapter = new MyCityPageAdapter(getSupportFragmentManager());
        mDrawerCityFragment.setAdapter(cityPageAdapter);


        ((CheckBox)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_header_arrow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_header_arrow)).isChecked()) {
                    mDrawerCityFragment.setVisibility(View.VISIBLE);
                } else {
                    mDrawerCityFragment.setVisibility(View.GONE);
                }
            }
        });

        eventsFeedPageSize = Integer.parseInt(this.getString(R.string.event_feeds_page_size));
        visibleThreshold = Integer.parseInt(this.getString(R.string.event_feeds_visible_treshold_for_loading_next_items));

        eventsListLayoutManager = new LinearLayoutManager(this);
        orgEventsRecyclerView.setLayoutManager(eventsListLayoutManager);
        orgEvents = new ArrayList<>();

        OrgEventsListAdapter oela = new OrgEventsListAdapter(this, orgEvents);
        oela.setOnSelectItemListener(new OrgEventsListAdapter.SelectEventItemListener() {
            @Override
            public void onEventItemSelected(String eventId, ActivityOptionsCompat options) {
                Intent intent = new Intent(EventsOrganizerFragment.this, EventActivity.class);
                intent.putExtra("event_id", eventId);
                intent.putExtra("is_organizer", true);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || options == null) {
                    EventsOrganizerFragment.this.startActivity(intent);
                    (EventsOrganizerFragment.this).overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
                } else {
                    EventsOrganizerFragment.this.startActivity(intent, options.toBundle());
                }
            }

            @Override
            public void onEventEditSelected(String eventId) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("event_id", eventId);
                startActivity(intent);
            }
        });
        orgEventsRecyclerView.setAdapter(oela);

        if (eventsRequestDoneReceiver == null) {
            eventsRequestDoneReceiver = createEventsRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(eventsRequestDoneReceiver, new IntentFilter(BroadcastIntents.EVENTS_REQUEST_OK));
        }

        if (deleteEventRequestDoneReceiver == null) {
            deleteEventRequestDoneReceiver = deleteEventRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(deleteEventRequestDoneReceiver, new IntentFilter(BroadcastIntents.EVENTDELETE_REQUEST_OK));
        }

        HappRestClient.getInstance().getEvents(false);
        createScrollListener();

    }


    public class MyCityPageAdapter extends FragmentPagerAdapter {

        public MyCityPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SelectCityFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 1;
        }
    }


    public void functionToRun(final String eventId) {
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.delete_confirm_title))
                .content(getResources().getString(R.string.delete_confirm_description))
                .positiveText(getResources().getString(R.string.agree))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        APIService.doEventDelete(eventId);
                    }
                })
                .negativeText(getResources().getString(R.string.cancel))
                .show();
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
        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city)).setText(App.getCurrentCity().getName());

    }


    protected void updateEventsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventRealmResults = realm.where(Event.class)
                .beginGroup()
                .equalTo("author.id", App.getCurrentUser().getId())
                .or().isNull("author")
                .endGroup()
                .equalTo("localOnly", false)
                .findAllSorted("startDate", Sort.ASCENDING);

        orgEvents = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults);
        ((OrgEventsListAdapter) orgEventsRecyclerView.getAdapter()).updateData(orgEvents);
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

    private BroadcastReceiver deleteEventRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateEventsList();
            }
        };
    }

    protected void createScrollListener() {
        orgEventsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        if (deleteEventRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(deleteEventRequestDoneReceiver);
        }

        super.onDestroy();
    }



}
