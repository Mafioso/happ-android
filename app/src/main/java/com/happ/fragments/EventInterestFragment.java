package com.happ.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.happ.App;
import com.happ.R;
import com.happ.controllers.InterestsListAdapter;
import com.happ.models.Interest;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;
import com.turingtechnologies.materialscrollbar.TouchScrollBar;

import java.util.ArrayList;

public class EventInterestFragment extends DialogFragment {
    private RecyclerView mInterestsRecyclerView;
    private ArrayList<Interest> interests;
    private InterestsListAdapter mInterestsListAdapter;
    private TouchScrollBar mInterestsScrollBar;
    private OnInterestSelectListener listener;

    public EventInterestFragment() {

    }

    public static EventInterestFragment newInstance() {
        EventInterestFragment fragment = new EventInterestFragment();
        Bundle args = new Bundle();
        args.putString("title", App.getContext().getString(R.string.select_interest_title));
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnInterestSelectListener(OnInterestSelectListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        interests = new ArrayList<>();
        String[] a = {"A ", "B ", "C ", "D ", "E ", "F ", "G ", "H ", "I ", "J " };
        for (int i=1;i<=20;i++) {
            interests.add(new Interest());
            interests.get(i-1).setTitle(a[(i-1)/2] + "Interest "+i);
            interests.get(i-1).setId(i);
        }

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.interest_list_view, null);

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.select_interest_title)
                .setView(contentView)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();


        mInterestsRecyclerView = (RecyclerView)contentView.findViewById(R.id.interests_recycler_view);


        LinearLayoutManager ilm = new LinearLayoutManager(getContext());
        mInterestsRecyclerView.setLayoutManager(ilm);

        mInterestsListAdapter = new InterestsListAdapter(getContext(), interests);
        mInterestsListAdapter.setOnItemClickListener(new InterestsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Interest interest) {
                Log.d(">>CLICK<<","Callback in EventInterestFragment");
                if (listener != null) {
                    listener.onInterestSelected(interest);
                }
                dialog.dismiss();
            }
        });
        mInterestsRecyclerView.setAdapter(mInterestsListAdapter);

        mInterestsScrollBar = (TouchScrollBar)contentView.findViewById(R.id.interests_scroll_bar);
        mInterestsScrollBar.setHandleColourRes(R.color.colorAccent);
        mInterestsScrollBar.addIndicator(new AlphabetIndicator(getContext()),true);

        return dialog;
    }

    public interface OnInterestSelectListener {
        public void onInterestSelected(Interest interest);
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
////        return super.onCreateView(inflater, container, savedInstanceState);
//        interests = new ArrayList<>();
//        String[] a = {"A ", "B ", "C ", "D ", "E ", "F ", "G ", "H ", "I ", "J " };
//        for (int i=1;i<=20;i++) {
//            interests.add(new Interest());
//            interests.get(i-1).setTitle(a[(i-1)/2] + "Interest "+i);
//            interests.get(i-1).setId(i);
//        }
////        getDialog().setTitle(R.string.select_interest_title);
////        getDialog().setCancelable(true);
////        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
////        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        return inflater.inflate(R.layout.interest_list_view, container);
//    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mInterestsRecyclerView = (RecyclerView)view.findViewById(R.id.interests_recycler_view);


        LinearLayoutManager ilm = new LinearLayoutManager(getContext());
        mInterestsRecyclerView.setLayoutManager(ilm);

        mInterestsListAdapter = new InterestsListAdapter(getContext(), interests);
        mInterestsRecyclerView.setAdapter(mInterestsListAdapter);

        mInterestsScrollBar = (TouchScrollBar)view.findViewById(R.id.interests_scroll_bar);
        mInterestsScrollBar.setHandleColourRes(R.color.colorAccent);
        mInterestsScrollBar.addIndicator(new AlphabetIndicator(getContext()),true);
    }

    //    protected ArrayList<Interest> interests;
//    protected RecyclerView interestsListView;
//    protected LinearLayoutManager interestsListLayoutManager;
//    private BroadcastReceiver interestsRequestDoneReceiver;
//    private int interestsFeedPageSize;
//
//    private boolean loading = true;
//    private int firstVisibleItem, visibleItemCount, totalItemCount;
//    private int previousTotal = 0;
//    private int visibleThreshold;
//    private View form=null;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        final View view = inflater.inflate(R.layout.feeds_fragment, container, false);
//        final Activity activity = getActivity();
//
//        interestsListView = (RecyclerView)view.findViewById(R.id.interests_list_view);
//        interestsListLayoutManager = new LinearLayoutManager(activity);
//        interestsListView.setLayoutManager(interestsListLayoutManager);
//        interests = new ArrayList<>();
//
//        InterestsListAdapter ela = new InterestsListAdapter(activity, interests);
//        interestsListView.setAdapter(ela);
//
//        interestsRequestDoneReceiver = createInterestsRequestDoneReceiver();
//        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(interestsRequestDoneReceiver, new IntentFilter(BroadcastIntents.EVENTS_REQUEST_OK));
//        APIService.getInterests();
//
////        form= getActivity().getLayoutInflater()
////                .inflate(R.layout.activity_event_interests, null);
////        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
//
////        return(builder.setTitle("Select Interest").setView(form)
////                .setPositiveButton(android.R.string.ok, this)
////                .setNegativeButton(android.R.string.cancel, null).create());
//        return view;
//    }
//
//    private BroadcastReceiver createInterestsRequestDoneReceiver() {
//        return new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                updateInterestsList();
//            }
//        };
//    }
//
//    protected void updateInterestsList() {
//        Realm realm = Realm.getDefaultInstance();
//        RealmResults<Interest> eventRealmResults = realm.where(Interest.class).findAllSorted("startDate", Sort.ASCENDING);
//        interests = (ArrayList<Interest>)realm.copyFromRealm(eventRealmResults.subList(0, eventRealmResults.size()));
//        ((InterestsListAdapter)interestsListView.getAdapter()).updateData(interests);
//        realm.close();
//    }
//
//    @Override
//    public void onDestroy() {
//        if (interestsRequestDoneReceiver != null) {
//            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(interestsRequestDoneReceiver);
//        }
//        super.onDestroy();
//    }


//    @Override
//    public void onClick(DialogInterface dialog, int which) {
//
//        EditText loginBox=(EditText)form.findViewById(R.id.login);
//        String login = loginBox.getText().toString();
//
////        Interest interest;
////        ((EventCreateActivity)getActivity()).eventInterest = interest;
//
//        EditText loginText = (EditText) getActivity().findViewById(R.id.input_interest);
//        loginText.setText(login);
//    }
//    @Override
//    public void onDismiss(DialogInterface unused) {
//        super.onDismiss(unused);
//    }
//    @Override
//    public void onCancel(DialogInterface unused) {
//        super.onCancel(unused);
//    }
}