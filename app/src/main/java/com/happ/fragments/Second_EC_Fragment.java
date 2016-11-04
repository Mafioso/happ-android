package com.happ.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happ.R;

/**
 * Created by dante on 10/26/16.
 */
public class Second_EC_Fragment extends Fragment {

    public static Second_EC_Fragment newInstance() {

        return new Second_EC_Fragment();
    }

    public Second_EC_Fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_ec_second, container, false);
        final Activity activity = getActivity();

        return view;
    }
}
