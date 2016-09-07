package com.happ.controllers;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.Interest;
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
    private LinearLayoutManager interestsListLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_interests);


        mInterestsRecyclerView = (RecyclerView) findViewById(R.id.activity_interests_rv);
        interestsListLayoutManager = new LinearLayoutManager(this);
        mInterestsRecyclerView.setLayoutManager(interestsListLayoutManager);
        interests = new ArrayList<>();

        mInterestsListAdapter = new InterestsListAdapter(this, interests);
        mInterestsRecyclerView.setAdapter(mInterestsListAdapter);

        interestsRequestDoneReceiver = createInterestsRequestDoneReceiver();
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(interestsRequestDoneReceiver, new IntentFilter(BroadcastIntents.INTERESTS_REQUEST_OK));
//        APIService.getEvents();
        HappRestClient.getInstance().getInterests();
        


//        final String[] months = {"January", "February", "March", "April",
//                "May", "June", "July", "August", "September", "October",
//                "November", "December"};
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, months);
//        final ListView listView = (ListView) findViewById(R.id.listView1);
//        listView.setAdapter(adapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                String text = (String) listView.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(), text,
//                        Toast.LENGTH_SHORT).show();
//            }
//        });


    }

    private BroadcastReceiver createInterestsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateInterestsList();
            }
        };
    }
    public interface OnInterestSelectListener {
        public void onInterestSelected(Interest interest);
    }

    protected void updateInterestsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Interest> interestsRealmResults = realm.where(Interest.class).findAll();
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
}
