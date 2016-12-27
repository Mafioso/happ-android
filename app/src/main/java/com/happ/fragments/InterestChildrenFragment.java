package com.happ.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.happ.R;
import com.happ.adapters.ChildInterestsAdapter;
import com.happ.adapters.InterestsListAdapter;
import com.happ.models.Interest;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class InterestChildrenFragment extends Fragment {
    private static final String ARG_INTEREST = "interest_id";
    private static final String ARG_CHILDREN = "children";
    private static final String ARG_SEL_CHILDREN = "selected_children";
    private static final String ARG_PARENTS = "parent_interests";
    private static final String ARG_TOP = "top";
    private static final String ARG_HEIGHT = "height";
    private static final String ARG_SINGLE = "single";


    private String mInterest;
    private ArrayList<String> mChildren;
    private ArrayList<String> mSelectedChildren;
    private ArrayList<String> mParents;
    private int mTop;
    private int mHeight;

    private OnInterestChildrenInteractionListener mListener;

    private RelativeLayout mContainer;
    private RelativeLayout mParentsContainer;

    private RecyclerView mParentsView;
    private InterestsListAdapter mParentsAdapter;

    private RecyclerView mChildrenView;
    private ChildInterestsAdapter mChildrenAdapter;
    private Button mCloseButton;
    private boolean isSingle;

    public InterestChildrenFragment() {
    }


    public static InterestChildrenFragment newInstance(String interestId,
                                                       ArrayList<String> children,
                                                       ArrayList<String> parents,
                                                       ArrayList<String> selectedChildren,
                                                       int top,
                                                       int height,
                                                       boolean single) {
        InterestChildrenFragment fragment = new InterestChildrenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INTEREST, interestId);
        args.putStringArrayList(ARG_CHILDREN, children);
        args.putStringArrayList(ARG_PARENTS, parents);
        args.putStringArrayList(ARG_SEL_CHILDREN, selectedChildren);
        args.putInt(ARG_TOP, top);
        args.putInt(ARG_HEIGHT, height);
        args.putBoolean(ARG_SINGLE, single);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInterest = getArguments().getString(ARG_INTEREST);
            mChildren = getArguments().getStringArrayList(ARG_CHILDREN);
            mParents = getArguments().getStringArrayList(ARG_PARENTS);
            mSelectedChildren = getArguments().getStringArrayList(ARG_SEL_CHILDREN);
            mTop = getArguments().getInt(ARG_TOP);
            mHeight = getArguments().getInt(ARG_HEIGHT);
            isSingle = getArguments().getBoolean(ARG_SINGLE, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_interest_children, container, false);

        mContainer = (RelativeLayout)v.findViewById(R.id.selected_row_container);
        mParentsContainer = (RelativeLayout)v.findViewById(R.id.selected_row);
        mCloseButton = (Button)v.findViewById(R.id.close_child_interests);

        mParentsView = (RecyclerView)v.findViewById(R.id.parents_list);
        mChildrenView = (RecyclerView)v.findViewById(R.id.children_list);


        RelativeLayout.LayoutParams parentsParams = (RelativeLayout.LayoutParams) mParentsContainer.getLayoutParams();
        parentsParams.height = mHeight;
        parentsParams.setMargins(0, mTop, 0, 0);
        mParentsContainer.setLayoutParams(parentsParams);
        GridLayoutManager glm = new GridLayoutManager(this.getActivity(), 3);


        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Interest> interest_query = realm.where(Interest.class)
                .beginGroup();
        for (int i=0; i<mParents.size(); i++) {
            interest_query.equalTo("id", mParents.get(i));
            if (i+1 < mParents.size()) interest_query.or();
        }
        interest_query.endGroup();
        RealmResults<Interest> interest_results = interest_query.findAll();
        ArrayList<Interest> parents = new ArrayList<>();
        if (interest_results != null) parents = (ArrayList<Interest>) realm.copyFromRealm(interest_results);


        mParentsAdapter = new InterestsListAdapter(this.getActivity(), parents, false);
        mParentsAdapter.setActiveInterestId(mInterest);

        mParentsView.setHasFixedSize(true);
        mParentsView.setLayoutManager(glm);
        mParentsView.setAdapter(mParentsAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        mChildrenView.setLayoutManager(llm);

        ArrayList<Interest> children = new ArrayList<>();
        RealmResults<Interest> children_results = realm.where(Interest.class).equalTo("id", mInterest).findAll();
        if (children_results != null) children = (ArrayList<Interest>) realm.copyFromRealm(children_results);
        realm.close();

        mChildrenAdapter = new ChildInterestsAdapter(this.getActivity(), children, mSelectedChildren);
        mChildrenAdapter.setSingle(isSingle);
        mChildrenView.setAdapter(mChildrenAdapter);

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mChildrenAdapter.setOnChildItemChangedListener(new ChildInterestsAdapter.OnChildItemChanged() {
            @Override
            public void onChildItemSwitched(String childId) {
                if (isSingle) {
                    ArrayList<String> res = new ArrayList<String>();
                    res.add(childId);
                    if (mListener != null) {
                        mListener.onChildrenUpdated(mInterest, res);
                    }
                } else {
                    boolean isDeleted = false;
                    if (mSelectedChildren.size() > 0) {
                        for (int i = mSelectedChildren.size() - 1; i>=0; i--) {
                            if (mSelectedChildren.get(i).equals(childId)) {
                                mSelectedChildren.remove(i);
                                isDeleted = true;
                                break;
                            }
                        }
                    }
                    if (!isDeleted) mSelectedChildren.add(childId);
                    mChildrenAdapter.updateSelectedInterests(mSelectedChildren);
                    if (mListener != null) {
                        mListener.onChildrenUpdated(mInterest, mSelectedChildren);
                    }
//            mListener.onFragmentInteraction(uri);
                }
            }
        });

        if (v.getViewTreeObserver().isAlive()) {
            v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    v.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    final ViewGroup sceneRoot = mContainer;
                    try {
                        wait(1000);
                    } catch (Exception ex) {}

                    TransitionSet set = new TransitionSet();
                    set.addTransition(new ChangeBounds());
                                    set.setOrdering(TransitionSet.ORDERING_TOGETHER);
                    set.setDuration(50000);
                    set.setInterpolator(PathInterpolatorCompat.create(0.4f, 0.0f, 0.6f, 1.0f));
                    TransitionManager.beginDelayedTransition(sceneRoot, set);

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mParentsContainer.getLayoutParams();
                    params.setMargins(0, 0, 0, 0);
                    sceneRoot.setBackgroundColor(getResources().getColor(R.color.background));
                    mParentsContainer.setLayoutParams(params);
                }
            });
        }

        return v;
    }



    @Override
    public void onStart() {
        super.onStart();
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnInterestChildrenInteractionListener) {
            mListener = (OnInterestChildrenInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInterestChildrenInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnInterestChildrenInteractionListener {
        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
        void onChildrenUpdated(String parentId, ArrayList<String> childrenIds);
    }
}
