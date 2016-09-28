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

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.controllers.EventsListAdapter;
import com.happ.controllers.FeedActivity;
import com.happ.models.Event;
import com.happ.retrofit.APIService;
import com.happ.retrofit.HappRestClient;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by iztiev on 8/15/16.
 */
public class EverythingFeedFragment extends BaseFeedFragment {
    private BroadcastReceiver eventsRequestDoneReceiver;
    private BroadcastReceiver didUpvoteReceiver;
    private BroadcastReceiver didIsFavReceiver;
    private BroadcastReceiver filteredEventsDoneReceiver;

    private boolean isUndoing = false;

    public static EverythingFeedFragment newInstance() {
        return new EverythingFeedFragment();
    }

    public EverythingFeedFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);


            if(filteredEventsDoneReceiver == null) {
                filteredEventsDoneReceiver = createFilteredEventsRequestDoneReceiver();
                LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(filteredEventsDoneReceiver, new IntentFilter(BroadcastIntents.FILTERED_EVENTS_REQUEST_OK));
            }
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
    HappRestClient.getInstance().getEvents(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateEventsList();
    }

    protected void updateEventsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventRealmResults = realm.where(Event.class).equalTo("localOnly", false).findAllSorted("startDate", Sort.ASCENDING);
        events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
        ((EventsListAdapter)eventsListView.getAdapter()).updateData(events);
        realm.close();
    }

    protected void filteredEventsList() {
        String maxFree = ((FeedActivity)getActivity()).getMaxFree();
        Date startDate = ((FeedActivity)getActivity()).getStartD();
        Date endDate = ((FeedActivity)getActivity()).getEndD();
//        if (startDate == null) {
//            startDate = new Date();
//            startDate.setTime(0);
//        }

        Realm realm = Realm.getDefaultInstance();
        RealmQuery q = realm.where(Event.class).equalTo("localOnly", false);
        if (startDate != null) q.greaterThanOrEqualTo("startDate", startDate);
        if (endDate != null) q.lessThanOrEqualTo("endDate", endDate);
        if (maxFree != null && maxFree.length() > 0) q.equalTo("lowestPrice", 0);
        RealmResults<Event> eventRealmResults = q.findAllSorted("startDate", Sort.ASCENDING);

        events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
        ((EventsListAdapter)eventsListView.getAdapter()).updateData(events);
        realm.close();
    }

    @Override
    public void onDestroy() {
        if (filteredEventsDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(filteredEventsDoneReceiver);
        }
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


    @Override
    protected void getEvents(int page, boolean favs) {
        APIService.getEvents(page, false);
    }


    private BroadcastReceiver createEventsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateEventsList();
            }
        };
    }

    private BroadcastReceiver createUpvoteReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateEventsList();
            }
        };
    }

    private BroadcastReceiver createFavReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateEventsList();
            }
        };
    }

    private BroadcastReceiver createFilteredEventsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                filteredEventsList();
            }
        };
    }
}
