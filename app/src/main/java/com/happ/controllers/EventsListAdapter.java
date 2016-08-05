package com.happ.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.FutureTarget;
import com.happ.R;
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

            if(itemHolder.mImageView != null && item.event.getImages().size() > 0){
//                ParseFile image = (ParseFile) parseList.get(position).get("logo");
                final String url = item.event.getImages().get(0).getUrl();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            RequestManager rm = Glide.with(itemHolder.mImageView.getContext());
                            DrawableTypeRequest<String> dtr = rm.load(url);
                            BitmapTypeRequest<String> btr = dtr.asBitmap();
                            FutureTarget<Bitmap> ft = btr.into(-1, -1);
                            Bitmap bm = ft.get();
                            int a = 100;
                        } catch (Exception ex) {
                            System.out.print(ex.getLocalizedMessage());
                        }
                    }
                }).start();



//                Glide.with(itemHolder.mImageView.getContext())
//                        .load(url)
//                        .asBitmap()
//                        .into(itemHolder.mImageView);
            }
            else{
                Glide.clear(itemHolder.mImageView);
                itemHolder.mImageView.setImageDrawable(null);
            }

            itemHolder.mTitleView.setText(item.event.getTitle());
            itemHolder.mInterestView.setText(item.event.getInterest().getTitle());

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
        public TextView mInterestView;
        public ImageView mImageView;

        public EventsListItemViewHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView)itemView.findViewById(R.id.events_list_item_title);
            mDateView = (TextView)itemView.findViewById(R.id.events_list_item_start_date);
            mInterestView = (TextView)itemView.findViewById(R.id.events_list_item_interest);
            mImageView = (ImageView)itemView.findViewById(R.id.events_list_image_view);
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
