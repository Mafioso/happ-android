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
import com.happ.R;
import com.happ.controllers.EventsListAdapter;
import com.happ.models.Event;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by dante on 8/9/16.
 */
public class FavoriteFeedFragment extends BaseFeedFragment {
    private BroadcastReceiver eventsRequestDoneReceiver;

    public static FavoriteFeedFragment newInstance() {
        return new FavoriteFeedFragment();
    }

    public FavoriteFeedFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        eventsRequestDoneReceiver = createEventsRequestDoneReceiver();
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(eventsRequestDoneReceiver, new IntentFilter(BroadcastIntents.EVENTS_REQUEST_OK));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateEventsList();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateEventsList();
    }

    @Override
    public void onDestroy() {
        if (eventsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(eventsRequestDoneReceiver);
        }
        super.onDestroy();
    }

    protected void updateEventsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventRealmResults = realm.where(Event.class).equalTo("inFavorites", true).findAllSorted("startDate", Sort.ASCENDING);
        events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
        ((EventsListAdapter)eventsListView.getAdapter()).updateData(events);
        realm.close();
    }

    @Override
    protected void getEvents(int page, boolean favs) {
        super.getEvents(page, true);
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
