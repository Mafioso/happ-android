package com.happ.controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.happ.R;
import com.happ.models.Event;
import com.happ.models.Interest;

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
                DateTimeFormatter fmt = DateTimeFormat.forPattern("MMMM dd, yyyy");
                header = eventDate.toString(fmt);
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
    public void onBindViewHolder(EventsListViewHolder holder, int position) {
        final EventListItem item = mItems.get(position);
        if (item.isHeader) {
            ((EventsListHeaderViewHolder)holder).mTitleView.setText(item.headerTitle);
        } else {
            final EventsListItemViewHolder itemHolder = (EventsListItemViewHolder)holder;

            if(item.event.getImages().size() > 0){
                final String url = item.event.getImages().get(0).getUrl();
                Glide.clear(itemHolder.mImageView);

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
                                    .override(viewWidth, viewHeight)
                                    .centerCrop()
                                    .into(itemHolder.mImageView);
                        }
                    });
                }


            }
            else{
                Glide.clear(itemHolder.mImageView);
                itemHolder.mImageView.setImageDrawable(null);
            }

            itemHolder.mTitleView.setText(item.event.getTitle());
            itemHolder.mCurencySymbol.setText(item.event.getCurrency().getSymbol());
            itemHolder.mHighestPrice.setText(Integer.toString(item.event.getHighestPrice()));
            itemHolder.mLowestPrice.setText(Integer.toString(item.event.getLowestPrice()));
            itemHolder.mVotesCount.setText(Integer.toString(item.event.getVotesCount()));
            itemHolder.mViewsCount.setText(Integer.toString(item.event.getViewsCount()));

            itemHolder.mInterestTitle.setText(item.event.getInterest().getTitle());

            Interest interest = item.event.getInterest();
            if (interest.getColor() != null) {
                itemHolder.mInterestViewColor.setBackgroundColor(Color.parseColor(interest.getColor()));
            }

            DateTime eventDate = new DateTime(item.event.getStartDate());
            String dateString = eventDate.toString(eventStartDateFormatter);

            itemHolder.mDateView.setText(dateString);
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
        public TextView mInterest;
        public TextView mInterestTitle;
        public ImageView mImageView;
        public TextView mCurencySymbol;
        public TextView mHighestPrice;
        public TextView mLowestPrice;
        public TextView mVotesCount;
        public TextView mViewsCount;
        public Event event;

        public EventsListItemViewHolder(final View itemView) {
            super(itemView);
            mTitleView = (TextView)itemView.findViewById(R.id.events_list_item_title);
            mDateView = (TextView)itemView.findViewById(R.id.events_list_item_start_date);
            mInterestViewColor = (LinearLayout)itemView.findViewById(R.id.events_list_interest_color);
            mInterest = (TextView)itemView.findViewById(R.id.events_list_item_interest);
            mInterestTitle = (TextView)itemView.findViewById(R.id.events_list_item_interest_title);
            mImageView = (ImageView)itemView.findViewById(R.id.events_list_image_view);
            mCurencySymbol = (TextView)itemView.findViewById(R.id.events_list_currency_symbol);
            mHighestPrice = (TextView)itemView.findViewById(R.id.events_list_highestprice);
            mLowestPrice = (TextView)itemView.findViewById(R.id.events_list_lowestprice);
            mVotesCount = (TextView)itemView.findViewById(R.id.events_list_votes_count);
            mViewsCount = (TextView)itemView.findViewById(R.id.events_list_views_count);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), EventActivity.class);
                    v.getContext().startActivity(intent);
                    Toast.makeText(v.getContext(), "Hello.", Toast.LENGTH_SHORT).show();
                }
            });

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
