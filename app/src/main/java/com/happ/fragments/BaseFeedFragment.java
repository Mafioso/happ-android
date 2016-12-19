package com.happ.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happ.App;
import com.happ.R;
import com.happ.adapters.EventsListAdapter;
import com.happ.controllers_drawer.EventActivity;
import com.happ.controllers_drawer.FeedActivity;
import com.happ.models.Event;
import com.happ.retrofit.APIService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

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

    protected RelativeLayout mRLEmptyFrom;
    protected Button mBtnEmptyForm;
    protected View mDarkViewProgress;
    protected AppCompatImageView mIVEmpty;
    protected MaterialProgressBar mFeedEventsProgress;
    protected TextView mPersonalSubText;
    protected String userId = App.getCurrentUser().getId();

    protected ChangeColorIconToolbarListener mChangeColorIconToolbarListener;

    public static BaseFeedFragment newInstance() {
        return new BaseFeedFragment();
    }

    public BaseFeedFragment() {
    }

    public interface ChangeColorIconToolbarListener {
        void onChangeColorIconToolbar(@DrawableRes int drawableHome, @DrawableRes int drawableFilter);
        void onClickButtonEmpty();
    }

    public void setChangeColorIconToolbarListener(ChangeColorIconToolbarListener listener) {
        mChangeColorIconToolbarListener = listener;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        eventsFeedPageSize = Integer.parseInt(this.getString(R.string.event_feeds_page_size));
//        visibleThreshold = Integer.parseInt(this.getString(R.string.event_feeds_visible_treshold_for_loading_next_items));
        visibleThreshold = 2;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.feeds_fragment, container, false);
        final Activity activity = getActivity();

        final Display display = activity.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();

        mRLEmptyFrom = (RelativeLayout) view.findViewById(R.id.rl_empty_form);
        eventsListView = (RecyclerView) view.findViewById(R.id.events_list_view);
        mBtnEmptyForm = (Button) view.findViewById(R.id.btn_empty_form);
        mDarkViewProgress = (View) view.findViewById(R.id.dark_view_feed_progress);
        mFeedEventsProgress = (MaterialProgressBar) view.findViewById(R.id.feed_events_progress);
        mPersonalSubText = (TextView) view.findViewById(R.id.tv_empty_personal_text);
        mIVEmpty = (AppCompatImageView) view.findViewById(R.id.iv_logo_empty);
        mRLEmptyFrom.setVisibility(View.GONE);


        eventsListLayoutManager = new LinearLayoutManager(activity);
        eventsListView.setLayoutManager(eventsListLayoutManager);
        events = new ArrayList<>();
        mEventAdapter = new EventsListAdapter(activity, events);

        mEventAdapter.setOnSelectItemListener(new EventsListAdapter.SelectEventItemListener() {
            @Override
            public void onEventItemSelected(String eventId, ActivityOptionsCompat options, int position) {
                Intent intent = new Intent(activity, EventActivity.class);
                intent.putExtra("event_id", eventId);
                intent.putExtra("in_event_activity", true);
                intent.putExtra("position", position);

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

    protected void clearRealmForOldCity(){
        Realm realm = Realm.getDefaultInstance();
//        final RealmResults<Event> results = realm.where(Event.class).notEqualTo("inFavorites", true).findAll();
        final RealmResults<Event> results = realm.where(Event.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
        realm.close();
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
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

                        String feedSearchText = ((FeedActivity)getActivity()).getFeedSearch();
                        String maxFree = ((FeedActivity)getActivity()).getMaxFree();
                        Date startDate = ((FeedActivity)getActivity()).getStartD();
                        Date endDate = ((FeedActivity)getActivity()).getEndD();
                        boolean popularityEvents = ((FeedActivity)getActivity()).getPopularityEvents();

                        String sD = "";
                        String eD = "";

                        if (startDate != null) sD = sdf.format(startDate);
                        if (endDate != null) eD = sdf.format(endDate);

                        if (startDate == null || endDate == null || maxFree.equals("") || feedSearchText.equals("") || !popularityEvents) {
                            getEvents(nextPage, false);
                            Log.e("BASE_FEED_FRAGMENT", "Simple scroll");
                        } else {
                            APIService.getFilteredEvents(nextPage, feedSearchText, sD, eD, maxFree,popularityEvents, false);
                            Log.e("BASE_FEED_FRAGMENT", "Filter scroll");
                        }

                    }

                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    protected void getEvents(int page, boolean favs) {

    }
}