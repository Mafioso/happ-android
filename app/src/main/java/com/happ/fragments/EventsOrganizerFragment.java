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
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.OrgEventsListAdapter;
import com.happ.controllers.EditCreateActivity;
import com.happ.controllers.RejectionReasonsActivity;
import com.happ.controllers_drawer.EventActivity;
import com.happ.controllers_drawer.OrganizerModeActivity;
import com.happ.models.Event;
import com.happ.models.EventStatus;
import com.happ.retrofit.APIService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
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

    protected RelativeLayout mRLEmptyFrom;
    protected Button mBtnEmptyForm;
    protected AppCompatImageView mIVEmpty;
    protected TextView mPersonalSubText;

    private BroadcastReceiver eventsRequestDoneReceiver;
    private BroadcastReceiver filteredEventsRequestDoneReceiver;
    private BroadcastReceiver deleteEventRequestDoneReceiver;
    private BroadcastReceiver didUpvoteReceiver;
    private BroadcastReceiver didIsFavReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.org_events_fragment, container, false);
        final Activity activity = getActivity();

        orgEventsRecyclerView = (RecyclerView) view.findViewById(R.id.org_events_list_view);
        mRLEmptyFrom = (RelativeLayout) view.findViewById(R.id.rl_empty_form);
        mBtnEmptyForm = (Button) view.findViewById(R.id.btn_empty_form);
        mPersonalSubText = (TextView) view.findViewById(R.id.tv_empty_personal_text);
        mIVEmpty = (AppCompatImageView) view.findViewById(R.id.iv_logo_empty);

        mRLEmptyFrom.setVisibility(View.GONE);

        eventsFeedPageSize = 10;
        visibleThreshold = 5;
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

            @Override
            public void onEventRejectionReasonsActivity(String eventId) {
                Intent intent = new Intent(activity, RejectionReasonsActivity.class);
                intent.putExtra("event_id", eventId);
                startActivity(intent);
            }
        });
        orgEventsRecyclerView.setAdapter(oela);

        if (eventsRequestDoneReceiver == null) {
            eventsRequestDoneReceiver = createEventsRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(eventsRequestDoneReceiver, new IntentFilter(BroadcastIntents.ORG_EVENTS_REQUEST_OK));
        }

        if (filteredEventsRequestDoneReceiver == null) {
            filteredEventsRequestDoneReceiver = filteredEventsRequestReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(filteredEventsRequestDoneReceiver, new IntentFilter(BroadcastIntents.FILTERED_ORG_EVENTS_REQUEST_OK));
        }

        if (deleteEventRequestDoneReceiver == null) {
            deleteEventRequestDoneReceiver = deleteEventRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(deleteEventRequestDoneReceiver, new IntentFilter(BroadcastIntents.EVENTDELETE_REQUEST_OK));
        }

        if (didUpvoteReceiver == null) {
            didUpvoteReceiver = createUpvoteReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(didUpvoteReceiver, new IntentFilter(BroadcastIntents.EVENT_UPVOTE_REQUEST_OK));
        }

        if (didIsFavReceiver == null) {
            didIsFavReceiver = createFavReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(didIsFavReceiver, new IntentFilter(BroadcastIntents.EVENT_UNFAV_REQUEST_OK));
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

        isEmpty();
    }

    protected void updateFilteredEventsList() {

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        today = cal.getTime();

        ArrayList<Integer> selectedStatuses = new ArrayList<>();
        if (((OrganizerModeActivity)getActivity()).getFilterIsActive()) selectedStatuses.add(EventStatus.ACTIVE);
        if (((OrganizerModeActivity)getActivity()).getFilterIsOnreview()) selectedStatuses.add(EventStatus.ON_REVIEW);
        if (((OrganizerModeActivity)getActivity()).getFilterIsRejected()) selectedStatuses.add(EventStatus.REJECTED);
        if (((OrganizerModeActivity)getActivity()).getFilterIsFinished()) selectedStatuses.add(EventStatus.FINISHED);

        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Event> q = realm.where(Event.class).beginGroup();
        q.equalTo("localOnly", false);
        q.equalTo("author.id", App.getCurrentUser().getId());
        q.beginGroup();
        for (int i=0; i<selectedStatuses.size(); i++) {
            int status = selectedStatuses.get(i);
            if (i > 0) q.or();
            if (status == EventStatus.FINISHED) {
                q.beginGroup().equalTo("status", EventStatus.ACTIVE).lessThan("endDate", today).endGroup();
            } else if (status == EventStatus.ACTIVE){
                q.beginGroup().equalTo("status", EventStatus.ACTIVE).greaterThanOrEqualTo("endDate", today).endGroup();
            } else {
                q.equalTo("status", status);
            }
        }
        if (selectedStatuses.size() == 0) {
            q.equalTo("status", Integer.MAX_VALUE);
        }
        q.endGroup();

        RealmResults<Event> eventRealmResults = q.endGroup().findAll();

        orgEvents = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults);
        ((OrgEventsListAdapter) orgEventsRecyclerView.getAdapter()).updateData(orgEvents);
        realm.close();

        isEmpty();
    }

    private BroadcastReceiver createEventsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateEventsList();

            }
        };
    }

    private BroadcastReceiver filteredEventsRequestReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateFilteredEventsList();
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


    private BroadcastReceiver createUpvoteReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateFilteredEventsList();
            }
        };
    }

    private BroadcastReceiver createFavReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateFilteredEventsList();
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
//        APIService.getOrgEvents(page);

        boolean isActive = ((OrganizerModeActivity)getActivity()).getFilterIsActive();
        boolean isInactive = ((OrganizerModeActivity)getActivity()).getFilterIsInactive();
        boolean isOnreview = ((OrganizerModeActivity)getActivity()).getFilterIsOnreview();
        boolean isRejected = ((OrganizerModeActivity)getActivity()).getFilterIsRejected();
        boolean isFinished = ((OrganizerModeActivity)getActivity()).getFilterIsFinished();

        APIService.getFilteredOrgEvents(page, isActive, isInactive, isOnreview, isRejected,isFinished);
    }

    @Override
    public void onDestroy() {
        if (eventsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(eventsRequestDoneReceiver);
            eventsRequestDoneReceiver = null;
        }

        if (deleteEventRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(deleteEventRequestDoneReceiver);
            deleteEventRequestDoneReceiver = null;
        }

        if (filteredEventsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(filteredEventsRequestDoneReceiver);
            filteredEventsRequestDoneReceiver = null;
        }

        if (didUpvoteReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(didUpvoteReceiver);
            didUpvoteReceiver = null;
        }
        if (didIsFavReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(didIsFavReceiver);
            didIsFavReceiver = null;
        }

        super.onDestroy();
    }

    private void isEmpty() {
        if (orgEvents.isEmpty()) {

            mRLEmptyFrom.setVisibility(View.VISIBLE);
            mIVEmpty.setImageResource(R.drawable.fav_empty);

            mBtnEmptyForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.animatealpha);
                    mBtnEmptyForm.startAnimation(anim);

                    Intent intent = new Intent(App.getContext(), EditCreateActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            });
        } else {
            mRLEmptyFrom.setVisibility(View.GONE);
        }

    }

}
