package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.Event;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity {
    private BroadcastReceiver eventsRequestDoneReceiver;
    private ArrayList<Event> events;
    private RecyclerView rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recyclerview);


//        rv = (RecyclerView)findViewById(R.id.rv);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        rv.setLayoutManager(llm);
//        rv.setHasFixedSize(true);
//
//        events = new ArrayList<Event>();
//
//        RVAdapter adapter = new RVAdapter(events);
//        rv.setAdapter(adapter);
//
        eventsRequestDoneReceiver = createEventsRequestDoneReceiver();
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(eventsRequestDoneReceiver, new IntentFilter(BroadcastIntents.EVENTS_REQUEST_OK));
//        HappRestClient.getInstance().getEvents();
//        APIService.getEvents();
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

//                Realm realm = Realm.getDefaultInstance();
//                RealmResults<Event> evts = realm.where(Event.class).findAll();
//                events = (ArrayList<Event>)realm.copyFromRealm(evts.subList(0, evts.size()));
//                ((RVAdapter)rv.getAdapter()).updateData(events);
////                tw = (TextView) findViewById(R.id.textView2);
////                tw.setText(String.valueOf(evt.getTitle()));
//                realm.close();
            }
        };
    }

}
