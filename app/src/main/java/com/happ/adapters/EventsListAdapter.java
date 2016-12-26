package com.happ.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happ.App;
import com.happ.R;
import com.happ.Typefaces;
import com.happ.models.Event;
import com.happ.models.Interest;
import com.happ.retrofit.APIService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by iztiev on 8/4/16.
 */
public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventsListViewHolder> {
    private ArrayList<EventListItem> mItems;
    private ArrayList<EventListItem> defaultItems;
    private static final int VIEW_TYPE_HEADER = 0x01;
    private static final int VIEW_TYPE_CONTENT = 0x00;
    private final DateTimeFormatter eventStartDateFormatter;
    private final Context context;
    private SelectEventItemListener mSelectItemListener;
    private boolean isOrganizer;
    private boolean sortByPopularity;

    private InterestsListAdapter mInterestsListAdapter;
    private ArrayList<Interest> interests;


    public interface SelectEventItemListener {
        void onEventItemSelected(String eventId, ActivityOptionsCompat options, int position);
        void onEventEditSelected(String eventId);
    }

    public void setOnSelectItemListener(SelectEventItemListener listener) {
        mSelectItemListener = listener;
    }

    public EventsListAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        defaultItems = new ArrayList<>();
        eventStartDateFormatter = DateTimeFormat.forPattern("MMMM dd, yyyy 'a''t' h:mm a");
        this.updateItems(events);
    }

    public void setSortByPopularity(boolean sortByPopularity) {
        this.sortByPopularity = sortByPopularity;

    }

    public void updateItems(ArrayList<Event> events) {
        defaultItems.clear();
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
            DateTime eventDate = new DateTime(events.get(i).getDatetimes().get(0).getDate());
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
                defaultItems.add(new EventListItem(true, sectionManager, sectionFirstPosition, null, header));
            }
            defaultItems.add(new EventListItem(false, sectionManager, sectionFirstPosition, events.get(i), header));
        }
    }

    protected void sortItems() {
        if (sortByPopularity) {
            ArrayList<EventListItem> sortedItems = new ArrayList<>();
            if (mItems.size() > 0) {
                sortedItems.add(mItems.get(0));
                int first = 1;
                int last = 0;
                for (int i = 1; i < mItems.size(); i++) {
                    if (!mItems.get(i).isHeader) {
                        last = i;
                    } else {
                        ArrayList<EventListItem> tmp = (ArrayList)mItems.subList(first, last);

                        Collections.sort(tmp);
                        sortedItems.addAll(tmp);
                        sortedItems.add(mItems.get(0));

                        if (i + 1 < mItems.size()) {
                            first = i + 1;
                        }
                    }
                }
                ArrayList<EventListItem> tmp = (ArrayList)mItems.subList(first, last);

                Collections.sort(tmp);
                sortedItems.addAll(tmp);
            }
            mItems = sortedItems;
        } else {
            mItems = defaultItems;
        }
    }

    public void updateData(ArrayList<Event> events) {
        this.updateItems(events);
        this.sortItems();
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

    public void eventSelected(EventsListItemViewHolder itemViewHolder, EventListItem item, int position) {
        String id = item.event.getId();
        ActivityOptionsCompat optionsCompat = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Pair<View, String> p1 = Pair.create((View) itemViewHolder.mTextViewTitle, "event_title");
//            Pair<View, String> p2 = Pair.create((View) itemViewHolder.mInterestViewColor, "event_interest_bg");
            Pair<View, String> p3 = Pair.create((View) itemViewHolder.mImageView, "ivent_image");
            Pair<View, String> p4 = Pair.create((View) itemViewHolder.mInterestTitle, "event_interest_name");
            optionsCompat = ActivityOptionsCompat.
//                    makeSceneTransitionAnimation((Activity) context, p1, p2, p3, p4);
                    makeSceneTransitionAnimation((Activity) context, p1, p3, p4);
        }
        if (mSelectItemListener != null) {
            mSelectItemListener.onEventItemSelected(id, optionsCompat, position);
        }
    }



    @Override
    public void onBindViewHolder(final EventsListViewHolder holder, int position) {
        final EventListItem item = mItems.get(position);
        holder.itemView.setOnClickListener(null);
        if (item.isHeader) {

            ((EventsListHeaderViewHolder)holder).mTVDateTitle.setText(item.headerTitle);

        } else {

            final EventsListItemViewHolder itemHolder = (EventsListItemViewHolder)holder;

            RelativeLayout.LayoutParams bgParams = (RelativeLayout.LayoutParams) itemHolder.mBackground.getLayoutParams();
            itemHolder.mBackground.setLayoutParams(bgParams);

            itemHolder.mFavoritesImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.event.isInFavorites()) {
                        APIService.doUnFav(item.event.getId());
                    } else {
                        APIService.doFav(item.event.getId());
                    }
                }
            });
            itemHolder.mUpvoteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.event.isDidVote()) {
                        APIService.doDownVote(item.event.getId());
                    } else {
                        APIService.doUpVote(item.event.getId());
                    }
                }
            });

            final int pos = position;

            itemHolder.mRLFullCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventSelected(itemHolder, item, pos);
                }
            });

            itemHolder.mTextViewTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventSelected(itemHolder, item, pos);
                }
            });


            if(item.event.getImages().size() > 0) {
                String url = item.event.getImages().get(0).getUrl();
                itemHolder.mImagePreloader.setVisibility(View.VISIBLE);

                Picasso.with(context)
                        .load(url)
                        .fit()
                        .centerCrop()
                        .into(itemHolder.mImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                itemHolder.mImagePlaceHolder.setVisibility(View.GONE);
                                itemHolder.mImagePreloader.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                itemHolder.mImagePlaceHolder.setVisibility(View.VISIBLE);
                            }
                        });
            } else {
                itemHolder.mImagePreloader.setVisibility(View.GONE);
                itemHolder.mImagePlaceHolder.setVisibility(View.VISIBLE);
            }

            Typeface tfcs = Typefaces.get(App.getContext(), "fonts/WienLight_Normal.otf");
            itemHolder.mTextViewTitle.setTypeface(tfcs);
            itemHolder.mTextViewTitle.setText(item.event.getTitle());

            itemHolder.mPlace.setText(item.event.getPlace());

            if (item.event.getLowestPrice() == 0) {
                itemHolder.mPrice.setText(context.getResources().getString(R.string.free));
            } else {
                String price = App.getContext().getResources().getString(R.string.from)
                            + " " +
                        item.event.getLowestPrice()
                            + " ";
                        if (item.event.getCurrency().getCode() != null) {
                            price += item.event.getCurrency().getCode();
                        } else {
                            price += "KZT";
                        }
//                        App.getCurrentUser().getSettings().getCurrencyObject().getName();
                itemHolder.mPrice.setText(price);
            }

            itemHolder.mVotesCount.setText(String.valueOf(item.event.getVotesCount()));


            if ( item.event.getInterest() != null) {
                ArrayList<String> fullTitle = item.event.getInterest().getFullTitle();

                String fullTitleString = fullTitle.get(0);
                if (fullTitle.size() > 1) {
                    fullTitleString = fullTitleString + " / " + fullTitle.get(1);
                }
                itemHolder.mInterestTitle.setText(fullTitleString);

            } else {
                itemHolder.mInterestTitle.setText("Null");
            }

            if (item.event.getColor() != null) {
                itemHolder.mBackground.setBackgroundColor(Color.parseColor(item.event.getColor()));
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

//            String startTime = item.event.getStartDateFormatted("HH:mm");
//            String endTime = item.event.getEndDateFormatted("HH:mm");
//            String rangeTime = startTime + " — " + endTime;
//
//            if (startTime.equals(endTime)) {
//                itemHolder.mTime.setText(startTime);
//            } else {
//                itemHolder.mTime.setText(rangeTime);
//            }


            Menu menu = itemHolder.mToolbar.getMenu();
            if (menu != null) menu.clear();
            itemHolder.mToolbar.setVisibility(View.VISIBLE);
            itemHolder.mToolbar.inflateMenu(R.menu.menu_event_feed);
            itemHolder.mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    menuItem.setChecked(false);
                    if (menuItem.getItemId() == R.id.menu_unsubscribe) {

                        mInterestsListAdapter = new InterestsListAdapter(context, interests, true);
                        mInterestsListAdapter.setUserAcivityIds(App.getCurrentUser().getInterestIds());
                        ArrayList<String> selectedInterests = mInterestsListAdapter.getSelectedInterests();
                        String idInterest = item.event.getInterest().getId();
                        selectedInterests.remove(idInterest);
                        APIService.setInterests(selectedInterests);

                    }
                    return false;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class EventsListViewHolder extends RecyclerView.ViewHolder {

        public EventsListViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class EventsListHeaderViewHolder extends EventsListViewHolder {

        public TextView mTVDateTitle;

        public EventsListHeaderViewHolder(View itemView) {
            super(itemView);
            mTVDateTitle = (TextView)itemView.findViewById(R.id.events_list_header_title);
        }
    }

    public class EventsListItemViewHolder extends EventsListViewHolder {
        private TextView mTime;
        private RelativeLayout mBackground;
        private TextView mInterestTitle;
        private ImageView mImageView;
        private ImageView mImagePlaceHolder;
        private TextView mPrice;
        private TextView mPlace;
        private TextView mVotesCount;
        private ImageView mFavoritesImage;
        private ImageView mUpvoteImage;
        private ProgressBar mImagePreloader;
        private Toolbar mToolbar;
        private TextView mTextViewTitle;
        private RelativeLayout mRLFullCard;


        public EventsListItemViewHolder(final View itemView) {
            super(itemView);
            mTextViewTitle = (TextView) itemView.findViewById(R.id.org_event_item_title);
            mTime = (TextView) itemView.findViewById(R.id.event_item_time);
            mInterestTitle = (TextView) itemView.findViewById(R.id.org_event_item_interest);
            mImageView = (ImageView) itemView.findViewById(R.id.org_event_item_imageview);
            mPrice = (TextView) itemView.findViewById(R.id.org_event_item_price);
            mVotesCount = (TextView) itemView.findViewById(R.id.org_event_item_votes_count);
            mImagePreloader = (ProgressBar) itemView.findViewById(R.id.event_item_image_preloader);
            mFavoritesImage = (ImageView) itemView.findViewById(R.id.clickimage_favorites);
            mUpvoteImage = (ImageView) itemView.findViewById(R.id.clickimage_like);
            mToolbar = (Toolbar) itemView.findViewById(R.id.event_toolbar);
            mBackground = (RelativeLayout) itemView.findViewById(R.id.event_item_bg);
            mPlace = (TextView) itemView.findViewById(R.id.event_item_place);
            mImagePlaceHolder = (ImageView) itemView.findViewById(R.id.event_item_image_placeholder);
            mRLFullCard = (RelativeLayout) itemView.findViewById(R.id.rl_event_card);
        }

    }

    public class EventListItem implements Comparable<EventListItem> {
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

        @Override
        public int compareTo(EventListItem o) {
            return event.getViewsCount() - o.event.getViewsCount();
        }
    }
}
