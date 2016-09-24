package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.Interest;
import com.happ.models.UserAccount;
import com.happ.retrofit.APIService;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by dante on 9/6/16.
 */
public class SelectInterestsActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected RecyclerView mInterestsRecyclerView;
    private ArrayList<Interest> interests;
    private InterestsListAdapter mInterestsListAdapter;
    private BroadcastReceiver interestsRequestDoneReceiver;
    private BroadcastReceiver setInterestsOKReceiver;
    private LinearLayoutManager interestsListLayoutManager;
    private FloatingActionButton mFab;
    private int interestsPageSize;
    private DrawerLayout mDrawerLayout;

    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold;

    private boolean fullActivity = false;
    private NavigationView navigationView;

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar)).setImageDrawable(getResources().getDrawable(R.drawable.avatar));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_interests);
        Intent intent = getIntent();
        fullActivity = intent.getBooleanExtra("is_full", false);

        interestsPageSize = Integer.parseInt(this.getString(R.string.event_feeds_page_size));
        visibleThreshold = Integer.parseInt(this.getString(R.string.event_feeds_visible_treshold_for_loading_next_items));

        setTitle(getResources().getString(R.string.select_interest_title));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedInterests = mInterestsListAdapter.getSelectedInterests();
                // SEND DATA TO SERVER
                APIService.setInterests(selectedInterests);
                mFab.setVisibility(View.GONE);
            }
        });


        mInterestsRecyclerView = (RecyclerView) findViewById(R.id.activity_interests_rv);
        interestsListLayoutManager = new LinearLayoutManager(this);
        mInterestsRecyclerView.setLayoutManager(interestsListLayoutManager);

        Realm realm = Realm.getDefaultInstance();
        interests = new ArrayList<>();

        try {
            RealmResults<Interest> interestsResults = realm.where(Interest.class).isNull("parentId").findAll();
            interests = (ArrayList<Interest>) realm.copyFromRealm(interestsResults);
        } catch (Exception ex) {
            Log.e("HAPP", "SelectInterestActivity > onCreate "+ex.getLocalizedMessage());
        } finally {
            realm.close();
        }

        mInterestsListAdapter = new InterestsListAdapter(this, interests);
        mInterestsRecyclerView.setAdapter(mInterestsListAdapter);
        mFab.setVisibility(View.VISIBLE);

        if (interestsRequestDoneReceiver == null) {
            interestsRequestDoneReceiver = createInterestsRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(interestsRequestDoneReceiver, new IntentFilter(BroadcastIntents.INTERESTS_REQUEST_OK));
        }
        if (setInterestsOKReceiver == null) {
            setInterestsOKReceiver = createSetInterestsOKReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(setInterestsOKReceiver, new IntentFilter(BroadcastIntents.SET_INTERESTS_OK));
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (fullActivity) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {

                    if (menuItem.getItemId() == R.id.nav_item_logout) {
                        App.doLogout(SelectInterestsActivity.this);
                    }

                    if (menuItem.getItemId() == R.id.nav_item_settings) {
                        Intent goToFeedIntent = new Intent(SelectInterestsActivity.this, SettingsActivity.class);
                        goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(goToFeedIntent);
                        overridePendingTransition(0,0);

                    }
                    if (menuItem.getItemId() == R.id.nav_item_organizer) {
                        Intent goToFeedIntent = new Intent(SelectInterestsActivity.this, OrganizerModeActivity.class);
                        goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(goToFeedIntent);
                        overridePendingTransition(0,0);
                    }

                    if (menuItem.getItemId() == R.id.nav_item_interests) {
                        Intent intent = new Intent(SelectInterestsActivity.this, SelectInterestsActivity.class);
                        intent.putExtra("is_full", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(0,0);

                    }

                    if (menuItem.getItemId() == R.id.nav_item_feed) {
                        Intent intent = new Intent(SelectInterestsActivity.this, FeedActivity.class);
                        intent.putExtra("is_full", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }
                    mDrawerLayout.closeDrawers();
                    return true;
                }
            });

            navigationView.getMenu().findItem(R.id.nav_item_interests).setChecked(true);
            ((TextView)navigationView.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
            ((CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar)).setImageDrawable(getResources().getDrawable(R.drawable.avatar));

            ((TextView)navigationView.getHeaderView(0).findViewById(R.id.drawer_username)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SelectInterestsActivity.this, UserActivity.class);
                    startActivity(intent);
                }
            });
            ((CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SelectInterestsActivity.this, UserActivity.class);
                    startActivity(intent);
                }
            });
        } else {
//            mDrawerLayout.setVisibility(View.GONE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            navigationView.setVisibility(View.GONE);
        }


        APIService.getInterests();
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

    private BroadcastReceiver createInterestsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateInterestsList();
            }
        };
    }

    private BroadcastReceiver createSetInterestsOKReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent feedIntent = new Intent(SelectInterestsActivity.this, FeedActivity.class);
                feedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SelectInterestsActivity.this.startActivity(feedIntent);
                SelectInterestsActivity.this.overridePendingTransition(0,0);
            }
        };
    }


    public interface OnInterestSelectListener {
        public void onInterestSelected(Interest interest);
    }

    protected void updateInterestsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Interest> interestsRealmResults = realm.where(Interest.class).isNull("parentId").findAll();
        interests = (ArrayList<Interest>)realm.copyFromRealm(interestsRealmResults);
        mInterestsListAdapter.updateData(interests);
        realm.close();
    }

    protected void createScrollListener() {
        mInterestsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    if (mFab.getVisibility() != View.GONE) mFab.hide();
                    visibleItemCount = interestsListLayoutManager.getChildCount();
                    totalItemCount = interestsListLayoutManager.getItemCount();
                    firstVisibleItem = interestsListLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }

                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {
                        loading = true;
                        int nextPage = (totalItemCount / interestsPageSize) + 1;
                        APIService.getInterests(nextPage);
                    }
                }
                if (dy < 0) {
                    if (mFab.getVisibility() != View.VISIBLE) mFab.show();
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (interestsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(interestsRequestDoneReceiver);
            interestsRequestDoneReceiver = null;
        }
        if (setInterestsOKReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(setInterestsOKReceiver);
            setInterestsOKReceiver = null;
        }
    }
}
