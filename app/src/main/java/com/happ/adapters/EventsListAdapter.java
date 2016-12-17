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
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.happ.App;
import com.happ.R;
import com.happ.Typefaces;
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
        void onEventItemSelected(String eventId, ActivityOptionsCompat options, int position);
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

//            if(item.event.getImages().size() > 0){
//                final String url = item.event.getImages().get(0).getUrl();
////                final String url = "http://lorempixel.com/g/1080/610/nature/" + position + "/";
//                Glide.clear(itemHolder.mImageView);
//                itemHolder.mImagePreloader.setVisibility(View.VISIBLE);
//
//                try {
//                    int viewWidth = itemHolder.mImageView.getWidth();
//                    int viewHeight = itemHolder.mImageView.getHeight();
//                    if (viewHeight > 0 && viewHeight > 0) {
//                        Glide.with(App.getContext())
//                                .load(url)
//                                .listener(new RequestListener<String, GlideDrawable>() {
//                                    @Override
//                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                        return false;
//                                    }
//
//                                    @Override
//                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                        itemHolder.mImagePreloader.setVisibility(View.INVISIBLE);
//                                        Bitmap bm = ((GlideBitmapDrawable)resource.getCurrent()).getBitmap();
//                                        Palette p = Palette.from(bm).generate();
//                                        itemHolder.mBackground.setBackgroundColor(p.getMutedSwatch().getRgb());
//                                        return false;
//                                    }
//                                })
//                                .override(viewWidth, viewHeight)
//                                .centerCrop()
//                                .into(itemHolder.mImageView);
//                    }
//                } catch (Exception ex) {
//                    ViewTreeObserver viewTreeObserver = itemHolder.mImageView.getViewTreeObserver();
//                    if (viewTreeObserver.isAlive()) {
//                        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                            @Override
//                            public void onGlobalLayout() {
//                                itemHolder.mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                                int viewWidth = itemHolder.mImageView.getWidth();
//                                int viewHeight = itemHolder.mImageView.getHeight();
//                                Log.d("HEIGHT_WIDTH", String.valueOf(viewWidth)+" "+String.valueOf(viewHeight));
//
//                                Glide.with(App.getContext())
//                                        .load(url)
//                                        .listener(new RequestListener<String, GlideDrawable>() {
//                                            @Override
//                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                                return false;
//                                            }
//
//                                            @Override
//                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                                itemHolder.mImagePreloader.setVisibility(View.INVISIBLE);
//                                                return false;
//                                            }
//                                        })
//                                        .override(viewWidth, viewHeight)
//                                        .centerCrop()
//                                        .into(itemHolder.mImageView);
//                            }
//                        });
//                    }
//                }
//            } else{
//                Glide.clear(itemHolder.mImageView);
//                itemHolder.mImageView.setImageDrawable(null);
//                itemHolder.mImagePreloader.setVisibility(View.INVISIBLE);
//            }



            if(item.event.getImages().size() > 0){
                final String url = item.event.getImages().get(0).getUrl();
                Glide.clear(itemHolder.mImageView);
                itemHolder.mImagePreloader.setVisibility(View.VISIBLE);
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

                                                itemHolder.mImagePlaceHolder.setVisibility(View.VISIBLE);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                Log.d("GLIDE_OK", url);
//                                                Bitmap bm = ((GlideBitmapDrawable)resource.getCurrent()).getBitmap();
//                                                Palette p = Palette.from(bm).generate();
//
//                                                Palette.Swatch vibrantSwatch = p.getVibrantSwatch();
//
//                                                String strColor = String.format("#%06X", 0xFFFFFF & vibrantSwatch.getRgb());
//
//                                                Realm realm = Realm.getDefaultInstance();
//                                                Event event = realm.where(Event.class).equalTo("id",item.event.getId()).findFirst();
//                                                if (event != null) {
//                                                    realm.beginTransaction();
//                                                    event.setColor(strColor);
//                                                    realm.copyToRealmOrUpdate(event);
//                                                    realm.commitTransaction();
//                                                }
//                                                realm.close();
//
//                                                if (vibrantSwatch != null) {
//                                                    itemHolder.mBackground.setBackgroundColor(vibrantSwatch.getRgb());
//                                                } else {
//
//                                                }

                                                itemHolder.mImagePlaceHolder.setVisibility(View.INVISIBLE);
                                              itemHolder.mImagePreloader.setVisibility(View.INVISIBLE);
                                                return false;
                                            }
                                        })
                                        .override(viewWidth, viewHeight)
                                        .centerCrop()
                                        .into(itemHolder.mImageView);

//                            final Drawable image = holder.mImageView.getDrawable();
//                            if (holder.mImageView.getVie  wTreeObserver().isAlive()) {
//                                holder.mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                                    @Override
//                                    public void onGlobalLayout() {
//                                        if (image != null && !image.equals(holder.mImageView.getDrawable())) {
//                                            holder.mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                                            setBlurs(holder, position);
//                                        }
//                                    }
//                                });
//
//                            }
                            }
                        });
                    }
                } catch (Exception ex) {
                    Log.e("EVENTS", ex.getLocalizedMessage());
                }
            } else {
                Glide.clear(itemHolder.mImageView);
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

            String startTime = item.event.getStartDateFormatted("HH:mm");
            String endTime = item.event.getEndDateFormatted("HH:mm");
            String rangeTime = startTime + " — " + endTime;

            if (startTime.equals(endTime)) {
                itemHolder.mTime.setText(startTime);
            } else {
                itemHolder.mTime.setText(rangeTime);
            }


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
