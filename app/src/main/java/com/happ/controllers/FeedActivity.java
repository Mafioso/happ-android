package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.fragments.EverythingFeedFragment;
import com.happ.fragments.FavoriteFeedFragment;
import com.happ.models.Interest;
import com.happ.models.User;
import com.happ.models.UserAccount;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

public class FeedActivity extends AppCompatActivity {

    protected final String[] mTabNames = {"Everything", "Favorites"};
    protected ArrayList<Fragment> mTabFragments;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private Menu menu;
    protected ArrayList<User> user;
    private String username;


    private BroadcastReceiver userRequestDoneReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        if (userRequestDoneReceiver == null) {
            userRequestDoneReceiver = createLoginSuccessReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(userRequestDoneReceiver, new IntentFilter(BroadcastIntents.USEREDIT_REQUEST_OK));
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(App.getCurrentCity().getName());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    App.doLogout(FeedActivity.this);
                }

                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    Intent goToFeedIntent = new Intent(FeedActivity.this, SettingsActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);

                }
                if (menuItem.getItemId() == R.id.nav_item_organizer) {
                    Intent goToFeedIntent = new Intent(FeedActivity.this, OrganizerModeActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);
                }

                if (menuItem.getItemId() == R.id.nav_item_interests) {
                    Intent intent = new Intent(FeedActivity.this, SelectInterestsActivity.class);
                    intent.putExtra("is_full", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0);

                }

                if (menuItem.getItemId() == R.id.nav_item_feed) {
                    Intent intent = new Intent(FeedActivity.this, FeedActivity.class);
                    intent.putExtra("is_full", true);
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
                Intent intent = new Intent(FeedActivity.this, UserAccount.class);
                startActivity(intent);
            }
        });
        ((CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, UserAccount.class);
                startActivity(intent);
            }
        });

        mTabFragments = new ArrayList<>();
        for (int i=0; i<mTabNames.length; i++) {
            mTabFragments.add(null);
        }
        FeedPagerAdapter adapter = new FeedPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_filter:
                Toast.makeText(FeedActivity.this, "HELLO", Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected class FeedPagerAdapter extends FragmentStatePagerAdapter {

        public FeedPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (mTabFragments.get(position) == null) {
                switch (position) {
                    case 1:
                        mTabFragments.set(position, FavoriteFeedFragment.newInstance());
                        break;
                    default:
                        mTabFragments.set(position, EverythingFeedFragment.newInstance());
                        break;
                }
            }
            return mTabFragments.get(position);
        }

        @Override
        public int getCount() {
            return mTabNames.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabNames[position];
        }
    }

    private BroadcastReceiver createLoginSuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUserList();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserList();
    }

    protected void updateUserList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> eventRealmResults = realm.where(User.class).equalTo("username", username).findAll();
        user = (ArrayList<User>)realm.copyFromRealm(eventRealmResults);
//        ((EventsListAdapter)eventsListView.getAdapter()).updateData(events);
        realm.close();
    }

    @Override
    protected void onDestroy() {

        if (userRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(userRequestDoneReceiver);
        }
        super.onDestroy();
    }

}

