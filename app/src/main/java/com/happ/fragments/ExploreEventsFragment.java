package com.happ.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.ExploreListAdapter;
import com.happ.controllers_drawer.EventActivity;
import com.happ.models.Event;
import com.happ.retrofit.APIService;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by dante on 9/5/16.
 */
public class ExploreEventsFragment extends Fragment {

    protected RecyclerView mExploreRecyclerView;
    private ArrayList<Event> events;
    private ExploreListAdapter mExploreListAdapter;

    private GridLayoutManager mExploreEventGridLayoutManager;

    private int interestsPageSize;
    private boolean loading = true;
    private boolean dataLoading = false;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold;
    private HideShowFilterListener hideshowFilterListener;
    private BroadcastReceiver exploreEventsRequestDoneReceiver;

    public ExploreEventsFragment() {

    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public interface HideShowFilterListener {
        void onHideFilter();
        void onShowFilter();
    }

    public void setOnHideShowFilterListener(HideShowFilterListener listener) {
        this.hideshowFilterListener = listener;
    }
    public static ExploreEventsFragment newInstance() {
        return new ExploreEventsFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        interestsPageSize = Integer.parseInt(this.getString(R.string.event_feeds_page_size));
        visibleThreshold = 6;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.explore_events_fragment, container, false);
        final Activity activity = getActivity();

        hideshowFilterListener.onHideFilter();

        mExploreRecyclerView = (RecyclerView) contentView.findViewById(R.id.fragment_explore_rv);
        mExploreEventGridLayoutManager = new GridLayoutManager(activity, 3);
        mExploreRecyclerView.setHasFixedSize(true);
        mExploreRecyclerView.setLayoutManager(mExploreEventGridLayoutManager);
        events = new ArrayList<>();
        mExploreListAdapter = new ExploreListAdapter(activity, events);

        mExploreListAdapter.setOnSelectEventExploreListener(new ExploreListAdapter.SelectEventExploreItemListener() {
            @Override
            public void onExploreEventItemSelected(Event event) {
                Intent intent = new Intent(getActivity(), EventActivity.class);
                intent.putExtra("event_id", event.getId());
                intent.putExtra("in_event_activity", true);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
                } else {
                    startActivity(intent);
                }
            }
        });

        mExploreRecyclerView.setAdapter(mExploreListAdapter);

        APIService.getExploreEvents(1);
        createScrollListener();

        if(exploreEventsRequestDoneReceiver == null) {
            exploreEventsRequestDoneReceiver = createExploreEventsRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(exploreEventsRequestDoneReceiver, new IntentFilter(BroadcastIntents.EXPLORE_EVENTS_REQUEST_OK));
        }
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateExploreEvents();
    }

    protected void updateExploreEvents() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventRealmResults = realm.where(Event.class).findAll();
        events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults);
        ((ExploreListAdapter)mExploreRecyclerView.getAdapter()).updateData(events);
        realm.close();
    }

    protected void createScrollListener() {
        mExploreRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {

                    visibleItemCount = mExploreEventGridLayoutManager.getChildCount();
                    totalItemCount = mExploreEventGridLayoutManager.getItemCount();
                    firstVisibleItem = mExploreEventGridLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }

                    if (! dataLoading && !loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {
                        loading = true;
                        int nextPage = (totalItemCount / interestsPageSize) + 1;
                        dataLoading = true;

                        APIService.getExploreEvents(nextPage);
                    }
                }
                if (dy < 0) {
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        hideshowFilterListener.onShowFilter();
    }

    private BroadcastReceiver createExploreEventsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateExploreEvents();
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (exploreEventsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(exploreEventsRequestDoneReceiver);
            exploreEventsRequestDoneReceiver = null;
        }
    }
}
