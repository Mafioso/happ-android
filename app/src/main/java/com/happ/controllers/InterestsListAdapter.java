package com.happ.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.happ.R;
import com.happ.models.Event;
import com.happ.models.Interest;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.ArrayList;

import retrofit2.http.POST;

/**
 * Created by iztiev on 8/4/16.
 */
public class InterestsListAdapter extends RecyclerView.Adapter<InterestsListAdapter.InterestsListViewHolder> implements INameableAdapter {
    private ArrayList<Interest> mInterests;
    private ArrayList<String> selectedInterests;
    private final Context context;
    private OnItemClickListener listener;



    public InterestsListAdapter(Context context, ArrayList<Interest> interests) {
        this.context = context;
        this.mInterests = interests;
        this.selectedInterests = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ArrayList<String> getSelectedInterests() {
        return this.selectedInterests;
    }

    public void updateData(ArrayList<Interest> interests) {
        mInterests = interests;
        Log.d("AAAAA", String.valueOf(mInterests.size()));
        notifyDataSetChanged();
    }

    @Override
    public InterestsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_list_item, parent, false);
        return new InterestsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InterestsListViewHolder holder, int position) {
        Interest interest = mInterests.get(position);
        holder.mInterestTitle.setText(interest.getTitle());
        String id = interest.getId();
        if (selectedInterests.indexOf(id) >= 0) {
            if (!holder.mCheckBox.isChecked()) holder.mCheckBox.toggle();
        } else {
            if (holder.mCheckBox.isChecked()) holder.mCheckBox.toggle();
        }
        if (interest.hasChildren()) {
            holder.mExpandChildrenButton.setVisibility(View.VISIBLE);
            holder.mExpandChildrenButton.setEnabled(true);
        } else {
            holder.mExpandChildrenButton.setVisibility(View.INVISIBLE);
            holder.mExpandChildrenButton.setEnabled(false);
        }
        holder.bind(interest.getId(), null);
    }

    @Override
    public int getItemCount() {
        return mInterests.size();
    }

    @Override
    public Character getCharacterForElement(int element) {
        return mInterests.get(element).getTitle().charAt(0);
    }

    public class InterestsListViewHolder extends RecyclerView.ViewHolder {
        public TextView mInterestTitle;
        public CheckBox mCheckBox;
        public ImageButton mExpandChildrenButton;

        public InterestsListViewHolder(View itemView) {
            super(itemView);
            mInterestTitle = (TextView) itemView.findViewById(R.id.interest_title);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.interest_checkbox);
            mExpandChildrenButton = (ImageButton) itemView.findViewById(R.id.interest_show_children);
        }

        public void bind(final String interestId, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeState(interestId);
                }
            });

            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeState(interestId);
                }
            });
        }

        private void changeState(String interestId) {
            if (selectedInterests.indexOf(interestId) >= 0) {
                selectedInterests.remove(interestId);
            } else {
                selectedInterests.add(interestId);
            }
            notifyDataSetChanged();
        }


    }

    public interface OnItemClickListener {
        void onItemClick(Interest interest);
    }
}
