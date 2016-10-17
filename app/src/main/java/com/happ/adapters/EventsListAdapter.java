package com.happ.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.happ.App;
import com.happ.R;
import com.happ.Typefaces;
import com.happ.controllers_drawer.OrganizerModeActivity;
import com.happ.models.Event;
import com.happ.models.Interest;
import com.happ.retrofit.APIService;

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
    private SelectEventItemListener mSelectItemListener;
    private boolean isOrganizer;

    private InterestsListAdapter mInterestsListAdapter;
    private ArrayList<Interest> interests;

    public interface SelectEventItemListener {
        void onEventItemSelected(String eventId, ActivityOptionsCompat options);
        void onEventEditSelected(String eventId);
    }

    public void setOnSelectItemListener(SelectEventItemListener listener) {
        mSelectItemListener = listener;
    }

    public EventsListAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        mItems = new ArrayList<>();
        eventStartDateFormatter = DateTimeFormat.forPattern("MMMM dd, yyyy 'a''t' h:mm a");
        this.updateItems(events);
    }

    public void setIsOrganizer(boolean val) {
        isOrganizer = val;
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

    public void eventSelected(EventsListItemViewHolder itemViewHolder, EventListItem item) {
        String id = item.event.getId();
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


            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventSelected(itemHolder, item);
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
                        Glide.with(App.getContext())
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

                                Glide.with(App.getContext())
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
            Typeface tfcs = Typefaces.get(App.getContext(), "fonts/WienLight_Normal.ttf");
            itemHolder.mTitleView.setTypeface(tfcs);

            itemHolder.mPrice.setText(item.event.getPriceRange());
                itemHolder.mVotesCount.setText(String.valueOf(item.event.getVotesCount()));
//            itemHolder.mViewsCount.setText(String.valueOf(item.event.getViewsCount()));

            if ( item.event.getInterest() != null) {
                ArrayList<String> fullTitle = item.event.getInterest().getFullTitle();

//                if (item.event.getInterest().getColor() != null) {
//                    String colorString =  item.event.getInterest().getColor();
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

//            itemHolder.mDateView.setText(item.event.getStartDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));

            if (!this.isOrganizer) {
                Menu menu = itemHolder.mToolbar.getMenu();
                if (menu != null) menu.clear();
                itemHolder.mToolbar.setVisibility(View.VISIBLE);
                itemHolder.mToolbar.inflateMenu(R.menu.menu_event_feed);
                itemHolder.mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        menuItem.setChecked(false);
                        if (menuItem.getItemId() == R.id.menu_unsubscribe) {

                            mInterestsListAdapter = new InterestsListAdapter(App.getContext(), interests);
                            mInterestsListAdapter.setUserAcivityIds(App.getCurrentUser().getInterestIds());
                            ArrayList<String> selectedInterests = mInterestsListAdapter.getSelectedInterests();
                            String idInterest = item.event.getInterest().getId();
                            selectedInterests.remove(idInterest);
                            APIService.setInterests(selectedInterests);

                        }
                        return false;
                    }
                });

            } else {
                Menu menu = itemHolder.mToolbar.getMenu();
                if (menu != null) menu.clear();
                itemHolder.mToolbar.setVisibility(View.VISIBLE);
                itemHolder.mToolbar.inflateMenu(R.menu.menu_event_org);
                itemHolder.mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        menuItem.setChecked(false);
                        if (menuItem.getItemId() == R.id.menu_edit) {
                            mSelectItemListener.onEventEditSelected(item.event.getId());
                        }
                        if (menuItem.getItemId() == R.id.menu_delete) {
                            ((OrganizerModeActivity) context).functionToRun(item.event.getId());
                        }
                        return false;
                    }
                });
            }
        }
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class EventsListViewHolder extends RecyclerView.ViewHolder {

        public CardView mCard;
        public TextView mTitleView;

        public EventsListViewHolder(View itemView) {
            super(itemView);
            mCard = (CardView) itemView.findViewById(R.id.cardView);
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
        public ImageView mUpvoteImage, mDownVoteImage;
        public ProgressBar mImagePreloader;
        public Toolbar mToolbar;


        public EventsListItemViewHolder(final View itemView) {
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
        }

    }

    public class EventListItem {
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
