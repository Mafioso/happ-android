package com.happ.controllers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.happ.R;
import com.happ.fragments.ImageViewFragment;
import com.happ.models.EventImage;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by Torab on 20-May-16.
 */
public class EventImagesSwipeAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private RealmList<EventImage> imageList;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private ArrayList<ImageViewFragment> imageFragments;


    public EventImagesSwipeAdapter(FragmentManager fm) {
        super(fm);
        imageFragments = new ArrayList<>();
    }

    public void setImageList(RealmList<EventImage> imageList) {
        this.imageList = imageList;
        if (imageList.size() == 0) imageList.add(new EventImage());
        imageFragments.clear();
        for (int i=0; i<getCount();i++) {
            imageFragments.add(null);
        }
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Fragment getItem(int position) {
        if (imageFragments.get(position) == null) {
            ImageViewFragment fragment = ImageViewFragment.newInstance();
            fragment.setUrl(imageList.get(position).getUrl());
            imageFragments.set(position, fragment);
        }
        return imageFragments.get(position);
    }
}
