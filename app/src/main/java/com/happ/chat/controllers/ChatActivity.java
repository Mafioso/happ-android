package com.happ.chat.controllers;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.happ.App;
import com.happ.R;
import com.happ.chat.adapters.ChatAdapter;
import com.happ.chat.model.ChatMessage;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by dante on 1/25/17.
 */
public class ChatActivity extends AppCompatActivity {

    private EditText messageET;
    private Button sendBtn;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerViewChatMessages;
    private LinearLayoutManager llm;
    private ArrayList<ChatMessage> chatMessage;
    private ChatAdapter chatAdapter;
    private int senderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle(R.string.chat);

        QBChatService.setDebugEnabled(true); // enable chat logging
        QBChatService.setDefaultAutoSendPresenceInterval(60); //enable sending online status every 60 sec to keep connection alive

        QBChatService.ConfigurationBuilder chatServiceConfigurationBuilder = new QBChatService.ConfigurationBuilder();

        chatServiceConfigurationBuilder.setSocketTimeout(60); //Sets chat socket's read timeout in seconds
        chatServiceConfigurationBuilder.setKeepAlive(true); //Sets connection socket's keepAlive option.
        chatServiceConfigurationBuilder.setUseTls(true); //Sets the TLS security mode used when making the connection. By default TLS is disabled.
        QBChatService.setConfigurationBuilder(chatServiceConfigurationBuilder);

        final QBUser user = new QBUser("rustem", "qwerty123");

        QBAuth.createSession(user).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                user.setId(qbSession.getUserId());

                senderId = qbSession.getUserId();

                QBChatService.getInstance().login(user, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {

                        final QBChatDialog chatDialog = new QBChatDialog("58870a78a28f9a97e0000069");
                        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
                        messageGetBuilder.setLimit(500);
                        QBRestChatService.getDialogMessages(chatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                            @Override
                            public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {

                                Log.e("Message Size", "" + qbChatMessages.size());

                                for (int i = 0; i < qbChatMessages.size(); i++) {
                                    Log.d("Message id", " >>> " + qbChatMessages.get(i).getBody());
                                }
                                for (int i = 0; i < qbChatMessages.size(); i++) {
                                    ChatMessage chatMessage = new ChatMessage();
                                    chatMessage.setId(qbChatMessages.get(i).getId());
                                    chatMessage.setMessage(qbChatMessages.get(i).getBody());
                                    chatMessage.setChatDialogId(qbChatMessages.get(i).getDialogId());
                                    chatMessage.setSenderId(qbChatMessages.get(i).getSenderId());
                                    chatMessage.setDateSend(qbChatMessages.get(i).getDateSent());


                                    Realm realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    realm.copyToRealmOrUpdate(chatMessage);
                                    realm.commitTransaction();
                                    realm.close();
                                }

                                Realm realm = Realm.getDefaultInstance();
                                RealmResults<ChatMessage> chatDialogsRealmResults = realm.where(ChatMessage.class).findAll();
                                chatMessage = (ArrayList<ChatMessage>)realm.copyFromRealm(chatDialogsRealmResults);
//                                  ((ChatAdapter)mRecyclerViewChatMessages.getAdapter()).updateData(chatMessage);
                                realm.close();

                                mRecyclerViewChatMessages = (RecyclerView) findViewById(R.id.rv_chat_messages);
                                llm = new LinearLayoutManager(App.getContext());
                                mRecyclerViewChatMessages.setLayoutManager(llm);
                                chatAdapter = new ChatAdapter(App.getContext(), chatMessage, senderId);
                                mRecyclerViewChatMessages.setAdapter(chatAdapter);

                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Log.e("Error Exception", "" + e);
                            }
                        });

                    }

                    @Override
                    public void onError(QBResponseException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {
                e.printStackTrace();
            }
        });



        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_right_arrow_grey);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ChatActivity.this, "done", Toast.LENGTH_SHORT).show();
                }
            });
        }

        initControls();
    }


    private void initControls() {
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QBChatMessage msg = new QBChatMessage();
                msg.setBody(messageET.getText().toString());
                msg.setDialogId("58870a78a28f9a97e0000069");

                boolean sendToDialog = true; //set true for send this message to the chat or false for just create it.

                QBRestChatService.createMessage(msg, sendToDialog).performAsync(new QBEntityCallback<QBChatMessage>() {
                    @Override
                    public void onSuccess(QBChatMessage message, Bundle bundle) {
                        Log.e("CHAT ACTIVITY", "" + message.getBody());
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }
        });

    }

}
