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
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.ArrayList;

/**
 * Created by iztiev on 8/4/16.
 */
public class InterestsListAdapter extends RecyclerView.Adapter<InterestsListAdapter.InterestsListViewHolder> implements INameableAdapter {
    private final ArrayList<Interest> mItems;
    private final Context context;
    private OnItemClickListener listener;

    public InterestsListAdapter(Context context, ArrayList<Interest> interests) {
        this.context = context;
        this.mItems = interests;
    }

    public InterestsListAdapter(Context context, ArrayList<Interest> interests, OnItemClickListener listener) {
        this.context = context;
        this.mItems = interests;
        this.listener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


//    public void updateItems(ArrayList<Interest> interests) {
//        mItems.clear();
//    }

//    public void updateData(ArrayList<Interest> interests) {
//        this.updateItems(interests);
//        Log.d("AAAAA", String.valueOf(interests.size()));
//        this.notifyDataSetChanged();
//        this.notifyHeaderChanges();
//    }

//    private void notifyHeaderChanges() {
//        for (int i = 0; i < mItems.size(); i++) {
//            InterestListItem item = mItems.get(i);
//            if (item.isHeader) {
//                notifyItemChanged(i);
//            }
//        }
//    }

//    @Override
//    public int getItemViewType(int position) {
//        return mItems.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
//    }

    @Override
    public InterestsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_list_item, parent, false);
        return new InterestsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InterestsListViewHolder holder, int position) {
//        final InterestListItem item = mItems.get(position);
//        holder.itemView.setOnClickListener(null);
//            final InterestsListItemViewHolder itemHolder = (InterestsListItemViewHolder)holder;
        holder.mTitleInterest.setText(mItems.get(position).getTitle());
        holder.bind(mItems.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public Character getCharacterForElement(int element) {
        return mItems.get(element).getTitle().charAt(0);
    }

    public class InterestsListViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitleInterest;
        public InterestsListViewHolder(View itemView) {
            super(itemView);
            mTitleInterest = (TextView)itemView.findViewById(R.id.test_interests_title);
        }

        public void bind(final Interest interest, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(">>CLICK<<","TRUE");
                    if (listener != null) {
                        listener.onItemClick(interest);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Interest interest);
    }
}
