package com.example.imPine.model;

public class UserResponse {
    private UserID user; // This should match the key "user" in your JSON

    // Getter
    public UserID getUser() {
        return user;
    }

    // Setter
    public void setUser(UserID user) {
        this.user = user;
    }

    // toString(), etc., if needed
}
