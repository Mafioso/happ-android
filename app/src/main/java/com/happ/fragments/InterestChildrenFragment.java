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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnInterestChildrenInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InterestChildrenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InterestChildrenFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_INTEREST = "interest_id";
    private static final String ARG_CHILDREN = "children";
    private static final String ARG_PARENTS = "parent_interests";
    private static final String ARG_TOP = "top";
    private static final String ARG_HEIGHT = "height";

    // TODO: Rename and change types of parameters
    private String mInterest;
    private ArrayList<String> mChildren;
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

    public InterestChildrenFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static InterestChildrenFragment newInstance(String interestId,
                                                       ArrayList<String> children,
                                                       ArrayList<String> parents,
                                                       int top,
                                                       int height) {
        InterestChildrenFragment fragment = new InterestChildrenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INTEREST, interestId);
        args.putStringArrayList(ARG_CHILDREN, children);
        args.putStringArrayList(ARG_PARENTS, parents);
        args.putInt(ARG_TOP, top);
        args.putInt(ARG_HEIGHT, height);

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
            mTop = getArguments().getInt(ARG_TOP);
            mHeight = getArguments().getInt(ARG_HEIGHT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_interest_children, container, false);

        mContainer = (RelativeLayout)v.findViewById(R.id.selected_row_container);
        mParentsContainer = (RelativeLayout)v.findViewById(R.id.selected_row);

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
        mParentsAdapter = new InterestsListAdapter(this.getActivity(), parents);
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

        mChildrenAdapter = new ChildInterestsAdapter(this.getActivity(), children);
        mChildrenView.setAdapter(mChildrenAdapter);

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



//        final ViewGroup sceneRoot = mContainer;
//
//        TransitionSet set = new TransitionSet();
//        set.addTransition(new ChangeBounds());
//        //                set.setOrdering(TransitionSet.ORDERING_TOGETHER);
//        set.setDuration(500);
//        set.setInterpolator(PathInterpolatorCompat.create(0.4f, 0.0f, 0.6f, 1.0f));
//        TransitionManager.beginDelayedTransition(sceneRoot, set);
//
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mParentsContainer.getLayoutParams();
//        params.setMargins(0, 0, 0, 0);
//        sceneRoot.setBackgroundColor(getResources().getColor(R.color.background));
//        mParentsContainer.setLayoutParams(params);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(Uri uri);
    }
}
