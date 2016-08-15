package com.happ.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happ.R;
import com.happ.controllers.EventsListAdapter;
import com.happ.models.Event;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by dante on 8/9/16.
 */
public class FavoriteFeedFragment extends BaseFeedFragment {

    public static FavoriteFeedFragment newInstance() {
        return new FavoriteFeedFragment();
    }

    public FavoriteFeedFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    @Override
    public void onStart() {
//        updateEventsList();
        super.onStart();
    }

    @Override
    public void onResume() {
        updateEventsList();
        super.onResume();
    }



    protected void updateEventsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> eventRealmResults = realm.where(Event.class).equalTo("inFavorites", true).findAllSorted("startDate", Sort.ASCENDING);
        events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
        ((EventsListAdapter)eventsListView.getAdapter()).updateData(events);
        realm.close();
    }

    @Override
    protected void getEvents(int page) {
        super.getEvents(page);
    }
}
