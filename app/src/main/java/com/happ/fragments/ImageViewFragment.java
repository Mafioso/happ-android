package com.happ.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.happ.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by iztiev on 8/22/16.
 */
public class ImageViewFragment extends Fragment {
    public ImageView mImageView;
    private ProgressBar mProgressBar;
    private String imageUrl;

    public static ImageViewFragment newInstance() {
        return new ImageViewFragment();
    }

    public ImageViewFragment() {
    }

    public void setUrl(String url) {
        imageUrl = url;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.images_pager, container, false);
        mImageView = (ImageView) view.findViewById(R.id.images_pager_image);
        mImageView.setVisibility(View.INVISIBLE);

        mProgressBar = (ProgressBar) view.findViewById(R.id.images_pager_preloader);
        mProgressBar.setVisibility(View.VISIBLE);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ViewTreeObserver viewTreeObserver = mImageView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if (imageUrl != null) {
                        int viewWidth = mImageView.getWidth();
                        int viewHeight = mImageView.getHeight();
                        mImageView.setVisibility(View.GONE);

                        Log.d("EventImagesSwipeAdapter", "Setting Image");

                        Picasso.with(getContext())
                                .load(imageUrl)
                                .fit()
                                .centerCrop()
                                .into(mImageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            mImageView.setVisibility(View.VISIBLE);
                                            mProgressBar.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onError() {
                                        }
                        });
                    } else {
                        mImageView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}
