package com.happ.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happ.R;
import com.happ.controllers.EventActivity;
import com.happ.controllers.EventsListAdapter;
import com.happ.models.Event;

import java.util.ArrayList;

/**
 * Created by dante on 8/8/16.
 */
public class BaseFeedFragment extends Fragment {
    protected ArrayList<Event> events;
    protected RecyclerView eventsListView;
    protected LinearLayoutManager eventsListLayoutManager;
    protected EventsListAdapter mEventAdapter;
    private int eventsFeedPageSize;

    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold;


    public static BaseFeedFragment newInstance() {
        return new BaseFeedFragment();
    }

    public BaseFeedFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        eventsFeedPageSize = Integer.parseInt(this.getString(R.string.event_feeds_page_size));
        visibleThreshold = Integer.parseInt(this.getString(R.string.event_feeds_visible_treshold_for_loading_next_items));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.feeds_fragment, container, false);
        final Activity activity = getActivity();

        eventsListView = (RecyclerView)view.findViewById(R.id.events_list_view);
        eventsListLayoutManager = new LinearLayoutManager(activity);
        eventsListView.setLayoutManager(eventsListLayoutManager);
        events = new ArrayList<>();

        mEventAdapter = new EventsListAdapter(activity, events);

        mEventAdapter.setOnSelectItemListener(new EventsListAdapter.SelectEventItemListener() {
            @Override
            public void onEventItemSelected(String eventId, ActivityOptionsCompat options) {
                Intent intent = new Intent(activity, EventActivity.class);
                intent.putExtra("event_id", eventId);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || options == null) {
                    activity.startActivity(intent);
                    (activity).overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
                } else {
                    activity.startActivity(intent, options.toBundle());
                }
            }

            @Override
            public void onEventEditSelected(String eventId) {

            }
        });
        eventsListView.setAdapter(mEventAdapter);

        createScrollListener();


        return view;
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
                        getEvents(nextPage, false);
                    }
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    protected void getEvents(int page, boolean favs) {
    }
}
