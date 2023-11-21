package com.example.imPine.model;

import com.example.imPine.Friends;

import java.util.Collection;
import java.util.List;

public class FollowListResponse {
    UserID user;
    List<Friends> follows;

    public List<Friends> getFollows() {
        return follows;
    }

    public void setFollows(List<Friends> friendsList) {
        follows = friendsList;
    }

    // Add getters, setters, and constructors as needed
}