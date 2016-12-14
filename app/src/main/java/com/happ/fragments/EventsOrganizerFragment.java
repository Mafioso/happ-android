package com.happ.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.OrgEventsListAdapter;
import com.happ.controllers.EditCreateActivity;
import com.happ.controllers_drawer.EventActivity;
import com.happ.models.Event;
import com.happ.retrofit.APIService;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by dante on 9/14/16.
 */
public class EventsOrganizerFragment extends Fragment {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public EventsOrganizerFragment() {

    }

    public static EventsOrganizerFragment newInstance() {
        return new EventsOrganizerFragment();
    }

    protected ArrayList<Event> orgEvents;
    protected RecyclerView orgEventsRecyclerView;
    protected LinearLayoutManager eventsListLayoutManager;

    private int eventsFeedPageSize;
    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold;

    private BroadcastReceiver eventsRequestDoneReceiver;
    private BroadcastReceiver deleteEventRequestDoneReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.org_events_fragment, container, false);
        final Activity activity = getActivity();

        orgEventsRecyclerView = (RecyclerView) view.findViewById(R.id.org_events_list_view);

        eventsFeedPageSize = Integer.parseInt(this.getString(R.string.event_feeds_page_size));
        visibleThreshold = Integer.parseInt(this.getString(R.string.event_feeds_visible_treshold_for_loading_next_items));

        eventsListLayoutManager = new LinearLayoutManager(activity);
        orgEventsRecyclerView.setLayoutManager(eventsListLayoutManager);
        orgEvents = new ArrayList<>();

        OrgEventsListAdapter oela = new OrgEventsListAdapter(activity, orgEvents);
        oela.setOnSelectItemListener(new OrgEventsListAdapter.SelectEventItemListener() {
            @Override
            public void onEventItemSelected(String eventId, ActivityOptionsCompat options) {
                Intent intent = new Intent(activity, EventActivity.class);
                intent.putExtra("event_id", eventId);
                intent.putExtra("is_organizer", true);
                intent.putExtra("in_event_activity", true);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || options == null) {
                    EventsOrganizerFragment.this.startActivity(intent);
                    (activity).overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
                } else {
                    EventsOrganizerFragment.this.startActivity(intent, options.toBundle());
                }
            }

            @Override
            public void onEventEditSelected(String eventId) {
                Intent intent = new Intent(activity, EditCreateActivity.class);
                intent.putExtra("event_id", eventId);
                startActivity(intent);
            }
        });
        orgEventsRecyclerView.setAdapter(oela);

        if (eventsRequestDoneReceiver == null) {
            eventsRequestDoneReceiver = createEventsRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(eventsRequestDoneReceiver, new IntentFilter(BroadcastIntents.ORG_EVENTS_REQUEST_OK));
        }

        if (deleteEventRequestDoneReceiver == null) {
            deleteEventRequestDoneReceiver = deleteEventRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(deleteEventRequestDoneReceiver, new IntentFilter(BroadcastIntents.EVENTDELETE_REQUEST_OK));
        }

        getEvents(1);
        createScrollListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateEventsList();
    }

    protected void updateEventsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventRealmResults = realm.where(Event.class)
                .beginGroup()
                    .equalTo("author.id", App.getCurrentUser().getId())
                    .equalTo("localOnly", false)
                .endGroup()
                .findAllSorted("startDate", Sort.DESCENDING);
        orgEvents = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults);
        ((OrgEventsListAdapter) orgEventsRecyclerView.getAdapter()).updateData(orgEvents);
        realm.close();
    }

    private BroadcastReceiver createEventsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateEventsList();

            }
        };
    }

    private BroadcastReceiver deleteEventRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateEventsList();
            }
        };
    }

    protected void createScrollListener() {
        orgEventsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        getEvents(nextPage);
                    }
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    protected void getEvents(int page) {
        APIService.getOrgEvents(page);
    }

    @Override
    public void onDestroy() {
        if (eventsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(eventsRequestDoneReceiver);
        }

        if (deleteEventRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(deleteEventRequestDoneReceiver);
        }

        super.onDestroy();
    }

}
