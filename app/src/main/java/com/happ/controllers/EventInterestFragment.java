package com.happ.controllers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.Interest;
import com.happ.retrofit.APIService;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class EventInterestFragment extends Fragment {

    protected ArrayList<Interest> interests;
    protected RecyclerView interestsListView;
    protected LinearLayoutManager interestsListLayoutManager;
    private BroadcastReceiver interestsRequestDoneReceiver;
    private int interestsFeedPageSize;

    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold;
    private View form=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.feeds_fragment, container, false);
        final Activity activity = getActivity();

        interestsListView = (RecyclerView)view.findViewById(R.id.interests_list_view);
        interestsListLayoutManager = new LinearLayoutManager(activity);
        interestsListView.setLayoutManager(interestsListLayoutManager);
        interests = new ArrayList<>();

        InterestsListAdapter ela = new InterestsListAdapter(activity, interests);
        interestsListView.setAdapter(ela);

        interestsRequestDoneReceiver = createInterestsRequestDoneReceiver();
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(interestsRequestDoneReceiver, new IntentFilter(BroadcastIntents.EVENTS_REQUEST_OK));
        APIService.getInterests();

//        form= getActivity().getLayoutInflater()
//                .inflate(R.layout.activity_event_interests, null);
//        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

//        return(builder.setTitle("Select Interest").setView(form)
//                .setPositiveButton(android.R.string.ok, this)
//                .setNegativeButton(android.R.string.cancel, null).create());
        return view;
    }

    private BroadcastReceiver createInterestsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateInterestsList();
            }
        };
    }

    protected void updateInterestsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Interest> eventRealmResults = realm.where(Interest.class).findAllSorted("startDate", Sort.ASCENDING);
        interests = (ArrayList<Interest>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
        ((InterestsListAdapter)interestsListView.getAdapter()).updateData(interests);
        realm.close();
    }

    @Override
    public void onDestroy() {
        if (interestsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(interestsRequestDoneReceiver);
        }
        super.onDestroy();
    }


//    @Override
//    public void onClick(DialogInterface dialog, int which) {
//
//        EditText loginBox=(EditText)form.findViewById(R.id.login);
//        String login = loginBox.getText().toString();
//
////        Interest interest;
////        ((EventCreateActivity)getActivity()).eventInterest = interest;
//
//        EditText loginText = (EditText) getActivity().findViewById(R.id.input_interest);
//        loginText.setText(login);
//    }
//    @Override
//    public void onDismiss(DialogInterface unused) {
//        super.onDismiss(unused);
//    }
//    @Override
//    public void onCancel(DialogInterface unused) {
//        super.onCancel(unused);
//    }
}