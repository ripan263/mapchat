package com.example.ripan.map;

public class User {
    //Unique identifier:
    private String userID;

    //Internal processing:
    private UserState state;

    //Content:
    private String userName;

    //User Information
    private String firstName;
    private String surName;
    private String dateOfBirth;
    private String email;
    private String password;


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

    public User (String first, String sur, String date, String e, String pass, String user) {
        userID = "";
        state = UserState.Unknown;
        userName = user;
        firstName = first;
        surName = sur;
        dateOfBirth = date;
        email = e;
        password = pass;
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

    public String getFirstName() { return firstName; }

    public void setFirstName (String name) { firstName = name; }

    public String getSurName () { return surName; }

    public void setSurName (String name) { surName = name; }

    public String getDateOfBirth () { return dateOfBirth; }

    public void setDateOfBirth (String date) { dateOfBirth = date; }

    public String getEmail () { return email; }

    public void setEmail (String e) { email = e; }

    public String getPassword () { return password; }

    public void setPassword (String pass) { password = pass; }

}
