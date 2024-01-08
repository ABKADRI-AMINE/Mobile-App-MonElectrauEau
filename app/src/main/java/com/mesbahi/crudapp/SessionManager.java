package com.mesbahi.crudapp;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {
        // Private constructor to prevent instantiation
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void loginUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logoutUser() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}

