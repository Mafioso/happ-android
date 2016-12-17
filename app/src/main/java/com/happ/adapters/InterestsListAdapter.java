package com.happ.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.happ.App;
import com.happ.R;
import com.happ.models.Interest;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

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
    private String activeInterestId;

    private HashMap<String, InterestsListAdapter> expandedInterestAdapters;
    private HashMap<String, ArrayList<String>> selectedInterestIds;

    private final Context context;
    private OnInterestsSelectListener listener;
    private int itemWidth;
    private int margin;
    private int middleItemWidth;
    private boolean parentsView;


    public interface OnInterestsSelectListener {
        void onParentInterestChanged(String interestId);
        void onInterestExpandRequested(String interestId, int position, int top, int height);
    }

    public interface OnInterestClickedListener {
        void onInterestSelected(Interest interest);
    }

    public void setOnInterestsSelectListener(OnInterestsSelectListener listener) {
        this.listener = listener;
    }

    public void setOnItemSelectedListener(OnInterestClickedListener listener) {
        this.interestSelectedListener = listener;
    }

    public void setSelectSingle(boolean selectSingle) {
        this.selectSingle = selectSingle;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setUserAcivityIds(ArrayList<String> interestIds) {
        userInterestIds = interestIds;
        updateSelectedInterests();
    }

    public void setActiveInterestId(String interestId) {
        activeInterestId = interestId;
        notifyDataSetChanged();
    }

    public void updateSelectedInterests(HashMap<String, ArrayList<String>> selectedInterestIds) {
        this.selectedInterestIds = selectedInterestIds;
        notifyDataSetChanged();
    }


    public InterestsListAdapter(Context context, List<Interest> interests, boolean parentsView) {
        this.context = context;
        this.mInterests = interests;
        this.parentsView = parentsView;
        this.selectedInterests = new ArrayList<>();
        this.expandedInterestAdapters = new HashMap<>();
        this.selectedInterestIds = new HashMap<>();
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        margin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, ((Activity) context).getResources().getDisplayMetrics());
        itemWidth = (width - (2*margin))/3;
        middleItemWidth = itemWidth + (width - (itemWidth*3+ 2*margin));
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
    public void onBindViewHolder(final InterestsListViewHolder holder, final int position) {

        final Interest interest = mInterests.get(position);
        int row = position/3;

        RecyclerView.LayoutParams containerParams = (RecyclerView.LayoutParams) holder.mInterestContainer.getLayoutParams();
        RelativeLayout.LayoutParams ivParams = (RelativeLayout.LayoutParams) holder.mImageContainer.getLayoutParams();
        RelativeLayout.LayoutParams bgParams = (RelativeLayout.LayoutParams) holder.mInterestBackground.getLayoutParams();

        ivParams.height = itemWidth;
        ivParams.width = itemWidth;
        bgParams.width = itemWidth;
        containerParams.setMargins(0,0,0,margin);

        int tMiddleWidth = itemWidth;
        if (position != row*3) {
            if (position == row*3+1) tMiddleWidth = middleItemWidth;
            ivParams.width = tMiddleWidth;
            bgParams.width = tMiddleWidth;
            containerParams.setMargins(margin,0,0,margin);
//            bgParams.setMargins(margin,0,0,0);
        }

        holder.mImageContainer.setLayoutParams(ivParams);
        holder.mInterestBackground.setLayoutParams(bgParams);
        holder.mInterestContainer.setLayoutParams(containerParams);

        holder.mInterestTitle.setText(interest.getTitle());

        final InterestsListViewHolder itemHolder = (InterestsListViewHolder)holder;

        if (activeInterestId != null) {
            if (!interest.getId().equals(activeInterestId)) {
                holder.itemView.setAlpha(0.2f);
            }
        }

//        if(interest.getUrl().length() > 0 || interest.getUrl() != null ){
//            final String url = interest.getUrl();

        boolean selected = false;
        if (selectedInterestIds.get(interest.getId()) != null) {
            if (selectedInterestIds.get(interest.getId()).size() > 0) {
                int selectedCount = selectedInterestIds.get(interest.getId()).size();
                int total = interest.getChildren().size();
                if (selectedCount < total) {
                    holder.mCheckImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_white));
                } else {
                    holder.mCheckImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_all_white));
                }
                String count = "" + selectedCount + "/" + total;
                holder.mCountText.setText(count);

                holder.mCheckImage.setVisibility(View.VISIBLE);
                holder.mCountText.setVisibility(View.VISIBLE);
            } else {
                holder.mCheckImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_all_white));
                holder.mCheckImage.setVisibility(View.VISIBLE);
                holder.mCountText.setVisibility(View.GONE);
            }
            selected = true;
        } else {
            holder.mCheckImage.setVisibility(View.GONE);
            holder.mCountText.setVisibility(View.GONE);
        }
        final boolean isSelected = selected;

        if (interest.getImage() != null) {
            final String url = interest.getImage().getUrl();
            holder.mInterestImageView.setImageDrawable(null);
            try {
                ViewTreeObserver viewTreeObserver = holder.mInterestImageView.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            holder.mInterestImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewWidth = holder.mInterestImageView.getWidth();
                            int viewHeight = holder.mInterestImageView.getHeight();
                            if (isSelected) {
                                Glide.with(App.getContext())
                                        .load(url)
                                        .asBitmap()
                                        .transform(new BlurTransformation(context), new CropSquareTransformation(context))
//                                        .override(viewWidth, viewHeight)
//                                        .centerCrop()
                                        .into(holder.mInterestImageView);
                            } else {
                                Glide.with(App.getContext())
                                        .load(url)
                                        .asBitmap()
                                        .override(viewWidth, viewHeight)
                                        .centerCrop()
                                        .into(holder.mInterestImageView);
                            }
                        }
                    });
                }
            } catch (Exception ex) {
                Log.e("INTERESTS", ex.getLocalizedMessage());
            }

        } else {
            Glide.clear(holder.mInterestImageView);
        }
        holder.mInterestBackground.setBackgroundColor(Color.parseColor(interest.getColor()));

        String id = interest.getId();


        holder.mChildrenRecyclerView.setAdapter(null);
        holder.mChildrenRecyclerView.setVisibility(View.GONE);

        if (this.parentsView) {
            if (interest.getChildren().size() > 0) {
                holder.mSelectInterestsButton.setVisibility(View.VISIBLE);
            } else {
                holder.mSelectInterestsButton.setVisibility(View.GONE);
            }
            holder.bind(interest.getId(), listener, position);
        }
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
        private RecyclerView mChildrenRecyclerView;
        private ImageView mInterestImageView;
        private RelativeLayout mInterestBackground;
        private RelativeLayout mInterestContainer;
        private RelativeLayout mImageContainer;
        private String url;
        private RelativeLayout mInactiveOverlay;

        private TextView mCountText;
        private ImageView mCheckImage;
        private ImageButton mSelectInterestsButton;

        public InterestsListViewHolder(View itemView) {
            super(itemView);
            mInterestTitle = (TextView) itemView.findViewById(R.id.interest_title);
            mInterestImageView = (ImageView) itemView.findViewById(R.id.interest_imageview);
            mInterestBackground = (RelativeLayout) itemView.findViewById(R.id.interest_bg);
            mInterestContainer = (RelativeLayout) itemView.findViewById(R.id.interest_container);
            mImageContainer = (RelativeLayout) itemView.findViewById(R.id.interest_image_container) ;
            mInactiveOverlay= (RelativeLayout) itemView.findViewById(R.id.inactive_overlay);
            mSelectInterestsButton = (ImageButton) itemView.findViewById(R.id.show_children_button);

            mCountText = (TextView) itemView.findViewById(R.id.selected_count_text) ;
            mCheckImage = (ImageView) itemView.findViewById(R.id.selected_check);

            mChildrenRecyclerView = (RecyclerView) itemView.findViewById(R.id.interest_children_list);
            mChildrenRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mChildrenRecyclerView.setVisibility(View.GONE);
            mSelectInterestsButton.setVisibility(View.GONE);
        }

        public void bind(final String interestId, final OnInterestsSelectListener listener, final int position) {

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Realm realm = Realm.getDefaultInstance();
                    if (realm.where(Interest.class).equalTo("parentId", interestId).findAll().size() > 0) {
                        int[] location = new int[2];
                        itemView.getLocationInWindow(location);
                        listener.onInterestExpandRequested(interestId, position, location[1], itemView.getHeight());
                    }
                    realm.close();
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeState(interestId, listener);
                }
            });

            mSelectInterestsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Realm realm = Realm.getDefaultInstance();
                    if (realm.where(Interest.class).equalTo("parentId", interestId).findAll().size() > 0) {
                        int[] location = new int[2];
                        itemView.getLocationInWindow(location);
                        listener.onInterestExpandRequested(interestId, position, location[1], itemView.getHeight());
                    }
                    realm.close();
                }
            });
        }

        private void changeState(String interestId, final OnInterestsSelectListener listener) {
            if (listener != null) {
                listener.onParentInterestChanged(interestId);
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
}
