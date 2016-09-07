package com.happ.controllers;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.Interest;
import com.happ.retrofit.APIService;
import com.happ.retrofit.HappRestClient;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by dante on 9/6/16.
 */
public class SelectInterestsActivity extends AppCompatActivity {

    protected RecyclerView mInterestsRecyclerView;
    private ArrayList<Interest> interests;
    private InterestsListAdapter mInterestsListAdapter;
    private BroadcastReceiver interestsRequestDoneReceiver;
    private BroadcastReceiver setInterestsOKReceiver;
    private LinearLayoutManager interestsListLayoutManager;
    private FloatingActionButton mFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_interests);

        setTitle(getResources().getString(R.string.select_interest_title));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedInterests = mInterestsListAdapter.getSelectedInterests();
                // SEND DATA TO SERVER
                HappRestClient.getInstance().setInterests(selectedInterests);
            }
        });


        mInterestsRecyclerView = (RecyclerView) findViewById(R.id.activity_interests_rv);
        interestsListLayoutManager = new LinearLayoutManager(this);
        mInterestsRecyclerView.setLayoutManager(interestsListLayoutManager);

        Realm realm = Realm.getDefaultInstance();
        interests = new ArrayList<>();

        try {
            RealmResults<Interest> interestsResults = realm.where(Interest.class).isNull("parentId").findAll();
            interests = (ArrayList<Interest>) realm.copyFromRealm(interestsResults);
        } catch (Exception ex) {
            Log.e("HAPP", "SelectInterestActivity > onCreate "+ex.getLocalizedMessage());
        } finally {
            realm.close();
        }

        mInterestsListAdapter = new InterestsListAdapter(this, interests);
        mInterestsRecyclerView.setAdapter(mInterestsListAdapter);
        mFab.setVisibility(View.VISIBLE);

        if (interestsRequestDoneReceiver == null) {
            interestsRequestDoneReceiver = createInterestsRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(interestsRequestDoneReceiver, new IntentFilter(BroadcastIntents.INTERESTS_REQUEST_OK));
        }
        if (setInterestsOKReceiver == null) {
            setInterestsOKReceiver = createSetInterestsOKReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(setInterestsOKReceiver, new IntentFilter(BroadcastIntents.SET_INTERESTS_OK));
        }
//        APIService.getInterests();
        HappRestClient.getInstance().getInterests();

    }

    private BroadcastReceiver createInterestsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateInterestsList();
            }
        };
    }

    private BroadcastReceiver createSetInterestsOKReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent feedIntent = new Intent(SelectInterestsActivity.this, FeedActivity.class);
                feedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SelectInterestsActivity.this.startActivity(feedIntent);
                SelectInterestsActivity.this.overridePendingTransition(0,0);
            }
        };
    }


    public interface OnInterestSelectListener {
        public void onInterestSelected(Interest interest);
    }

    protected void updateInterestsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Interest> interestsRealmResults = realm.where(Interest.class).isNull("parentId").findAll();
        interests = (ArrayList<Interest>)realm.copyFromRealm(interestsRealmResults);
        mInterestsListAdapter.updateData(interests);
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.listsearch).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

//                interests.getFilter().filter(newText);

                return false;
            }
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (interestsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(interestsRequestDoneReceiver);
            interestsRequestDoneReceiver = null;
        }
        if (setInterestsOKReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(setInterestsOKReceiver);
            setInterestsOKReceiver = null;
        }
    }
}
