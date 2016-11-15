package com.happ.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.happ.App;
import com.happ.R;
import com.happ.Typefaces;
import com.happ.controllers_drawer.OrganizerModeActivity;
import com.happ.models.Event;
import com.happ.retrofit.APIService;

import java.util.ArrayList;

/**
 * Created by iztiev on 8/4/16.
 */
public class OrgEventsListAdapter extends RecyclerView.Adapter<OrgEventsListAdapter.OrgEventsListViewHolder> {
    private ArrayList<Event> mItems;
    private final Context context;
    private SelectEventItemListener mSelectItemListener;


    private String[] urls = {
            "http://www.freedigitalphotos.net/images/img/homepage/87357.jpg",
            "http://assets.barcroftmedia.com.s3-website-eu-west-1.amazonaws.com/assets/images/recent-images-11.jpg",
            "http://7606-presscdn-0-74.pagely.netdna-cdn.com/wp-content/uploads/2016/03/Dubai-Photos-Images-Oicture-Dubai-Landmarks-800x600.jpg",
            "http://www.gettyimages.ca/gi-resources/images/Homepage/Hero/UK/CMS_Creative_164657191_Kingfisher.jpg",
            "http://www.w3schools.com/css/trolltunga.jpg",
            "http://i164.photobucket.com/albums/u8/hemi1hemi/COLOR/COL9-6.jpg",
            "http://www.planwallpaper.com/static/images/desktop-year-of-the-tiger-images-wallpaper.jpg",
            "http://www.gettyimages.pt/gi-resources/images/Homepage/Hero/PT/PT_hero_42_153645159.jpg",
            "http://www.planwallpaper.com/static/images/beautiful-sunset-images-196063.jpg",
            "http://www.w3schools.com/css/img_fjords.jpg"
    };

    public interface SelectEventItemListener {
        void onEventItemSelected(String eventId, ActivityOptionsCompat options);
        void onEventEditSelected(String eventId);
    }

    public void setOnSelectItemListener(SelectEventItemListener listener) {
        mSelectItemListener = listener;
    }

    public OrgEventsListAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
    }


    public void updateData(ArrayList<Event> events) {
        mItems = events;
        Log.d("AAAAA", String.valueOf(events.size()));
        this.notifyDataSetChanged();
    }
    

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    

    @Override
    public OrgEventsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.org_event_list_item, parent, false);
            return new OrgEventsListItemViewHolder(view);
    }

    public void eventSelected(OrgEventsListItemViewHolder itemViewHolder, Event item) {
        String id = item.getId();
        ActivityOptionsCompat optionsCompat = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Pair<View, String> p1 = Pair.create((View) itemViewHolder.mTitleView, "event_title");
//            Pair<View, String> p2 = Pair.create((View) itemViewHolder.mInterestViewColor, "event_interest_bg");
            Pair<View, String> p3 = Pair.create((View) itemViewHolder.mImageView, "ivent_image");
            Pair<View, String> p4 = Pair.create((View) itemViewHolder.mInterestTitle, "event_interest_name");
            optionsCompat = ActivityOptionsCompat.
//                    makeSceneTransitionAnimation((Activity) context, p1, p2, p3, p4);
        makeSceneTransitionAnimation((Activity) context, p1, p3, p4);
        }
        if (mSelectItemListener != null) {
            mSelectItemListener.onEventItemSelected(id, optionsCompat);
        }
    }

    @Override
    public void onBindViewHolder(final OrgEventsListViewHolder holder, int position) {
        final Event event = mItems.get(position);
        holder.itemView.setOnClickListener(null);
        final OrgEventsListItemViewHolder itemHolder = (OrgEventsListItemViewHolder)holder;


        RelativeLayout.LayoutParams bgParams = (RelativeLayout.LayoutParams) itemHolder.mBgItem.getLayoutParams();
        RelativeLayout.LayoutParams bgParamsImageItem = (RelativeLayout.LayoutParams) itemHolder.mBgImageViewColor.getLayoutParams();
        itemHolder.mBgItem.setLayoutParams(bgParams);
        itemHolder.mBgImageViewColor.setLayoutParams(bgParamsImageItem);

        itemHolder.mFavoritesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event.isInFavorites()) {
                    APIService.doUnFav(event.getId());
                } else {
                    APIService.doFav(event.getId());
                }
            }
        });
        itemHolder.mUpvoteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event.isDidVote()) {
                    APIService.doDownVote(event.getId());
                } else {
                    APIService.doUpVote(event.getId());
                }
            }
        });


        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventSelected(itemHolder, event);
            }
        });

        final String url = urls[position%10];
        if(itemHolder.url == null || !itemHolder.url.equals(url)){
            itemHolder.url = url;
//            final String url = event.getImages().get(0).getUrl();
//                final String url = "http://lorempixel.com/g/1080/610/nature/" + position + "/";
            Glide.clear(itemHolder.mImageView);
            itemHolder.mImagePreloader.setVisibility(View.VISIBLE);
            Glide.clear(itemHolder.mImageView);
            try {
                ViewTreeObserver viewTreeObserver = itemHolder.mImageView.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            itemHolder.mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewWidth = itemHolder.mImageView.getWidth();
                            int viewHeight = itemHolder.mImageView.getHeight();
                            Log.d("HEIGHT_WIDTH", String.valueOf(viewWidth) + " " + String.valueOf(viewHeight));

                            Glide.with(App.getContext())
                                    .load(url)
                                    .listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            Log.e("GLIDE_ERR", url + " " + e.getMessage());
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            Log.d("GLIDE_OK", url);
                                            Bitmap bm = ((GlideBitmapDrawable)resource.getCurrent()).getBitmap();
                                            Palette p = Palette.from(bm).generate();
                                            itemHolder.mBgItem.setBackgroundColor(p.getMutedSwatch().getRgb());
                                            itemHolder.mBgImageViewColor.setBackgroundColor(p.getMutedSwatch().getRgb());
                                            itemHolder.mImagePreloader.setVisibility(View.INVISIBLE);
                                            return false;
                                        }
                                    })
                                    .override(viewWidth, viewHeight)
                                    .centerCrop()
                                    .into(itemHolder.mImageView);
                        }
                    });
                }
            } catch (Exception ex) {
//                ViewTreeObserver viewTreeObserver = itemHolder.mImageView.getViewTreeObserver();
//                if (viewTreeObserver.isAlive()) {
//                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                        @Override
//                        public void onGlobalLayout() {
//                            itemHolder.mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                            int viewWidth = itemHolder.mImageView.getWidth();
//                            int viewHeight = itemHolder.mImageView.getHeight();
//                            Log.d("HEIGHT_WIDTH", String.valueOf(viewWidth)+" "+String.valueOf(viewHeight));
//
//                            Glide.with(App.getContext())
//                                    .load(url)
//                                    .listener(new RequestListener<String, GlideDrawable>() {
//                                        @Override
//                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                            return false;
//                                        }
//
//                                        @Override
//                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                            itemHolder.mImagePreloader.setVisibility(View.INVISIBLE);
//                                            return false;
//                                        }
//                                    })
//                                    .override(viewWidth, viewHeight)
//                                    .centerCrop()
//                                    .into(itemHolder.mImageView);
//                        }
//                    });
//                }
                Log.e("ORG", ex.getLocalizedMessage());
            }
//        } else{
//            Glide.clear(itemHolder.mImageView);
//            itemHolder.mImageView.setImageDrawable(null);
//            itemHolder.mImagePreloader.setVisibility(View.INVISIBLE);
        }

        itemHolder.mTitleView.setText(event.getTitle());
        Typeface tfcs = Typefaces.get(App.getContext(), "fonts/WienLight_Normal.ttf");
        itemHolder.mTitleView.setTypeface(tfcs);

        itemHolder.mPrice.setText(event.getPriceRange());
        itemHolder.mPlace.setText(event.getPlace());
        itemHolder.mVotesCount.setText(String.valueOf(event.getVotesCount()));
//            itemHolder.mViewsCount.setText(String.valueOf(event.getViewsCount()));

        if ( event.getInterest() != null) {
            ArrayList<String> fullTitle = event.getInterest().getFullTitle();

//                if (event.getInterest().getColor() != null) {
//                    String colorString =  event.getInterest().getColor();
//                    if (colorString.length() > 6) {
//                        colorString = colorString.substring(colorString.length()-6);
//                    }
//                    String action_navigation_item_text_color = "#" + colorString;
//
//                    itemHolder.mInterestViewColor.setBackgroundColor(Color.parseColor(action_navigation_item_text_color));
//                } else {
//                    itemHolder.mInterestViewColor.setBackgroundColor(Color.parseColor("#FF1493"));
//                }

            String fullTitleString = fullTitle.get(0);
            if (fullTitle.size() > 1) {
                fullTitleString = fullTitleString + " / " + fullTitle.get(1);
            }
            itemHolder.mInterestTitle.setText(fullTitleString);

        } else {
            itemHolder.mInterestTitle.setText("Null");
        }

        if (event.isDidVote()) {
            itemHolder.mUpvoteImage.setImageResource(R.drawable.ic_did_upvote);
        } else {
            itemHolder.mUpvoteImage.setImageResource(R.drawable.ic_did_not_upvote);
        }

        if (event.isInFavorites()) {
            itemHolder.mFavoritesImage.setImageResource(R.drawable.ic_in_favorites);
        } else {
            itemHolder.mFavoritesImage.setImageResource(R.drawable.ic_not_in_favorites);
        }

//            itemHolder.mDateView.setText(event.getStartDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));


        Menu menu = itemHolder.mToolbar.getMenu();
        if (menu != null) menu.clear();
        itemHolder.mToolbar.setVisibility(View.VISIBLE);
        itemHolder.mToolbar.inflateMenu(R.menu.menu_event_org);
        itemHolder.mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                menuItem.setChecked(false);
                if (menuItem.getItemId() == R.id.menu_edit) {
                    mSelectItemListener.onEventEditSelected(event.getId());
                }
                if (menuItem.getItemId() == R.id.menu_delete) {
                    ((OrganizerModeActivity) context).functionToRun(event.getId());
                }
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class OrgEventsListViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout mRLView;
        public TextView mTitleView;

        public OrgEventsListViewHolder(View itemView) {
            super(itemView);
            mRLView = (RelativeLayout) itemView.findViewById(R.id.rlView);
        }
    }

    public class OrgEventsListItemViewHolder extends OrgEventsListViewHolder {
        public TextView mDateView;
        public LinearLayout mInterestViewColor;
        public TextView mInterestTitle;
        public ImageView mImageView;
        public TextView mPrice;
        public TextView mVotesCount;
        public TextView mViewsCount;
        public Event event;
        public ImageView mFavoritesImage;
        public ImageView mUpvoteImage, mDownVoteImage;
        public ProgressBar mImagePreloader;
        public Toolbar mToolbar;
        public RelativeLayout mBgItem;
        private TextView mPlace;
        private String url;
        private RelativeLayout mBgImageViewColor;


        public OrgEventsListItemViewHolder(final View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(R.id.events_list_item_title);
//            mDateView = (TextView) itemView.findViewById(R.id.events_list_item_start_date);
//            mInterestViewColor = (LinearLayout) itemView.findViewById(R.id.events_list_interest_color);
            mInterestTitle = (TextView) itemView.findViewById(R.id.events_list_item_interest);
            mImageView = (ImageView) itemView.findViewById(R.id.events_list_image_view);
            mPrice = (TextView) itemView.findViewById(R.id.events_list_price);
            mVotesCount = (TextView) itemView.findViewById(R.id.events_list_votes_count);
//            mViewsCount = (TextView) itemView.findViewById(R.id.events_list_views_count);
            mImagePreloader = (ProgressBar) itemView.findViewById(R.id.events_list_image_preloader);
            mFavoritesImage = (ImageView) itemView.findViewById(R.id.clickimage_favorites);
            mUpvoteImage = (ImageView) itemView.findViewById(R.id.clickimage_like);
            mToolbar = (Toolbar) itemView.findViewById(R.id.event_toolbar);
            mBgItem = (RelativeLayout) itemView.findViewById(R.id.bg_org_item);
            mPlace = (TextView) itemView.findViewById(R.id.events_list_place);
            mBgImageViewColor = (RelativeLayout) itemView.findViewById(R.id.color_bg_iv);
        }

    }
}
