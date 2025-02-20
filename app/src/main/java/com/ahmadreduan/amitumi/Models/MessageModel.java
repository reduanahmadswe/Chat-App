package com.ahmadreduan.amitumi.Models;

// MessageModel class banano hoise jehetu Firebase theke data store o retrieve korar jonno
public class MessageModel {

    String uId , message,messageId;
    Long timestamp;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    // Constructor: jodi user ID, timestamp o message dewa hoy
    public MessageModel(String uId, Long timestamp, String message) {
        this.uId = uId;
        this.timestamp = timestamp;
        this.message = message;
    }

    // Constructor: jodi user ID o message thake, but timestamp na thake
    public MessageModel(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }


    // Empty Constructor: Firebase er jonne lage
    public MessageModel() {
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
