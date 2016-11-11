package com.happ.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happ.R;
import com.happ.models.Event;

import io.realm.Realm;

/**
 * Created by dante on 10/26/16.
 */
public class Third_EC_Fragment extends Fragment {

    public static Third_EC_Fragment newInstance() {

        return new Third_EC_Fragment();
    }

    public Third_EC_Fragment() {

    }

    private Event event;
    private String eventId;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final Intent intent = getActivity().getIntent();
        eventId = intent.getStringExtra("event_id");
        if (eventId != null) {
            Realm realm = Realm.getDefaultInstance();
            event = realm.where(Event.class).equalTo("id", eventId).findFirst();
            event = realm.copyFromRealm(event);
            realm.close();
        } else {
            event = new Event();
        }

        final View view = inflater.inflate(R.layout.fragment_ec_third, container, false);
        final Activity activity = getActivity();


        if (eventId != null) {
            //set text
        } else {

        }


        return view;
    }
}
