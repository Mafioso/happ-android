package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.fragments.BaseFeedFragment;
import com.happ.fragments.EverythingFeedFragment;
import com.happ.fragments.FavoriteFeedFragment;
import com.happ.models.Event;
import com.happ.retrofit.APIService;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class FeedActivity extends AppCompatActivity  {
    private BroadcastReceiver eventsRequestDoneReceiver;
    protected ArrayList<Event> events;
    protected RecyclerView eventsListView;
    protected LinearLayoutManager eventsListLayoutManager;
    private int eventsFeedPageSize;

    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold;

    protected final String[] mTabNames = {"Everything", "Favorites"};
    protected ArrayList<Fragment> mTabFragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


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
                        APIService.getEvents(nextPage);
                    }
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }



    protected void updateEventsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventRealmResults = realm.where(Event.class).findAllSorted("startDate", Sort.ASCENDING);
        events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
        ((EventsListAdapter)eventsListView.getAdapter()).updateData(events);
        realm.close();
    }

    @Override
    protected void onDestroy() {
        if (eventsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(eventsRequestDoneReceiver);
        }
        super.onDestroy();
    }

    private BroadcastReceiver createEventsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            updateEventsList();
            }
        };
    }


//    public static class DesignDemoFragment extends Fragment {
//        private static final String TAB_POSITION = "tab_position";
//
//        public DesignDemoFragment() {
//
//        }
//
//        public static DesignDemoFragment newInstance(int tabPosition) {
//            DesignDemoFragment fragment = new DesignDemoFragment();
//            Bundle args = new Bundle();
//            args.putInt(TAB_POSITION, tabPosition);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        @Nullable
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//            Bundle args = getArguments();
//            int tabPosition = args.getInt(TAB_POSITION);
//            TextView tv = new TextView(getActivity());
//            tv.setGravity(Gravity.CENTER);
//            tv.setText("Text in Tab #" + tabPosition);
//            return tv;
//        }
//    }

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
//            switch (position) {
//                case 1:
//                    return (FavoriteFeedFragment)mTabFragments.get(position);
//                default:
//                    return (EverythingFeedFragment)mTabFragments.get(position);
//            }
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

