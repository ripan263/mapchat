package com.example.ripan.map;

import android.location.Location;
import android.util.*;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.*;
import java.util.stream.*;

import org.json.*;

public class Messages implements Communication.MessagesGetRequest.MessagesGetRequestHandler,
                                 Communication.MessagesPutRequest.MessagesPutRequestHandler {
    private static Messages messagesSingleton;

    private HashMap<String, Message> messages;
    private ArrayList<MessagesObserver> observers;

    public GoogleMap mMap;

    public static interface MessagesObserver {

        public void updatedMessages(ArrayList<Message> newMesssages);

    }

    protected static Messages getMessages() {
        if (messagesSingleton == null) {
            messagesSingleton = new Messages();
        }

        return messagesSingleton;
    }

    private Messages() {
        messages = new HashMap<>();
        observers = new ArrayList<>();
    }

    public ArrayList<Message> getMessagesByUser(String userID) {
        ArrayList<Message> messagesByUser = new ArrayList<>();

        for (Message message : messages.values()) {
            if (message.getUserID().equals(userID)) {
                messagesByUser.add(message);
            }
        }

        return messagesByUser;
    }

    public ArrayList<Message> getMessagesByLocalUser() {
        return getMessagesByUser(Users.getLocalUserID());
    }

    public ArrayList<Message> getAllMessages() {
        return new ArrayList<>(messages.values());
    }

    public void postMessage(Message message) {

        if (message == null) {
            throw new IllegalArgumentException();
        }

        //Process message locally:
        message.setState(Message.MessageState.UserGenerated);

        messages.put(message.getMessageID(), message);

        ArrayList<Message> newMessages = new ArrayList<>(1);
        newMessages.add(message);

        informObservers(newMessages);

        Log.v("Messages","Sending message:");
        message.print();

        //Prepare message for server:
        Optional<JSONObject> mObj = message.toJson();

        if(mObj.isPresent()) {
            String identifier = message.getMessageID();
            String jsonString = mObj.get().toString();

            //Send message to server:
            Communication communication = new Communication();

            communication.execute(new Communication.MessagesPutRequest(identifier, jsonString, this));
        }
    }

    @Override
    public void handlePutRequestResult(Communication.MessagesPutRequest request) {
        if (request.wasSuccessful()) {
            Log.v("Messages","Successfully sent message");
        } else {
            Log.e("Messages","Failed to send message");
        }

        Message message = messages.get(request.getIdentifier());

        if (message == null) {
            return;
        }

        if (request.wasSuccessful()) {
            message.setState(Message.MessageState.UserGeneratedSent);
        } else {
            message.setState(Message.MessageState.UserGeneratedFailedToSend);
        }
    }

    public void update() {
        //Removed timed-out messages:
        HashMap<String, Message> newMessages = new HashMap<>(messages.size());
        Date now = new Date();

        for (Message message : messages.values()) {
            if (now.getTime() - message.getDate().getTime() <= 600000) {
                newMessages.put(message.getMessageID(), message);
            }
        }

        messages = newMessages;

        //Request messages from server:
        Communication communication = new Communication();

        communication.execute(new Communication.MessagesGetRequest(this));
    }

    @Override
    public void handleGetRequestResult(Communication.MessagesGetRequest request) {
        if (!request.wasSuccessful()) {
            Log.e("Messages","Failed to update");
            return;
        }

        //Process JSON array:
        Object result;
        try {
            result = new JSONTokener(request.getResult()).nextValue();

        } catch (JSONException ex) {
            Log.e("Messages","Failed to parse JSON for messages", ex);
            return;
        }

        if (!(result instanceof  JSONArray)) {
            Log.e("Messages", "Failed to parse JSON for messages (no array found)");
            return;
        }

        JSONArray messagesArray = (JSONArray) result;

        //Process messages in JSON array:
        ArrayList<Message> newMessages = new ArrayList<>();

        int newMessagesCount = 0;
        int oldMessagesCount = 0;

        for (int i = 0; i < messagesArray.length(); i++) {
            Optional<Message> m;
            try {
                JSONObject messageObject = messagesArray.getJSONObject(i);
                m = Message.ParseMessage(messageObject);
            } catch (JSONException ex) {
                continue;
            }

            // If message successfully parsed.
            if (m.isPresent()) {
                Message message = m.get();

                message.setState(Message.MessageState.Received);


                Log.v("Messages","Message received:");
                message.print();

                if (messages.containsKey(message.getMessageID())) {
                    oldMessagesCount++;
                }else{
                    messages.put(message.getMessageID(), message);

                    newMessages.add(message);

                    newMessagesCount++;

                    // Update map...
                    displayMsgOnMap(message);
                }
            }

            else {
                Log.e("Messages", "Cannot parse json object.");
            }
        }

        //Log results:
        Log.v("Messages", "Successfully updated:");
        Log.v("Messages", oldMessagesCount + " old message(s)");
        Log.v("Messages", newMessagesCount + " new message(s)");

        informObservers(newMessages);
    }

    public void addObserver(MessagesObserver o) {
        if (o == null ||observers.contains(o)) {
            return;
        }

        observers.add(o);
    }

    public void removeObserver(MessagesObserver o) {
        observers.remove(o);
    }

    private void informObservers(ArrayList<Message> newMessages) {
        for (MessagesObserver observer : observers) {
            observer.updatedMessages(newMessages);
        }
    }

    // TODO: This better..
    public void displayMsgOnMap(Message m) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(m.getLocation()).title(m.getUserID() + ": " + m.getMessage()));
        marker.showInfoWindow();
    }

    private String getNewUniqueMessageID() {
        String messageID = StringID.randomID();

        while (messages.containsKey(messageID)) {
            messageID = StringID.randomID();
        }

        return messageID;
    }

}
