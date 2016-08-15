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

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class FeedActivity extends AppCompatActivity  {
    private BroadcastReceiver eventsRequestDoneReceiver;
    private ArrayList<Event> events;
    private RecyclerView eventsListView;
    private LinearLayoutManager eventsListLayoutManager;
    private int eventsFeedPageSize;

    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsFeedPageSize = Integer.parseInt(this.getString(R.string.event_feeds_page_size));
        visibleThreshold = Integer.parseInt(this.getString(R.string.event_feeds_visible_treshold_for_loading_next_items));
        setContentView(R.layout.activity_feed);


//        final String url = "http://3dpapa.ru/wp-content/uploads/2014/07/alex11.jpg";
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    RequestManager rm = Glide.with(App.getContext());
//                    DrawableTypeRequest<String> dtr = rm.load(url);
//                    BitmapTypeRequest<String> btr = dtr.asBitmap();
//                    FutureTarget<Bitmap> ft = btr.into(-1, -1);
//                    Bitmap bm = ft.get();
//                    int a = 100;
//                } catch (Exception ex) {
//                    System.out.print(ex.getLocalizedMessage());
//                }
//            }
//        }).start();


        eventsListView = (RecyclerView)findViewById(R.id.events_list_view);
        eventsListLayoutManager = new LinearLayoutManager(this);
        eventsListView.setLayoutManager(eventsListLayoutManager);
        events = new ArrayList<>();

        EventsListAdapter ela = new EventsListAdapter(this, events);
        eventsListView.setAdapter(ela);

        createScrollListener();

        eventsRequestDoneReceiver = createEventsRequestDoneReceiver();
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(eventsRequestDoneReceiver, new IntentFilter(BroadcastIntents.EVENTS_REQUEST_OK));
        APIService.getEvents();
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

}
