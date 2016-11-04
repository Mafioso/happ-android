package com.happ.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.happ.App;
import com.happ.R;

import java.util.ArrayList;

/**
 * Created by dante on 10/27/16.
 */
public class EcImagesAdapter extends RecyclerView.Adapter<EcImagesAdapter.EcImagesViewHolder> {

    private ArrayList<String> mImagesItems;
    private final Context contex;

//    private String[] urls = {
//            "http://www.freedigitalphotos.net/images/img/homepage/87357.jpg",
//            "http://assets.barcroftmedia.com.s3-website-eu-west-1.amazonaws.com/assets/images/recent-images-11.jpg",
//            "http://7606-presscdn-0-74.pagely.netdna-cdn.com/wp-content/uploads/2016/03/Dubai-Photos-Images-Oicture-Dubai-Landmarks-800x600.jpg",
//            "http://www.gettyimages.ca/gi-resources/images/Homepage/Hero/UK/CMS_Creative_164657191_Kingfisher.jpg",
//            "http://www.w3schools.com/css/trolltunga.jpg",
//            "http://i164.photobucket.com/albums/u8/hemi1hemi/COLOR/COL9-6.jpg",
//            "http://www.planwallpaper.com/static/images/desktop-year-of-the-tiger-images-wallpaper.jpg",
//            "http://www.gettyimages.pt/gi-resources/images/Homepage/Hero/PT/PT_hero_42_153645159.jpg",
//            "http://www.planwallpaper.com/static/images/beautiful-sunset-images-196063.jpg",
//            "http://www.w3schools.com/css/img_fjords.jpg"
//    };

    public EcImagesAdapter(Context context, ArrayList<String> mImagesItems) {
        this.contex = context;
        this.mImagesItems = mImagesItems;
    }

    @Override
    public EcImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ec_image_item, parent, false);
        return new EcImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EcImagesViewHolder holder, int position) {
        final String eventImage = mImagesItems.get(position);

        final EcImagesViewHolder itemHolder = (EcImagesViewHolder)holder;

        Glide
                .with(App.getContext())
                .load(eventImage)
                .centerCrop()
                .crossFade()
                .into(itemHolder.mImageView);

    }

    @Override
    public int getItemCount() {
        return mImagesItems.size();
    }

    public class EcImagesViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout mRLImage;
        public ImageView mImageView;

        public EcImagesViewHolder(View itemView) {
            super(itemView);
            mRLImage = (RelativeLayout) itemView.findViewById(R.id.rl_ec_image);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_ec);
        }
    }

}
