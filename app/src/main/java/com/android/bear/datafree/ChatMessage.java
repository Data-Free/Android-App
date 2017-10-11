package com.android.bear.datafree;

/**
 * Created by bear on 9/2/17.
 */

public class ChatMessage {
    private int id;
    private boolean onRight;
    private String message;
    private Long userId;

    ChatMessage(){}

    ChatMessage(String newMessage, int newID, boolean newOrientation) {
        message = newMessage;
        id = newID;
        onRight = newOrientation;
    }

    public long getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    boolean getOrientation() {
        return onRight;
    }

    void setOrientation(boolean orientation) {
        onRight = orientation;
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
