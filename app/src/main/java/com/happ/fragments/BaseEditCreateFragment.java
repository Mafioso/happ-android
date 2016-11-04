package com.happ.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by dante on 10/26/16.
 */
public class BaseEditCreateFragment extends Fragment {

    public static BaseEditCreateFragment newInstance() {

        return new BaseEditCreateFragment();
    }

    public BaseEditCreateFragment() {

    }

    private EditText mStartDate,
                    mEndDate,
                    mWebSite,
                    mTicketsLink,
                    mEmail,
                    mPhone,
                    mDescription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }
}
