package com.android.bear.datafree;

/**
 * Created by bear on 9/2/17.
 */

public class ChatMessage {
    private long id;
    private boolean isMe;
    private String message;
    private Long userId;
    private String dateTime;

    public long getId() {
        return id;
    }
    void setId(long id) {
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

    String getDate() {
        return dateTime;
    }

    void setDate(String dateTime) {
        this.dateTime = dateTime;
    }
}
