package com.happ.controllers;

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
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());

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
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_feed, menu);
//        boolean showOption = false;
//        if (mActivity.getDeadline() != null && mActivity.getDeadline().getTime() > 0 &&
//                (mActivity.getDateFinished() == null || mActivity.getDateFinished().getTime() < 1)
//                && mActivity.getAuthor().getId() == RealmReadAdapter.getInstance().getCurrentUserId()) {
//            showOption = true;
//        } else if (mActivity.getDeadline() == null || mActivity.getDeadline().getTime() < 1
//                && mActivity.getAuthor().getId() == RealmReadAdapter.getInstance().getCurrentUserId()) {
//            showOption = true;
//        }
//        if (showOption)
//            getMenuInflater().inflate(R.menu.menu_activity_opened, menu);
        return true;
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

