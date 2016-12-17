package com.happ.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.InterestsListAdapter;
import com.happ.models.Interest;
import com.happ.retrofit.APIService;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.TouchScrollBar;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class EventInterestFragment extends DialogFragment {
    protected RecyclerView mInterestsRecyclerView;
    private ArrayList<Interest> interests;
    private InterestsListAdapter mInterestsListAdapter;
    private TouchScrollBar mInterestsScrollBar;
    private OnInterestSelectListener listener;
    private BroadcastReceiver interestsRequestDoneReceiver;
    private LinearLayoutManager interestsListLayoutManager;

    public EventInterestFragment() {

    }

    public static EventInterestFragment newInstance() {
        EventInterestFragment fragment = new EventInterestFragment();
        return fragment;
    }

    public void setOnInterestSelectListener(OnInterestSelectListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.interest_list_view, null);
        final Activity activity = getActivity();


        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.select_interest_title)
                .setView(contentView)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();


        mInterestsRecyclerView = (RecyclerView)contentView.findViewById(R.id.interests_recycler_view);
        interestsListLayoutManager = new LinearLayoutManager(activity);
        mInterestsRecyclerView.setLayoutManager(interestsListLayoutManager);

        interests = new ArrayList<>();

        mInterestsListAdapter = new InterestsListAdapter(getContext(), interests, true);
        mInterestsListAdapter.setSelectSingle(true);
        mInterestsListAdapter.setOnItemSelectedListener(new InterestsListAdapter.OnInterestClickedListener() {
            @Override
            public void onInterestSelected(Interest interest) {
                if (listener != null) {
                    listener.onInterestSelected(interest);
                }
                dialog.dismiss();
            }
        });

        mInterestsRecyclerView.setAdapter(mInterestsListAdapter);
        updateInterestsList();

        interestsRequestDoneReceiver = createInterestsRequestDoneReceiver();
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(interestsRequestDoneReceiver, new IntentFilter(BroadcastIntents.INTERESTS_REQUEST_OK));
        APIService.getInterests();

//        HappRestClient.getInstance().getInterests();


        mInterestsScrollBar = (TouchScrollBar)contentView.findViewById(R.id.interests_scroll_bar);
        mInterestsScrollBar.setHandleColourRes(R.color.colorAccent);
        mInterestsScrollBar.addIndicator(new AlphabetIndicator(getContext()),true);

        return dialog;
    }

    public interface OnInterestSelectListener {
        void onInterestSelected(Interest interest);
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
        RealmResults<Interest> interestsRealmResults = realm.where(Interest.class).isNull("parentId").findAll();
        interests = (ArrayList<Interest>)realm.copyFromRealm(interestsRealmResults);
        mInterestsListAdapter.updateData(interests);
        realm.close();
    }

}