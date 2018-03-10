package com.example.ripan.map;

import android.location.Location;
import android.util.*;

import com.google.android.gms.maps.model.LatLng;

import java.util.*;
import java.util.stream.*;

import org.json.*;

public class Messages {
    private static HashMap<String, Message> messages = new HashMap<>();

    public static Map<String, Message> getMessagesByUser(String userID) {
        Map<String, Message> results = messages.entrySet().stream()
                .filter(map -> map.getValue().getUserID().equals(userID))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

        return results;
    }

    public static Map<String, Message> getMessagesByLocalUser() {
        return getMessagesByUser(Users.getLocalUserID());
    }

    public static Map<String, Message> getAllMessages() {
        return Collections.unmodifiableMap(messages);
    }

    public static void postMessage(Message message) {

        if (message == null) {
            throw new IllegalArgumentException();
        }

        //Process message locally:
        message.setState(Message.MessageState.UserGenerated);

        messages.put(message.getMessageID(), message);

        Log.v("Messages","Sending message:");
        message.print();

        //Prepare message for server:
        Optional<JSONObject> mObj = message.toJson();

        if(mObj.isPresent()) {
            String identifier = message.getMessageID();
            String jsonString = mObj.get().toString();

            //Send message to server:
            Communication communication = new Communication();

            communication.execute(new Communication.MessagesPutRequest(identifier, jsonString, Messages::postedMessage));
        }
    }

    private static void postedMessage(Communication.MessagesPutRequest request) {
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

    public static void update() {
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

        communication.execute(new Communication.MessagesGetRequest(Messages::updated));
    }

    private static void updated(Communication.MessagesGetRequest request) {
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
        int newMessagesCount = 0;
        int oldMessagesCount = 0;

        for (int i = 0; i < messagesArray.length(); i++) {
            Optional<Message> m = Optional.empty();
            try {
                JSONObject messageObject = messagesArray.getJSONObject(i);
                m = Message.ParseMessage(messageObject);
            } catch (JSONException ex) {
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

                    newMessagesCount++;
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
    }

    private static String getNewUniqueMessageID() {
        String messageID = StringID.randomID();

        while (messages.containsKey(messageID)) {
            messageID = StringID.randomID();
        }

        return messageID;
    }

}
