package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.Event;
import com.happ.retrofit.APIService;
import com.happ.retrofit.HappRestClient;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class FeedActivity extends AppCompatActivity {
    private BroadcastReceiver eventsRequestDoneReceiver;
    private ArrayList<Event> events;
    private RecyclerView eventsListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feed);

        eventsListView = (RecyclerView)findViewById(R.id.events_list_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        eventsListView.setLayoutManager(llm);

//        events = new ArrayList<Event>();
//        for (int i=0; i<200; i++) {
//            Event event = new Event();
//            event.setId(i+1);
//            event.setTitle("Title Numero " + String.valueOf(i+1));
//            long datems = (long)(Math.random()*5184000000l + 1470312000000l);
//            event.setStart_date(new Date(datems));
//            events.add(event);
//        }
//
//        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        realm.copyToRealmOrUpdate(events);
//        realm.commitTransaction();
//        realm.close();
        events = new ArrayList<>();

        EventsListAdapter ela = new EventsListAdapter(this, events);
        eventsListView.setAdapter(ela);
//
        eventsRequestDoneReceiver = createEventsRequestDoneReceiver();
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(eventsRequestDoneReceiver, new IntentFilter(BroadcastIntents.EVENTS_REQUEST_OK));
//        HappRestClient.getInstance().getEvents();
        APIService.getEvents();
//        updateEventsList();
    }

    protected void updateEventsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventRealmResults = realm.where(Event.class).findAllSorted("start_date", Sort.ASCENDING);
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

}
