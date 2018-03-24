package com.example.ripan.map;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

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
    private LatLng location;
    private PinType pinType;

    public enum MessageState {
        Unknown,
        Received,
        UserGenerated,
        UserGeneratedSent,
        UserGeneratedFailedToSend
    }

    public enum PinType {
        Red,
        Blue,
        Green,
        Orange,
        Swords,
        BoxingGlove,
        NyanCat
    }

    // Create message with new ID.
    public Message(String userID, String message, LatLng location, Date date, PinType type) {
        this(StringID.randomID(), userID, message, location, date, type);
    }


    // Create message with known ID.
    private Message(String messageID, String userID, String message, LatLng location, Date date, PinType pinType) {
        this.messageID = messageID;
        this.userID = userID;
        this.message = message;
        this.location = location;
        this.date = date;
        this.pinType = pinType;

        state = MessageState.Unknown;
    }

    // Parse message from json object
    public static Optional<Message> ParseMessage(JSONObject messageObject) {
        String messageID;

        String userID;
        String messageString;
        String typeString;
        double dateDouble;
        double latitude;
        double longitude;

        try {
            messageID = messageObject.getString("message_id");

            userID = messageObject.getString("user_id");
            messageString = messageObject.getString("message");
            dateDouble = messageObject.getDouble("time");
            latitude = messageObject.getDouble("latitude");
            longitude = messageObject.getDouble("longitude");
            typeString = messageObject.getString("pin_type");
        } catch (JSONException ex) {
            Log.e("Messages","Failed to parse JSON for message", ex);
            return Optional.empty();
        }

        if (StringID.isValidID(messageID) == false) {
            Log.e("Messages","Received invalid messageID");
            return Optional.empty();
        }

        //if (StringID.isValidID(userID) == false) {
        //    Log.e("Messages", "Received invalid userID");
        //    return Optional.empty();
        //}

        try {
            long dateLong = (long) dateDouble;
            Date date = new Date(dateLong);
            LatLng location = new LatLng(latitude, longitude);

            PinType type = PinType.valueOf(typeString);

            return Optional.of(new Message(messageID, userID, messageString, location, date, type));
        }
        catch (Exception e) {
            Log.e("Messages", "Invalid or out of date properties for message", e);
            return Optional.empty();
        }
    }

    public Optional<JSONObject> toJson() {
        JSONObject messageObject = new JSONObject();

        try{
            messageObject.put("message_id", getMessageID());
            messageObject.put("user_id", getUserID());
            messageObject.put("message", getMessage());
            messageObject.put("time",getDate().getTime());

            messageObject.put("latitude", getLocation().latitude);
            messageObject.put("longitude", getLocation().longitude);
            messageObject.put("pin_type", getPinType());

            return Optional.of(messageObject);

        } catch (JSONException ex) {
            Log.e("Messages","Failed to build JSON object for message", ex);
            return Optional.empty();
        }
    }

    public void print() {
        Log.v("Messages","MessageID: " + getMessageID());
        Log.v("Messages","Date: " + getDate());
        Log.v("Messages","UserID: " + getUserID());
        Log.v("Messages","Location: " + getLocation());
        Log.v("Messages","Message: " + getMessage());
        Log.v("Messages", "PinType: " + getPinType());
    }

    public String getMessageID() {
        return messageID;
    }

    /*public void setMessageID(String messageID) {
        this.messageID = messageID;
    }*/

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

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public PinType getPinType() {
        return pinType;
    }

}
