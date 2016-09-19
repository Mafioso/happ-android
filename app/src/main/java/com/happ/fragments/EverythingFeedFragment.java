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
import com.happ.models.Event;
import com.happ.retrofit.APIService;
import com.happ.retrofit.HappRestClient;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by iztiev on 8/15/16.
 */
public class EverythingFeedFragment extends BaseFeedFragment{
    private BroadcastReceiver eventsRequestDoneReceiver;

    public static EverythingFeedFragment newInstance() {
        return new EverythingFeedFragment();
    }

    public EverythingFeedFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

            eventsRequestDoneReceiver = createEventsRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(eventsRequestDoneReceiver, new IntentFilter(BroadcastIntents.EVENTS_REQUEST_OK));

        HappRestClient.getInstance().getEvents();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateEventsList();
    }


    public void updateEventListTest() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventRealmResults = realm.where(Event.class).findAllSorted("lowestPrice", Sort.ASCENDING);
        events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
        ((EventsListAdapter)eventsListView.getAdapter()).updateData(events);
        realm.close();
    }

    protected void updateEventsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventRealmResults = realm.where(Event.class).findAllSorted("startDate", Sort.ASCENDING);
        events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
        ((EventsListAdapter)eventsListView.getAdapter()).updateData(events);
        realm.close();
    }

    @Override
    public void onDestroy() {
        if (eventsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(eventsRequestDoneReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected void getEvents(int page) {
        APIService.getEvents(page);
    }

    private BroadcastReceiver createEventsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateEventsList();
            }
        };
    }
}
