package com.happ.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.happ.App;
import com.happ.R;
import com.happ.fragments.EverythingFeedFragment;
import com.happ.fragments.FavoriteFeedFragment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedActivity extends AppCompatActivity {

    protected final String[] mTabNames = {"Everything", "Favorites"};
    protected ArrayList<Fragment> mTabFragments;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private Menu menu;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

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
                    Intent goToFeedIntent = new Intent(FeedActivity.this, UserActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);

                }
                if (menuItem.getItemId() == R.id.nav_item_organizer) {
                    Intent goToFeedIntent = new Intent(FeedActivity.this, OrganizerModeActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.nav_item_feed).setChecked(true);

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar)).setImageDrawable(getResources().getDrawable(R.drawable.avatar));

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
//        getMenuInflater().inflate(R.menu.popupmenu, menu);
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

}

