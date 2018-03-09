package com.example.ripan.map;

import java.util.*;

public class Message {
    //Unique identifier for message:
    private String messageID;

    //Internal processing:
    private MessageState state;

    //Content:
    private String userID;
    private String message;
    private Date date;
    private String location;

    public enum MessageState {
        Unknown,
        Received,
        UserGenerated,
        UserGeneratedSent,
        UserGeneratedFailedToSend
    }

    public Message() {
        messageID = "";
        state = MessageState.Unknown;
        userID = "";
        message = "";
        date = new Date();
        location = "0, 0";
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public MessageState getState() {
        return state;
    }

    public void setState(MessageState state) {
        this.state = state;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
