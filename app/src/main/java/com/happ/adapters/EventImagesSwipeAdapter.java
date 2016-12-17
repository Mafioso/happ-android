package com.happ.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.happ.fragments.ImageViewFragment;
import com.happ.models.HappImage;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by Torab on 20-May-16.
 */
public class EventImagesSwipeAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private RealmList<HappImage> imageList;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private ArrayList<ImageViewFragment> imageFragments;


    public EventImagesSwipeAdapter(FragmentManager fm) {
        super(fm);
        imageFragments = new ArrayList<>();
    }

    public void setImageList(RealmList<HappImage> imageList) {
        this.imageList = imageList;
        if (imageList.size() == 0) imageList.add(new HappImage());
        imageFragments.clear();
        for (int i=0; i<getCount();i++) {
            imageFragments.add(null);
        }
        notifyDataSetChanged();
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
