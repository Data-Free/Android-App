package com.android.bear.datafree;

/**
 * Created by bear on 9/2/17.
 */

public class ChatMessage {
    private int id;
    private boolean isMe;
    private String message;
    private Long userId;

    ChatMessage(){}

    ChatMessage(String newMessage, int newID, boolean newOrientation) {
        message = newMessage;
        id = newID;
        isMe = newOrientation;
    }

    public long getId() {
        return id;
    }
    void setId(int id) {
        this.id = id;
    }
    boolean getIsme() {
        return isMe;
    }
    void setMe(boolean isMe) {
        this.isMe = isMe;
    }
    String getMessage() {
        return message;
    }
    void setMessage(String message) {
        this.message = message;
    }
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
