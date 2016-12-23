package com.happ.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happ.App;
import com.happ.R;
import com.happ.Typefaces;
import com.happ.models.Event;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by iztiev on 8/4/16.
 */
public class ExploreListAdapter extends RecyclerView.Adapter<ExploreListAdapter.ExploreListViewHolder> {
    private ArrayList<Event> mEvents;
    private final Context context;
    SelectEventExploreItemListener listener;

    public ExploreListAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        mEvents = events;
    }

    public void setOnSelectEventExploreListener(SelectEventExploreItemListener listener) {
        this.listener = listener;
    }

    public void updateData(ArrayList<Event> events) {
        mEvents = events;
        Log.d("EXPLORE ADAPTER", String.valueOf(events.size()));
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ExploreListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_explore_item, parent, false);
        return new ExploreListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ExploreListViewHolder holder, int position) {
        final Event events = mEvents.get(position);

        if(events.getImages().size() > 0){
            final String url = events.getImages().get(0).getUrl();
//            holder.mImagePreloader.setVisibility(View.VISIBLE);

            Picasso.with(context)
                    .load(url)
                    .fit()
                    .centerCrop()
                    .into(holder.mImageView, new Callback() {
                        @Override
                        public void onSuccess() {
//                            holder.mImagePreloader.setVisibility(View.GONE);
                            holder.mImagePlaceHolder.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
//                            holder.mImagePreloader.setVisibility(View.VISIBLE);
                        }
                    });

        } else {
//            holder.mImagePreloader.setVisibility(View.GONE);
            holder.mImagePlaceHolder.setVisibility(View.VISIBLE);
            holder.mImageView.setImageResource(android.R.color.transparent);
        }

        holder.mTextView.setText(events.getTitle());

        if (events.getColor() != null) {
            holder.mBackground.setBackgroundColor(Color.parseColor(events.getColor()));
        }

        Typeface tfcs = Typefaces.get(App.getContext(), "fonts/WienLight_Normal.otf");
        holder.mTextView.setTypeface(tfcs);

        holder.bind(events);
    }


    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ExploreListViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;
        private ImageView mImageView, mImagePlaceHolder;
        private RelativeLayout mBackground;

        public ExploreListViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.explore_textview);
            mImageView = (ImageView) itemView.findViewById(R.id.imageview_explore);
            mImagePlaceHolder = (ImageView) itemView.findViewById(R.id.explore_item_iv_placeholder);
            mBackground = (RelativeLayout) itemView.findViewById(R.id.explore_item_bg);

        }

        public void bind(final Event event) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onExploreEventItemSelected(event);
                }
            });
        }
    }

    public interface SelectEventExploreItemListener {
        void onExploreEventItemSelected(Event event);
    }

}
