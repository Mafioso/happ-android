package com.happ.controllers;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.happ.R;
import com.happ.models.Interest;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by iztiev on 8/4/16.
 */
public class InterestsListAdapter extends RecyclerView.Adapter<InterestsListAdapter.InterestsListViewHolder> implements INameableAdapter {
    private List<Interest> mInterests;
    private ArrayList<String> selectedInterests;
    private String parentId;
    private OnInterestClickedListener interestSelectedListener;
    private boolean selectSingle = false;
    private ArrayList<String> userInterestIds;

    private ArrayList<String> expandedInterests;

    private HashMap<String, InterestsListAdapter> expandedInterestAdapters;

    private final Context context;
    private OnInterestsSelectListener listener;
    private boolean isChild = false;



    public InterestsListAdapter(Context context, List<Interest> interests) {
        this.context = context;
        this.mInterests = interests;
        this.selectedInterests = new ArrayList<>();
        this.expandedInterests = new ArrayList<>();
        this.expandedInterestAdapters = new HashMap<>();
    }

    public void setSelectSingle(boolean selectSingle) {
        this.selectSingle = selectSingle;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public void setIsChild(boolean val) {
        isChild = val;
        notifyDataSetChanged();
    }

    public void setUserAcivityIds(ArrayList<String> interestIds) {
        userInterestIds = interestIds;
        updateSelectedInterests();
    }

    private void updateSelectedInterests() {
        if (userInterestIds != null) {
            for (int i=0; i<userInterestIds.size(); i++) {
                boolean notInSelectedInterests = true;
                for (int j = 0; j<selectedInterests.size(); j++) {
                    if (userInterestIds.get(i).equals(selectedInterests.get(j))) {
                        notInSelectedInterests = false;
                        break;
                    }
                }
                if (notInSelectedInterests) {
                    selectedInterests.add(userInterestIds.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }

    public void clearSelectedInterests() {
        this.selectedInterests.clear();
        notifyDataSetChanged();
    }

    public void setOnInterestsSelectListener(OnInterestsSelectListener listener) {
        this.listener = listener;
    }

    public void setOnItemSelectedListener(OnInterestClickedListener listener) {
        this.interestSelectedListener = listener;
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

    protected void removeChildrenFromSelectedInterests(Interest interest) {
        ArrayList<Interest> children = interest.getChildren();
        for (int i=0; i<children.size();i++) {
            if (selectedInterests.indexOf(children.get(i).getId()) >= 0) {
                selectedInterests.remove(children.get(i).getId());
            }
        }
    }

    @Override
    public void onBindViewHolder(InterestsListViewHolder holder, int position) {
        final Interest interest = mInterests.get(position);
        holder.mInterestTitle.setText(interest.getTitle());
        String id = interest.getId();

        if (this.selectSingle) {
            holder.mCheckBox.setVisibility(View.GONE);
        }

        if (selectedInterests.indexOf(id) >= 0) {
            removeChildrenFromSelectedInterests(interest);
            if (expandedInterestAdapters.get(interest.getId()) != null) {
                expandedInterestAdapters.get(interest.getId()).clearSelectedInterests();
            }
            if (!holder.mCheckBox.isChecked()) {
                holder.mCheckBox.toggle();
            }
        } else {
            if (holder.mCheckBox.isChecked()) holder.mCheckBox.toggle();
        }

        if (expandedInterests.indexOf(id) >= 0) {
            holder.mExpandChildrenButton.setImageDrawable(this.context.getResources().getDrawable(R.drawable.ic_chevron_up));
            if (!expandedInterestAdapters.containsKey(id)) {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<Interest> children = realm.where(Interest.class).equalTo("parentId",id).findAll();
                List<Interest> childrenList = realm.copyFromRealm(children);
                realm.close();
                InterestsListAdapter ila = new InterestsListAdapter(this.context, childrenList);
                ila.setIsChild(true);
                ila.setParentId(interest.getId());
                ila.setSelectSingle(this.selectSingle);
                if (userInterestIds != null) {
                    ila.setUserAcivityIds(userInterestIds);
                }

                if (this.selectSingle) {
                    ila.setOnItemSelectedListener(this.interestSelectedListener);
                } else {
                    ila.setOnInterestsSelectListener(new OnInterestsSelectListener() {
                        @Override
                        public void onInterestsSelected(ArrayList<String> selectedChildren, String parentId) {
                            removeChildrenFromSelectedInterests(interest);
                            if (selectedChildren.size() > 0) {
                                if (selectedInterests.indexOf(parentId) >= 0) {
                                    selectedInterests.remove(parentId);
                                }
                                selectedInterests.addAll(selectedChildren);
                            }
                            notifyDataSetChanged();
                        }
                    });
                }
                expandedInterestAdapters.put(id, ila);
            }
            holder.mChildrenRecyclerView.setAdapter(expandedInterestAdapters.get(id));
            holder.mChildrenRecyclerView.setVisibility(View.VISIBLE);
        } else {
            holder.mChildrenRecyclerView.setAdapter(null);
            holder.mExpandChildrenButton.setImageDrawable(this.context.getResources().getDrawable(R.drawable.ic_chevron_down));
            holder.mChildrenRecyclerView.setVisibility(View.GONE);
        }

        if (this.isChild) {
            holder.mExpandChildrenButton.setVisibility(View.GONE);
        } else if (interest.hasChildren()) {
            holder.mExpandChildrenButton.setVisibility(View.VISIBLE);
            holder.mExpandChildrenButton.setEnabled(true);
        } else {
            holder.mExpandChildrenButton.setVisibility(View.INVISIBLE);
            holder.mExpandChildrenButton.setEnabled(false);
        }
        holder.bind(interest.getId(), listener);
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
        private RecyclerView mChildrenRecyclerView;

        public InterestsListViewHolder(View itemView) {
            super(itemView);
            mInterestTitle = (TextView) itemView.findViewById(R.id.interest_title);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.interest_checkbox);
            mExpandChildrenButton = (ImageButton) itemView.findViewById(R.id.interest_show_children);
            mChildrenRecyclerView = (RecyclerView) itemView.findViewById(R.id.interest_children_list);
            mChildrenRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mChildrenRecyclerView.setVisibility(View.GONE);
        }

        public void bind(final String interestId, final OnInterestsSelectListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeState(interestId, listener);
                }
            });

            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeState(interestId, listener);
                }
            });

            mExpandChildrenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandedInterests.indexOf(interestId) >= 0) {
                        expandedInterests.remove(interestId);
                    } else {
                        expandedInterests.add(interestId);
                    }
                    notifyDataSetChanged();
                }
            });
        }

        private void changeState(String interestId, final OnInterestsSelectListener listener) {
            if (selectedInterests.indexOf(interestId) >= 0) {
                selectedInterests.remove(interestId);
            } else {
                selectedInterests.add(interestId);
            }
            if (listener != null) {
                listener.onInterestsSelected(selectedInterests, parentId);
            }
            if (interestSelectedListener != null) {
                Realm realm = Realm.getDefaultInstance();
                Interest interest = realm.where(Interest.class).equalTo("id", interestId).findFirst();
                if (interest != null) interest = realm.copyFromRealm(interest);
                realm.close();
                interestSelectedListener.onInterestSelected(interest);
            }
            notifyDataSetChanged();
        }


    }

    public interface OnInterestsSelectListener {
        void onInterestsSelected(ArrayList<String> selectedChildren, String parentId);
    }

    public interface OnInterestClickedListener {
        void onInterestSelected(Interest interest);
    }
}
