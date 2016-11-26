package com.happ.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.EventsListAdapter;
import com.happ.controllers_drawer.FeedActivity;
import com.happ.models.Event;
import com.happ.retrofit.APIService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by dante on 8/9/16.
 */
public class FavoriteFeedFragment extends BaseFeedFragment {
    private BroadcastReceiver eventsRequestDoneReceiver;
    private BroadcastReceiver didUpvoteReceiver;
    private BroadcastReceiver didIsFavReceiver;

    private BroadcastReceiver mUserSettingsChangedBroadcastReceiver;

    public static FavoriteFeedFragment newInstance() {
        return new FavoriteFeedFragment();
    }

    public FavoriteFeedFragment() {
    }

    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);


        if (eventsRequestDoneReceiver == null) {
            eventsRequestDoneReceiver = createEventsRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(eventsRequestDoneReceiver, new IntentFilter(BroadcastIntents.EVENTS_REQUEST_OK));
        }

        if (didUpvoteReceiver == null) {
            didUpvoteReceiver = createUpvoteReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(didUpvoteReceiver, new IntentFilter(BroadcastIntents.EVENT_UPVOTE_REQUEST_OK));
        }

        if (didIsFavReceiver == null) {
            didIsFavReceiver = createFavReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(didIsFavReceiver, new IntentFilter(BroadcastIntents.EVENT_UNFAV_REQUEST_OK));
        }

        if (mUserSettingsChangedBroadcastReceiver == null) {
            mUserSettingsChangedBroadcastReceiver = createUserChangedBroadcastReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(mUserSettingsChangedBroadcastReceiver, new IntentFilter(BroadcastIntents.GET_CURRENT_USER_REQUEST_OK));
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateFavoritesEventsList();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFavoritesEventsList();
    }

    protected void updateFavoritesEventsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventRealmResults = realm.where(Event.class).equalTo("inFavorites", true).equalTo("localOnly", false).findAllSorted("startDate", Sort.ASCENDING);
        events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
        ((EventsListAdapter)eventsListView.getAdapter()).updateData(events);
        realm.close();

        if (events.isEmpty()) {
            mRLEmptyFrom.setVisibility(View.VISIBLE);
            mPersonalSubText.setText(R.string.feed_favorites_empty);
            mBtnEmptyForm.setText(R.string.find_awesome_events);
            mBtnEmptyForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(App.getContext(), "Favorites Button action", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mRLEmptyFrom.setVisibility(View.GONE);
        }

        mFeedEventsProgress.setVisibility(View.GONE);
        mDarkViewProgress.setVisibility(View.GONE);
    }

    @Override
    protected void getEvents(int page, boolean favs) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String maxFree = ((FeedActivity)getActivity()).getMaxFree();
        Date startDate = ((FeedActivity)getActivity()).getStartD();
        Date endDate = ((FeedActivity)getActivity()).getEndD();
        String feedSearchText = ((FeedActivity)getActivity()).getFeedSearch();

        if (startDate != null || endDate != null || (maxFree != null && maxFree.length() > 0)) {
            String sD = "";
            String eD = "";
            if (startDate != null) sD = sdf.format(startDate);
            if (endDate != null) eD = sdf.format(endDate);
            APIService.getFilteredEvents(page,feedSearchText, sD, eD, maxFree, true);
        } else {
            APIService.getEvents(page, true);
        }
    }
    
    protected BroadcastReceiver createUserChangedBroadcastReceiver() {
        return  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra("EVENTS_CHANGED", false)) {
                    getEvents(0, true);
                }
            }
        };
    }
    private BroadcastReceiver createEventsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateFavoritesEventsList();
            }
        };
    }
    private BroadcastReceiver createUpvoteReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateFavoritesEventsList();
            }
        };
    }

    private BroadcastReceiver createFavReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateFavoritesEventsList();
            }
        };
    }

    @Override
    public void onDestroy() {
        if (eventsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(eventsRequestDoneReceiver);
        }
        if (didUpvoteReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(didUpvoteReceiver);
        }
        if (didIsFavReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(didIsFavReceiver);
        }
        super.onDestroy();
    }

}
