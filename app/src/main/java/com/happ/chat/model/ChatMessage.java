package com.happ.chat.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dante on 1/25/17.
 */
public class ChatMessage extends RealmObject {

    @PrimaryKey
    private String id;
    private String chatDialogId;
    private String message;
    private long dateSend;
    private int senderId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatDialogId() {
        return chatDialogId;
    }

    public void setChatDialogId(String chatDialogId) {
        this.chatDialogId = chatDialogId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDateSend() {
        return dateSend;
    }

    public void setDateSend(long dateSend) {
        this.dateSend = dateSend;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
}
