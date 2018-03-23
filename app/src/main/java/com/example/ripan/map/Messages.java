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

public class Messages {
    public interface MessagesObserver {
        void acceptNewMessages(ArrayList<Message> newMessages);
    }

    private static HashMap<String, Message> messages = new HashMap<>();
    private static ArrayList<MessagesObserver> observers = new ArrayList<>();

    private static MessagesCommunicationHelper communicationHelper = new MessagesCommunicationHelper();

    public static ArrayList<Message> getMessagesByUser(String userID) {
        ArrayList<Message> messagesByUser = new ArrayList<>();

        for (Message message : messages.values()) {
            if (message.getUserID().equals(userID)) {
                messagesByUser.add(message);
            }
        }

        return messagesByUser;
    }

    public static ArrayList<Message> getMessagesByLocalUser() {
        return getMessagesByUser(Users.getLocalUserID());
    }

    public static ArrayList<Message> getAllMessages() {
        return new ArrayList<>(messages.values());
    }

    public static void postMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException();
        }

        //Process message locally:
        message.setState(Message.MessageState.UserGenerated);

        messages.put(message.getMessageID(), message);

        ArrayList<Message> newMessages = new ArrayList<>(1);
        newMessages.add(message);

        Messages.informObservers(newMessages);

        Log.v("Messages","Sending message:");
        message.print();

        //Networking:
        communicationHelper.postMessage(message);
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

        //Networking:
        communicationHelper.update();
    }

    public static void addObserver(MessagesObserver o) {
        if (o == null ||observers.contains(o)) {
            return;
        }

        observers.add(o);
    }

    public static void removeObserver(MessagesObserver o) {
        observers.remove(o);
    }

    private static void informObservers(ArrayList<Message> newMessages) {
        for (MessagesObserver observer : observers) {
            observer.acceptNewMessages(newMessages);
        }
    }

    private static String getNewUniqueMessageID() {
        String messageID = StringID.randomID();

        while (messages.containsKey(messageID)) {
            messageID = StringID.randomID();
        }

        return messageID;
    }

    private static class MessagesCommunicationHelper
            implements Communication.MessagesGetRequest.MessagesGetRequestHandler,
                       Communication.MessagesPutRequest.MessagesPutRequestHandler {

        public void postMessage(Message message) {
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
    }

}
