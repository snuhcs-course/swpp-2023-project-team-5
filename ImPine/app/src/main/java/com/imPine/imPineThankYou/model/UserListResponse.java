package com.imPine.imPineThankYou.model;

import java.util.List;

public class UserListResponse {
    private List<User> users; // Assuming User is a class that matches the user objects in your JSON

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
