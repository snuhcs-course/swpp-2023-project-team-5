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

    // Add getters, setters, and constructors as needed
}