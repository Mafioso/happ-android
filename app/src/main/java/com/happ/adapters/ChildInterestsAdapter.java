package com.happ.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.happ.R;
import com.happ.models.Interest;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by iztiev on 10/21/16.
 */

public class ChildInterestsAdapter  extends RecyclerView.Adapter<ChildInterestsAdapter.ChildInterestsViewHolder> {
    ArrayList<Interest> interests;
    ArrayList<String> selectedInterests;
    Context context;
    OnChildItemChanged listener;
    private boolean single;

    public ChildInterestsAdapter(Context context, ArrayList<Interest> interests, ArrayList<String> selectedInterests) {
        this.context = context;
        this.interests = interests.get(0).getChildren();
        this.selectedInterests = selectedInterests;
//        this.interests = interests;
    }

    public void updateSelectedInterests(ArrayList<String> selectedInterests) {
        this.selectedInterests = selectedInterests;
        notifyDataSetChanged();
    }

    public void setOnChildItemChangedListener(OnChildItemChanged listener) {
        this.listener = listener;
    }



    @Override
    public ChildInterestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_child_item, parent, false);
        return new ChildInterestsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChildInterestsViewHolder holder, int position) {
        boolean isSelected = false;
        for (Iterator<String> id = selectedInterests.iterator(); id.hasNext();) {
            if (id.next().equals(interests.get(position).getId())) {
                isSelected = true;
                break;
            }
        }
        if (isSelected) {
            holder.mImageView.setVisibility(View.VISIBLE);
            holder.mTitleView.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.mImageView.setVisibility(View.GONE);
            holder.mTitleView.setTextColor(context.getResources().getColor(R.color.dark87));
        }
        holder.mTitleView.setText(interests.get(position).getTitle());
        holder.bind(interests.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return interests.size();
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public boolean isSingle() {
        return single;
    }

    public class ChildInterestsViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitleView;
        public ImageView mImageView;


        public ChildInterestsViewHolder(final View itemView) {
            super(itemView);
            mTitleView = (TextView)itemView.findViewById(R.id.child_text);
            mImageView = (ImageView)itemView.findViewById(R.id.child_check);
        }

        public void bind(final String childId) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onChildItemSwitched(childId);
                    }
                }
            });
        }

    }

    public interface OnChildItemChanged {
        void onChildItemSwitched(String childId);
    }
}
