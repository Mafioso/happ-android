package com.happ.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.happ.App;
import com.happ.R;
import com.happ.chat.adapters.DialogsAdapter;
import com.happ.chat.model.ChatDialogs;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by dante on 1/26/17.
 */
public class DialogsChatFeedFragment extends Fragment {

    private static String TAG = DialogsChatFeedFragment.class.getSimpleName();
    public static DialogsChatFeedFragment newInstance() {
        return new DialogsChatFeedFragment();
    }

    public DialogsChatFeedFragment() {
    }

    private RecyclerView mRecyclerViewChatDialogs;
    private DialogsAdapter dialogsAdapter;
    private LinearLayoutManager llm;
    private ArrayList<ChatDialogs> chatDialogs;
    private ProgressBar mProgressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.dialogs_chat_feed_fragment, container, false);
        final Activity activity = getActivity();

        mRecyclerViewChatDialogs = (RecyclerView) contentView.findViewById(R.id.rv_chat_dialogs);
        mProgressBar = (ProgressBar) contentView.findViewById(R.id.progress_dialogs);

        llm = new LinearLayoutManager(App.getContext());
        mRecyclerViewChatDialogs.setLayoutManager(llm);
        chatDialogs = new ArrayList<>();
        dialogsAdapter = new DialogsAdapter(App.getContext(), chatDialogs);
        mRecyclerViewChatDialogs.setAdapter(dialogsAdapter);

        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(50);

        QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(
            new QBEntityCallback<ArrayList<QBChatDialog>>() {
                @Override
                public void onSuccess(ArrayList<QBChatDialog> result, Bundle params) {
                    int totalEntries = params.getInt("total_entries");

                    for (int i = 0; i < totalEntries; i++) {
                        ChatDialogs chatDialogs = new ChatDialogs();
                        chatDialogs.setId(result.get(i).getDialogId());
                        chatDialogs.setName(result.get(i).getName());
                        chatDialogs.setCreatorId(result.get(i).getUserId());
                        chatDialogs.setLastMessageDateSent(result.get(i).getLastMessageDateSent());

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(chatDialogs);
                        realm.commitTransaction();
                        realm.close();
                    }

                    Realm realm = Realm.getDefaultInstance();
                    RealmResults<ChatDialogs> chatDialogsRealmResults = realm.where(ChatDialogs.class).findAll();
                    chatDialogs = (ArrayList<ChatDialogs>)realm.copyFromRealm(chatDialogsRealmResults);
                    ((DialogsAdapter)mRecyclerViewChatDialogs.getAdapter()).updateData(chatDialogs);
                    realm.close();

                }

                @Override
                public void onError(QBResponseException responseException) {
                    Log.e(TAG, " " + responseException);
                }
            });

        return contentView;
    }

}
