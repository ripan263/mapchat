package com.example.ripan.map;

import java.util.*;

public class Users {
    private static HashMap<String, User> users;
    private static String localUserID;

    static {
        users = new HashMap<>(1);
        localUserID = StringID.randomID();

        User localUser = new User();

        localUser.setUserID(localUserID);
        localUser.setState(User.UserState.Local);
        localUser.setUserName("Marry");

        users.put(localUserID, localUser);
    }

    public static String getLocalUserID() {
        return localUserID;
    }

    public static User getLocalUser() {
        return users.get(localUserID);
    }

    public static boolean hasUser(String userID) {
        return users.containsKey(userID);
    }

    public static User getUser(String userID) {
        return  users.get(userID);
    }

    public static Map<String, User> getAllUsers() {
        return Collections.unmodifiableMap(users);
    }

}
