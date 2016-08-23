package com.happ.fragments;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.happ.R;

/**
 * Created by iztiev on 8/22/16.
 */
public class ImageViewFragment extends Fragment {
    private ImageView mImageView;
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
                    int viewWidth = mImageView.getWidth();
                    int viewHeight = mImageView.getHeight();
                    mImageView.setVisibility(View.GONE);

                    Log.d("EventImagesSwipeAdapter", "Setting Image");

                    Glide.with(getContext())
                            .load(imageUrl)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    mImageView.setVisibility(View.VISIBLE);
                                    mProgressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .override(viewWidth, viewHeight)
                            .centerCrop()
                            .into(mImageView);
                }
            });
        }
    }
}
