package com.example.ripan.map;

public class User {
    //Unique identifier:
    private String userID;

    //Internal processing:
    private UserState state;

    //Content:
    private String userName;

    public enum UserState {
        Unknown,
        Received,
        Local
    }

    public User() {
        userID = "";
        state = UserState.Unknown;
        userName = "";
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
