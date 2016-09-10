package com.happ.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.happ.App;
import com.happ.R;
import com.happ.Typefaces;
import com.happ.models.Event;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by iztiev on 8/4/16.
 */
public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventsListViewHolder> {
    private final ArrayList<EventListItem> mItems;
    private static final int VIEW_TYPE_HEADER = 0x01;
    private static final int VIEW_TYPE_CONTENT = 0x00;
    private final DateTimeFormatter eventStartDateFormatter;
    private final Context context;

    public EventsListAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        mItems = new ArrayList<>();
        eventStartDateFormatter = DateTimeFormat.forPattern("MMMM dd, yyyy 'a''t' h:mm a");
        this.updateItems(events);
    }

    public void updateItems(ArrayList<Event> events) {
        mItems.clear();
        DateTime now = new DateTime();
        now.minusHours(now.hourOfDay().get());
        now.minusMinutes(now.minuteOfHour().get());
        now.minusSeconds(now.secondOfMinute().get());
        now.minusMillis(now.millisOfSecond().get());

        String lastHeader = "";
        int sectionManager = -1;
        int headerCount = 0;
        int sectionFirstPosition = 0;

        for (int i=0; i<events.size(); i++) {
            DateTime eventDate = new DateTime(events.get(i).getStartDate());
            eventDate.minusHours(eventDate.hourOfDay().get());
            eventDate.minusMinutes(eventDate.minuteOfHour().get());
            eventDate.minusSeconds(eventDate.secondOfMinute().get());
            eventDate.minusMillis(eventDate.millisOfSecond().get());
            String header = "";
            int days = Days.daysBetween(eventDate,now).getDays();
            if (days == 0) {
                header = context.getString(R.string.today);
            } else if (days == -1) {
                header = context.getString(R.string.tomorrow);
            } else {
                header = events.get(i).getStartDateFormatted("MMMM dd, yyyy");
            }
            if (!TextUtils.equals(lastHeader, header)) {
                sectionManager = (sectionManager + 1) % 2;
                sectionFirstPosition = i + headerCount;
                lastHeader = header;
                headerCount += 1;
                mItems.add(new EventListItem(true, sectionManager, sectionFirstPosition, null, header));
            }
            mItems.add(new EventListItem(false, sectionManager, sectionFirstPosition, events.get(i), header));
        }
    }

    public void updateData(ArrayList<Event> events) {
        this.updateItems(events);
        Log.d("AAAAA", String.valueOf(events.size()));
        this.notifyDataSetChanged();
        this.notifyHeaderChanges();
    }

    private void notifyHeaderChanges() {
        for (int i = 0; i < mItems.size(); i++) {
            EventListItem item = mItems.get(i);
            if (item.isHeader) {
                notifyItemChanged(i);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public EventsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_header, parent, false);
            return new EventsListHeaderViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
            return new EventsListItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final EventsListViewHolder holder, int position) {
        final EventListItem item = mItems.get(position);
        holder.itemView.setOnClickListener(null);
        if (item.isHeader) {

            ((EventsListHeaderViewHolder)holder).mTitleView.setText(item.headerTitle);

        } else {

            final EventsListItemViewHolder itemHolder = (EventsListItemViewHolder)holder;
            itemHolder.mFavoritesImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Add to favs from event "+item.event.getId(), Toast.LENGTH_LONG).show();
                }
            });
            itemHolder.mUpvoteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Like for event "+item.event.getId(), Toast.LENGTH_LONG).show();
                }
            });

            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, EventActivity.class);
                    intent.putExtra("event_id", item.event.getId());
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
                    } else {

                        Pair<View, String> p1 = Pair.create((View) itemHolder.mTitleView, "event_title");
                        Pair<View, String> p2 = Pair.create((View) itemHolder.mInterestViewColor, "event_interest_bg");
                        Pair<View, String> p3 = Pair.create((View) itemHolder.mImageView, "ivent_image");
                        Pair<View, String> p4 = Pair.create((View) itemHolder.mInterestTitle, "event_interest_name");
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) context, p1, p2, p3, p4);
                        context.startActivity(intent, options.toBundle());
                    }
                }
            });

            if(item.event.getImages().size() > 0){
                final String url = item.event.getImages().get(0).getUrl();
//                final String url = "http://lorempixel.com/g/1080/610/nature/" + position + "/";
                Glide.clear(itemHolder.mImageView);
                itemHolder.mImagePreloader.setVisibility(View.VISIBLE);

                try {
                    int viewWidth = itemHolder.mImageView.getWidth();
                    int viewHeight = itemHolder.mImageView.getHeight();
                    if (viewHeight > 0 && viewHeight > 0) {
                        Glide.with(itemHolder.mImageView.getContext())
                                .load(url)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        itemHolder.mImagePreloader.setVisibility(View.INVISIBLE);
                                        return false;
                                    }
                                })
                                .override(viewWidth, viewHeight)
                                .centerCrop()
                                .into(itemHolder.mImageView);
                    }
                } catch (Exception ex) {
                    ViewTreeObserver viewTreeObserver = itemHolder.mImageView.getViewTreeObserver();
                    if (viewTreeObserver.isAlive()) {
                        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                itemHolder.mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                int viewWidth = itemHolder.mImageView.getWidth();
                                int viewHeight = itemHolder.mImageView.getHeight();
                                Log.d("HEIGHT_WIDTH", String.valueOf(viewWidth)+" "+String.valueOf(viewHeight));

                                Glide.with(itemHolder.mImageView.getContext())
                                        .load(url)
                                        .listener(new RequestListener<String, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
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
                }




            } else{
                Glide.clear(itemHolder.mImageView);
                itemHolder.mImageView.setImageDrawable(null);
                itemHolder.mImagePreloader.setVisibility(View.INVISIBLE);
            }


            itemHolder.mTitleView.setText(item.event.getTitle());
            Typeface tfcs = Typefaces.get(App.getContext(), "fonts/RBNo2Light_a.otf");
            itemHolder.mTitleView.setTypeface(tfcs);
            itemHolder.mPrice.setText(item.event.getPriceRange());
            itemHolder.mVotesCount.setText(String.valueOf(item.event.getVotesCount()));
            itemHolder.mViewsCount.setText(String.valueOf(item.event.getViewsCount()));

            ArrayList<String> fullTitle = item.event.getInterest().getFullTitle();

            String fullTitleString = fullTitle.get(0);
            if (fullTitle.size() > 1) {
                fullTitleString = fullTitleString + " / " + fullTitle.get(1);
            }
            itemHolder.mInterestTitle.setText(fullTitleString);

            if (item.event.getInterest().getColor() != null) {
                String color = "#" + item.event.getInterest().getColor();
                itemHolder.mInterestViewColor.setBackgroundColor(Color.parseColor(color));
            }

            if (item.event.isDidVote()) {
                itemHolder.mUpvoteImage.setImageResource(R.drawable.ic_did_upvote);
            } else {
                itemHolder.mUpvoteImage.setImageResource(R.drawable.ic_did_not_upvote);
            }

            if (item.event.isInFavorites()) {
                itemHolder.mFavoritesImage.setImageResource(R.drawable.ic_in_favorites);
            } else {
                itemHolder.mFavoritesImage.setImageResource(R.drawable.ic_not_in_favorites);
            }


            itemHolder.mDateView.setText(item.event.getStartDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class EventsListViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleView;

        public EventsListViewHolder(View itemView) {

            super(itemView);
        }
    }

    public class EventsListHeaderViewHolder extends EventsListViewHolder {

        public EventsListHeaderViewHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView)itemView.findViewById(R.id.events_list_header_title);
        }
    }

    public class EventsListItemViewHolder extends EventsListViewHolder {
        public TextView mDateView;
        public LinearLayout mInterestViewColor;
        public TextView mInterestTitle;
        public ImageView mImageView;
        public TextView mPrice;
        public TextView mVotesCount;
        public TextView mViewsCount;
        public Event event;
        public ImageView mFavoritesImage;
        public ImageView mUpvoteImage;
        public ProgressBar mImagePreloader;

        public EventsListItemViewHolder(final View itemView) {
            super(itemView);
            mTitleView = (TextView)itemView.findViewById(R.id.events_list_item_title);
            mDateView = (TextView)itemView.findViewById(R.id.events_list_item_start_date);
            mInterestViewColor = (LinearLayout)itemView.findViewById(R.id.events_list_interest_color);
            mInterestTitle = (TextView)itemView.findViewById(R.id.events_list_item_interest);
            mImageView = (ImageView)itemView.findViewById(R.id.events_list_image_view);
            mPrice = (TextView)itemView.findViewById(R.id.events_list_price);
            mVotesCount = (TextView)itemView.findViewById(R.id.events_list_votes_count);
            mViewsCount = (TextView)itemView.findViewById(R.id.events_list_views_count);
            mImagePreloader = (ProgressBar)itemView.findViewById(R.id.events_list_image_preloader);
            mFavoritesImage = (ImageView)itemView.findViewById(R.id.clickimage_favorites);
            mUpvoteImage = (ImageView)itemView.findViewById(R.id.clickimage_like_or_dislike);
        }
    }

    private class EventListItem {
        public int sectionManager;
        public int sectionFirstPosition;
        public boolean isHeader;
        public String headerTitle;
        public Event event;

        public EventListItem(boolean isHeader, int sectionManager, int sectionFirstPosition, Event event, String headerTitle) {
            this.isHeader = isHeader;
            this.sectionManager = sectionManager;
            this.sectionFirstPosition = sectionFirstPosition;
            this.event = event;
            this.headerTitle = headerTitle;
        }
    }
}
