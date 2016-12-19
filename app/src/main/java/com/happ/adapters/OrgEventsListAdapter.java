package com.happ.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.happ.controllers_drawer.OrganizerModeActivity;
import com.happ.models.Event;
import com.happ.retrofit.APIService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by iztiev on 8/4/16.
 */
public class OrgEventsListAdapter extends RecyclerView.Adapter<OrgEventsListAdapter.OrgEventsListViewHolder> {
    private ArrayList<Event> mItems;
    private final Context context;
    private SelectEventItemListener mSelectItemListener;

    public interface SelectEventItemListener {
        void onEventItemSelected(String eventId, ActivityOptionsCompat options);
        void onEventEditSelected(String eventId);
    }

    public void setOnSelectItemListener(SelectEventItemListener listener) {
        mSelectItemListener = listener;
    }

    public OrgEventsListAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.mItems = events;
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

        Typeface tfcs = Typefaces.get(App.getContext(), "fonts/WienLight_Normal.otf");
        itemHolder.mTitleView.setTypeface(tfcs);
        itemHolder.mTitleView.setText(event.getTitle());

        if ( event.getInterest() != null) {
            ArrayList<String> fullTitle = event.getInterest().getFullTitle();

            String fullTitleString = fullTitle.get(0);
            if (fullTitle.size() > 1) {
                fullTitleString = fullTitleString + " / " + fullTitle.get(1);
            }
            itemHolder.mInterestTitle.setText(fullTitleString);

        } else {
            itemHolder.mInterestTitle.setText("Null");
        }

        if (event.getLowestPrice() == 0) {
            itemHolder.mPrice.setText(context.getResources().getString(R.string.free));
        } else {
            String price = App.getContext().getResources().getString(R.string.from)
                    + " " +
                    event.getLowestPrice()
                    + " ";
            if (event.getCurrency().getCode() != null) {
                price += event.getCurrency().getCode();
            } else {
                price += "KZT";
            }
            itemHolder.mPrice.setText(price);
        }

        if (event.getPlace() != null) {
            itemHolder.mPlace.setText(event.getPlace());
        } else {
            itemHolder.mPlace.setText("Venue unknown");
        }

        String startTime = event.getStartDateFormatted("dd MMM HH:mm").toUpperCase();
        String endTime = event.getEndDateFormatted("dd MMM HH:mm").toUpperCase();
        String rangeTime = startTime + " — " + endTime;

        if (startTime.equals(endTime)) {
            itemHolder.mDateView.setText(startTime);
        } else {
            itemHolder.mDateView.setText(rangeTime);
        }

        itemHolder.mVotesCount.setText(String.valueOf(event.getVotesCount()));

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


        if(event.getImages().size() > 0) {
            String url = event.getImages().get(0).getUrl();

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


        if (event.getStatus() == 2) {
            itemHolder.mEventStatus.setImageResource(R.drawable.ic_close_x);
            itemHolder.mBottomBar.setVisibility(View.GONE);
            itemHolder.mBgItem.setBackgroundColor(ContextCompat.getColor(context, R.color.declined_event_color));
            itemHolder.mPlace.setVisibility(View.GONE);
            itemHolder.mPrice.setVisibility(View.GONE);
            itemHolder.mDateView.setVisibility(View.GONE);
        } else if (event.getEndDate().getTime() < new Date().getTime()) {

            setLocked(itemHolder.mImageView);
            itemHolder.mEventStatus.setVisibility(View.GONE);
            itemHolder.mBgItem.setBackgroundColor(ContextCompat.getColor(context, R.color.dark57));
        } else {

            itemHolder.mBgItem.setBackgroundColor(Color.parseColor(event.getColor()));

            if (event.getStatus() == 0) {
                itemHolder.mEventStatus.setImageResource(R.drawable.ic_onreview);
            } else if (event.getStatus() == 1) {
                itemHolder.mEventStatus.setImageResource(R.drawable.ic_done);
            }
            // status
            // на модерации - 0
            // активный - 1
            // отклонён - 2
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

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventSelected(itemHolder, event);
            }
        });

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

    private static void  setLocked(ImageView v) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        v.setColorFilter(cf);
        v.setAlpha(128);   // 128 = 0.5
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
        private TextView mDateView;
        private TextView mInterestTitle;
        private TextView mPrice;
        private TextView mVotesCount;
        private TextView mPlace;

        private ImageView mImageView;
        private ImageView mFavoritesImage;
        private ImageView mUpvoteImage;
        private ImageView mImagePlaceHolder;
        private ImageView mEventStatus;

        private ProgressBar mImagePreloader;
        private Toolbar mToolbar;
        private RelativeLayout mBgItem;
        private RelativeLayout mBottomBar;


        public OrgEventsListItemViewHolder(final View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(R.id.org_event_item_title);
            mVotesCount = (TextView) itemView.findViewById(R.id.org_event_item_votes_count);
            mPlace = (TextView) itemView.findViewById(R.id.org_event_item_place);
            mInterestTitle = (TextView) itemView.findViewById(R.id.org_event_item_interest);
            mPrice = (TextView) itemView.findViewById(R.id.org_event_item_price);
            mDateView = (TextView) itemView.findViewById(R.id.org_event_item_datetime);

            mImageView = (ImageView) itemView.findViewById(R.id.org_event_item_imageview);
            mFavoritesImage = (ImageView) itemView.findViewById(R.id.clickimage_favorites);
            mUpvoteImage = (ImageView) itemView.findViewById(R.id.clickimage_like);
            mImagePlaceHolder = (ImageView) itemView.findViewById(R.id.org_event_item_placeholder);
            mEventStatus = (ImageView) itemView.findViewById(R.id.org_event_item_status);

            mToolbar = (Toolbar) itemView.findViewById(R.id.event_toolbar);
            mBgItem = (RelativeLayout) itemView.findViewById(R.id.org_event_item_bg_color);
            mImagePreloader = (ProgressBar) itemView.findViewById(R.id.event_item_image_preloader);
            mBottomBar = (RelativeLayout) itemView.findViewById(R.id.rl_bottom_bar);


        }

    }
}
