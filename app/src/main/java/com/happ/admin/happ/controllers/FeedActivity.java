package com.happ.admin.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.happ.admin.happ.App;
import com.happ.admin.happ.BroadcastIntents;
import com.happ.admin.happ.R;
import com.happ.admin.happ.models.Events;
import com.happ.admin.happ.retrofit.APIService;
import com.happ.admin.happ.retrofit.HappRestClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmResults;

public class FeedActivity extends AppCompatActivity {
    private BroadcastReceiver eventsRequestDoneReceiver;
//    private TextView tw;
    private ArrayList<Events> events;
    private RecyclerView rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recyclerview);


        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        events = new ArrayList<Events>();

        RVAdapter adapter = new RVAdapter(events);
        rv.setAdapter(adapter);

        eventsRequestDoneReceiver = createEventsRequestDoneReceiver();
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(eventsRequestDoneReceiver, new IntentFilter(BroadcastIntents.EVENTS_REQUEST_OK));
        HappRestClient.getInstance().getEvents();
        APIService.getEvents();
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

                Realm realm = Realm.getDefaultInstance();
                RealmResults<Events> evts = realm.where(Events.class).findAll();
                events = (ArrayList<Events>)realm.copyFromRealm(evts.subList(0, evts.size()));
                ((RVAdapter)rv.getAdapter()).updateData(events);
//                tw = (TextView) findViewById(R.id.textView2);
//                tw.setText(String.valueOf(evt.getTitle()));
                realm.close();
            }
        };
    }



    protected void outputData(String data) {
        System.out.println(data);
    }

//         Call<EventsResponse> call = service.getEvents();

}
