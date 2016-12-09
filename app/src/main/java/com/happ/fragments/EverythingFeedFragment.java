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

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.EventsListAdapter;
import com.happ.controllers_drawer.FeedActivity;
import com.happ.controllers_drawer.SelectInterestsActivity;
import com.happ.models.Event;
import com.happ.retrofit.APIService;
import com.happ.retrofit.HappRestClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    private String feedSearchText;
    private Date startDate;
    private Date endDate;
    private String maxFree;

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

        if (!feedSearchText.equals("") || !maxFree.equals("") || startDate != null || endDate != null) {
            filteredEventsList();
        } else {
            updateEventsList();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (App.hasConnection(getContext())) HappRestClient.getInstance().getEvents(false);
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

        return view;
    }

    protected void updateEventsList() {

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventRealmResults = realm.where(Event.class).equalTo("localOnly", false).findAllSorted("startDate", Sort.ASCENDING);
        events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
        ((EventsListAdapter)eventsListView.getAdapter()).updateData(events);
        realm.close();

        iSEmptyForm();
    }

    protected void filteredEventsList() {
        feedSearchText = ((FeedActivity)getActivity()).getFeedSearch();
        startDate = ((FeedActivity)getActivity()).getStartD();
        endDate = ((FeedActivity)getActivity()).getEndD();
        maxFree = ((FeedActivity)getActivity()).getMaxFree();

        Realm realm = Realm.getDefaultInstance();
        RealmQuery q = realm.where(Event.class).equalTo("localOnly", false).beginGroup();
        if (feedSearchText != null && feedSearchText.length() > 0) q.contains("title", feedSearchText);
        if (startDate != null) q.greaterThanOrEqualTo("startDate", startDate);
        if (endDate != null) q.lessThanOrEqualTo("startDate", endDate);
        if (maxFree != null && maxFree.length() > 0) q.equalTo("lowestPrice", 0);
        RealmResults<Event> eventRealmResults = q.endGroup().findAllSorted("startDate", Sort.ASCENDING);

        events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
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

            String sD = "";
            String eD = "";
            if (startDate != null) sD = sdf.format(startDate);
            if (endDate != null) eD = sdf.format(endDate);

            if (feedSearchText.equals("") || startDate != null || endDate != null || !maxFree.equals("")) {

                APIService.getFilteredEvents(page, feedSearchText, sD, eD, maxFree, false);
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
        }

        mFeedEventsProgress.setVisibility(View.GONE);
        mDarkViewProgress.setVisibility(View.GONE);
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
                    getEvents(0, false);
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

                if (!feedSearchText.equals("") || !maxFree.equals("") || startDate != null || endDate != null) {
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

                if (!feedSearchText.equals("") || !maxFree.equals("") || startDate != null || endDate != null) {
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
        }
        if (eventsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(eventsRequestDoneReceiver);
        }
        if (didUpvoteReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(didUpvoteReceiver);
        }
        if (didIsFavReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(didIsFavReceiver);
        }
        super.onDestroy();
    }

}
