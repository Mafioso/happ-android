package com.happ.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.happ.R;
import com.happ.models.Event;
import com.happ.models.Interest;

import java.util.ArrayList;

/**
 * Created by iztiev on 10/21/16.
 */

public class ChildInterestsAdapter  extends RecyclerView.Adapter<ChildInterestsAdapter.ChildInterestsViewHolder> {
    ArrayList<Interest> interests;
    Context context;

    public ChildInterestsAdapter(Context context, ArrayList<Interest> interests) {
        this.context = context;
        this.interests = interests;
    }

    @Override
    public ChildInterestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_child_item, parent, false);
        return new ChildInterestsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChildInterestsViewHolder holder, int position) {
        if (position % 3 == 0 || position % 5 == 0 || position % 7 == 0) {
            holder.mImageView.setVisibility(View.VISIBLE);
            holder.mTitleView.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.mImageView.setVisibility(View.GONE);
            holder.mTitleView.setTextColor(context.getResources().getColor(R.color.dark87));
        }
        holder.mTitleView.setText(interests.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return interests.size();
    }

    public class ChildInterestsViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitleView;
        public ImageView mImageView;


        public ChildInterestsViewHolder(final View itemView) {
            super(itemView);
            mTitleView = (TextView)itemView.findViewById(R.id.child_text);
            mImageView = (ImageView)itemView.findViewById(R.id.child_check);
        }

    }
}
