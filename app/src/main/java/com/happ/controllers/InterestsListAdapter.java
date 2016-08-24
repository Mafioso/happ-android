package com.happ.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happ.R;
import com.happ.models.Event;
import com.happ.models.Interest;

import java.util.ArrayList;

/**
 * Created by iztiev on 8/4/16.
 */
public class InterestsListAdapter extends RecyclerView.Adapter<InterestsListAdapter.InterestsListViewHolder> {
    private final ArrayList<InterestListItem> mItems;
    private static final int VIEW_TYPE_HEADER = 0x01;
    private static final int VIEW_TYPE_CONTENT = 0x00;
    private final Context context;

    public InterestsListAdapter(Context context, ArrayList<Interest> interests) {
        this.context = context;
        mItems = new ArrayList<>();
        this.updateItems(interests);
    }

    public void updateItems(ArrayList<Interest> interests) {
        mItems.clear();
    }

    public void updateData(ArrayList<Interest> interests) {
        this.updateItems(interests);
        Log.d("AAAAA", String.valueOf(interests.size()));
        this.notifyDataSetChanged();
        this.notifyHeaderChanges();
    }

    private void notifyHeaderChanges() {
        for (int i = 0; i < mItems.size(); i++) {
            InterestListItem item = mItems.get(i);
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
    public InterestsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_list_item, parent, false);
            return new InterestsListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InterestsListViewHolder holder, int position) {
        final InterestListItem item = mItems.get(position);
        holder.itemView.setOnClickListener(null);
            final InterestsListItemViewHolder itemHolder = (InterestsListItemViewHolder)holder;
            itemHolder.mTitleInterest.setText(item.interest.getTitle());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class InterestsListViewHolder extends RecyclerView.ViewHolder {

        public InterestsListViewHolder(View itemView) {

            super(itemView);
        }
    }

    public class InterestsListItemViewHolder extends InterestsListViewHolder {
       public TextView mTitleInterest;

        public InterestsListItemViewHolder(final View itemView) {
            super(itemView);
            mTitleInterest = (TextView) itemView.findViewById(R.id.test_interests_title);
        }
    }

    private class InterestListItem {
        public int sectionManager;
        public int sectionFirstPosition;
        public boolean isHeader;
        public Interest interest;

        public InterestListItem(boolean isHeader, int sectionManager, int sectionFirstPosition, Event event, String headerTitle) {
            this.isHeader = isHeader;
            this.sectionManager = sectionManager;
            this.sectionFirstPosition = sectionFirstPosition;
            this.interest = interest;
        }
    }
}
