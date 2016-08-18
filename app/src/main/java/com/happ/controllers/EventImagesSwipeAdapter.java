package com.happ.controllers;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.happ.R;

/**
 * Created by Torab on 20-May-16.
 */
public class EventImagesSwipeAdapter extends PagerAdapter {
    private int [] imageResources ={R.drawable.capture1,R.drawable.capture2,R.drawable.capture3,R.drawable.capture4,R.drawable.capture5};
    private Context ctx;
    private LayoutInflater layoutInflater;

    public EventImagesSwipeAdapter(Context c) {
        ctx=c;
    }

    @Override
    public int getCount() {

        return imageResources.length;
    }



    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater= (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView=layoutInflater.inflate(R.layout.activity_custom_swipe,container,false);
        ImageView imageView=(ImageView) itemView.findViewById(R.id.swip_image_view);
        imageView.setImageResource(imageResources[position]);
        container.addView(itemView);
        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return  (view==object);
    }
}
