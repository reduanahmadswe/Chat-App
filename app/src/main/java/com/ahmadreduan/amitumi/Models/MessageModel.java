package com.ahmadreduan.amitumi.Models;

public class MessageModel {

    String uId , message;
    Long timestamp;

    public MessageModel(String uId, Long timestamp, String message) {
        this.uId = uId;
        this.timestamp = timestamp;
        this.message = message;
    }

    public MessageModel(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }

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
