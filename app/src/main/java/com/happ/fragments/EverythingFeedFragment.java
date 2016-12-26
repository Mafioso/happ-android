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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.EventsListAdapter;
import com.happ.controllers_drawer.FeedActivity;
import com.happ.controllers_drawer.SelectInterestsActivity;
import com.happ.models.Event;
import com.happ.retrofit.APIService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Case;
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
    private BroadcastReceiver mUserSettingsChangedBroadcastReceiver;
    private BroadcastReceiver changeCityDoneReceiver;

    private String feedSearchText;
    private Date startDate;
    private Date endDate;
    private String maxFree;
    private String popularityEvents;

    private boolean isUndoing = false;

    public static EverythingFeedFragment newInstance() {
        return new EverythingFeedFragment();
    }

    public EverythingFeedFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        feedSearchText = ((FeedActivity)getActivity()).getFeedSearch();
        startDate = ((FeedActivity)getActivity()).getStartD();
        endDate = ((FeedActivity)getActivity()).getEndD();
        maxFree = ((FeedActivity)getActivity()).getMaxFree();
        popularityEvents = ((FeedActivity)getActivity()).getPopularityEvents();

        if (!feedSearchText.equals("") || !maxFree.equals("") || startDate != null || endDate != null || !popularityEvents.equals("")) {
            filteredEventsList();
        } else {
            updateEventsList();
        }
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

            if (mUserSettingsChangedBroadcastReceiver == null) {
                mUserSettingsChangedBroadcastReceiver = createUserChangedBroadcastReceiver();
                LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(mUserSettingsChangedBroadcastReceiver, new IntentFilter(BroadcastIntents.GET_CURRENT_USER_REQUEST_OK));
            }

            if (changeCityDoneReceiver == null) {
                changeCityDoneReceiver = changeCityReceiver();
                LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(changeCityDoneReceiver, new IntentFilter(BroadcastIntents.SET_CITIES_OK));
            }

            if (App.hasConnection(getContext())) {
                getEvents(1, false);
            } else {
                Toast.makeText(getContext(), "Events not updated", Toast.LENGTH_SHORT).show();
            }

        return view;
    }

    protected void updateEventsList() {
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        today = cal.getTime();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventRealmResults = realm.where(Event.class).equalTo("localOnly", false).notEqualTo("author.id", userId).findAllSorted("startDate", Sort.ASCENDING);
        events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
        ((EventsListAdapter)eventsListView.getAdapter()).setSortByPopularity(false);
        ((EventsListAdapter)eventsListView.getAdapter()).updateData(events);
        realm.close();

        iSEmptyForm();
    }

    protected void filteredEventsList() {
        feedSearchText = ((FeedActivity)getActivity()).getFeedSearch();
        startDate = ((FeedActivity)getActivity()).getStartD();
        endDate = ((FeedActivity)getActivity()).getEndD();
        maxFree = ((FeedActivity)getActivity()).getMaxFree();
        popularityEvents = ((FeedActivity)getActivity()).getPopularityEvents();

        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Event> q = realm.where(Event.class).beginGroup();
        q.notEqualTo("author.id", userId);
        q.equalTo("localOnly", false);
        if (!feedSearchText.equals("")) q.contains("title", feedSearchText, Case.INSENSITIVE);
        if (startDate != null) q.greaterThanOrEqualTo("startDate", startDate);
        if (endDate != null) q.lessThanOrEqualTo("startDate", endDate);
        if (maxFree.equals("")) q.equalTo("highestPrice", 0);
        if (!popularityEvents.equals("")) {
            RealmResults<Event> eventRealmResults = q.endGroup().findAll().sort("votesCount", Sort.DESCENDING, "startDate", Sort.ASCENDING);
            events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
        } else {
            RealmResults<Event> eventRealmResults = q.endGroup().greaterThanOrEqualTo("startDate", new Date()).findAllSorted("startDate", Sort.ASCENDING);
            events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
        }
        ((EventsListAdapter)eventsListView.getAdapter()).setSortByPopularity(popularityEvents.equals("popular"));
        ((EventsListAdapter)eventsListView.getAdapter()).updateData(events);
        realm.close();

        iSEmptyForm();
    }


    @Override
    protected void getEvents(int page, boolean favs) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if ((FeedActivity)getActivity() != null) {
            feedSearchText = ((FeedActivity)getActivity()).getFeedSearch();
            startDate = ((FeedActivity)getActivity()).getStartD();
            endDate = ((FeedActivity)getActivity()).getEndD();
            maxFree = ((FeedActivity)getActivity()).getMaxFree();
            popularityEvents = ((FeedActivity)getActivity()).getPopularityEvents();

            String sD = "";
            String eD = "";
            if (startDate != null) sD = sdf.format(startDate);
            if (endDate != null) eD = sdf.format(endDate);

            if (!feedSearchText.equals("") || startDate != null || endDate != null || !maxFree.equals("") || !popularityEvents.equals("")) {
                APIService.getFilteredEvents(page, feedSearchText, sD, eD, maxFree, popularityEvents, false);
            } else {
                APIService.getEvents(page, false);
            }
        }
    }

    private void iSEmptyForm() {
        if (events.isEmpty()) {

            mRLEmptyFrom.setVisibility(View.VISIBLE);
            mPersonalSubText.setText(R.string.feed_everything_empty);
            mBtnEmptyForm.setText(R.string.add_more_interests);
            mIVEmpty.setImageResource(R.drawable.empty_feed);

            mChangeColorIconToolbarListener.onChangeColorIconToolbar(R.drawable.ic_menu_gray, R.drawable.ic_filter_gray);

            mBtnEmptyForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.animatealpha);
                    mBtnEmptyForm.startAnimation(anim);

                    Intent intent = new Intent(App.getContext(), SelectInterestsActivity.class);
                    intent.putExtra("is_full", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            });
        } else {
            mRLEmptyFrom.setVisibility(View.GONE);
            mChangeColorIconToolbarListener.onChangeColorIconToolbar(R.drawable.ic_menu, R.drawable.ic_filter);
        }

        mFeedEventsProgress.setVisibility(View.GONE);
        mDarkViewProgress.setVisibility(View.GONE);
    }


    private BroadcastReceiver changeCityReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                clearRealmForOldCity();
                getEvents(1, false);
            }
        };
    }

    private BroadcastReceiver createEventsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateEventsList();
            }
        };
    }

    protected BroadcastReceiver createUserChangedBroadcastReceiver() {
        return  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra("EVENTS_CHANGED", false)) {
                    getEvents(1,false);
                }
            }
        };
    }

    private BroadcastReceiver createUpvoteReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                feedSearchText = ((FeedActivity)getActivity()).getFeedSearch();
                startDate = ((FeedActivity)getActivity()).getStartD();
                endDate = ((FeedActivity)getActivity()).getEndD();
                maxFree = ((FeedActivity)getActivity()).getMaxFree();
                popularityEvents = ((FeedActivity)getActivity()).getPopularityEvents();

                if (!feedSearchText.equals("") || !maxFree.equals("") || startDate != null || endDate != null || !popularityEvents.equals("")) {
                    filteredEventsList();
                } else {
                    updateEventsList();
                }

            }
        };
    }

    private BroadcastReceiver createFavReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                feedSearchText = ((FeedActivity)getActivity()).getFeedSearch();
                startDate = ((FeedActivity)getActivity()).getStartD();
                endDate = ((FeedActivity)getActivity()).getEndD();
                maxFree = ((FeedActivity)getActivity()).getMaxFree();
                popularityEvents = ((FeedActivity)getActivity()).getPopularityEvents();

                if (!feedSearchText.equals("") || !maxFree.equals("") || startDate != null || endDate != null || !popularityEvents.equals("")) {
                    filteredEventsList();
                } else {
                    updateEventsList();
                }
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


    @Override
    public void onDestroy() {

        if (filteredEventsDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(filteredEventsDoneReceiver);
            filteredEventsDoneReceiver = null;
        }
        if (eventsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(eventsRequestDoneReceiver);
            eventsRequestDoneReceiver = null;
        }
        if (didUpvoteReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(didUpvoteReceiver);
            didUpvoteReceiver = null;
        }
        if (didIsFavReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(didIsFavReceiver);
            didIsFavReceiver = null;
        }
        if (changeCityDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(changeCityDoneReceiver);
            changeCityDoneReceiver = null;
        }
        super.onDestroy();
    }

}
