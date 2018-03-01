package com.example.ripan.map;

import android.util.*;

import java.util.*;
import java.util.stream.*;

import org.json.*;

public class Messages {
    private static HashMap<String, Message> messages;

    static {
        messages = new HashMap<>();
    }

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
        message.setMessageID(getNewUniqueMessageID());
        message.setState(Message.MessageState.UserGenerated);

        message.setUserID(Users.getLocalUserID());

        messages.put(message.getMessageID(), message);

        Log.v("Messages","Sending message:");
        Log.v("Messages","MessageID: " + message.getMessageID());
        Log.v("Messages","Date: " + message.getDate());
        Log.v("Messages","UserID: " + message.getUserID());
        Log.v("Messages","Location: " + message.getLocation());
        Log.v("Messages","Message: " + message.getMessage());

        //Prepare message for server:
        JSONObject messageObject = new JSONObject();

        try{
            messageObject.put("message_id", message.getMessageID());
            messageObject.put("user_id", message.getUserID());
            messageObject.put("message", message.getMessage());
            messageObject.put("time",message.getDate().getTime());
            messageObject.put("location",message.getLocation());
        }catch (JSONException ex) {
            Log.e("Messages","Failed to build JSON object for message", ex);
            return;
        }

        String identifier = message.getMessageID();
        String jsonString = messageObject.toString();

        //Send message to server:
        Communication communication = new Communication();

        communication.execute(new Communication.MessagesPutRequest(identifier, jsonString, Messages::postedMessage));
    }

    private static void postedMessage(Communication.MessagesPutRequest request) {
        if (request.wasSuccessful() == false) {
            Log.e("Messages","Failed to send message");
        }else{
            Log.v("Messages","Successfully sent message");
        }

        Message message = messages.get(request.getIdentifier());

        if (message == null) {
            return;
        }

        if (request.wasSuccessful()) {
            message.setState(Message.MessageState.UserGeneratedSent);
        }else{
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
        if (request.wasSuccessful() == false) {
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

        if ((result instanceof  JSONArray) == false) {
            Log.e("Messages", "Failed to parse JSON for messages (no array found)");
            return;
        }

        JSONArray messagesArray = (JSONArray) result;

        //Process messages in JSON array:
        int newMessagesCount = 0;
        int oldMessagesCount = 0;

        for (int i = 0; i < messagesArray.length(); i++) {
            String messageID;

            String userID;
            String messageString;
            double dateDouble;
            String location;

            try {
                JSONObject messageObject = messagesArray.getJSONObject(i);

                messageID = messageObject.getString("message_id");

                userID = messageObject.getString("user_id");
                messageString = messageObject.getString("message");
                dateDouble = messageObject.getDouble("time");
                location = messageObject.getString("location");

            }catch (JSONException ex) {
                Log.e("Messages","Failed to parse JSON for message", ex);
                continue;
            }

            if (StringID.isValidID(messageID) == false) {
                Log.e("Messages","Received invalid messageID");
                continue;
            }

            if (StringID.isValidID(userID) == false) {
                Log.e("Messages", "Received invalid userID");
                continue;
            }

            long dateLong = (long) dateDouble;
            Date date = new Date(dateLong);

            Message message = new Message();

            message.setMessageID(messageID);
            message.setState(Message.MessageState.Received);

            message.setUserID(userID);
            message.setMessage(messageString);
            message.setDate(date);
            message.setLocation(location);

            Log.v("Messages","Message received:");
            Log.v("Messages","MessageID: " + message.getMessageID());
            Log.v("Messages","Date: " + message.getDate());
            Log.v("Messages","UserID: " + message.getUserID());
            Log.v("Messages","Location: " + message.getLocation());
            Log.v("Messages","Message: " + message.getMessage());

            if (messages.containsKey(messageID)) {
                oldMessagesCount++;
            }else{
                messages.put(messageID, message);

                newMessagesCount++;
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
