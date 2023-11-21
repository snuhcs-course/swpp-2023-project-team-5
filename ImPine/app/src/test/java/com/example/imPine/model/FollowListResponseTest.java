package com.example.imPine.model;

import com.example.imPine.Friends;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class FollowListResponseTest {

    private FollowListResponse followListResponse;
    private List<Friends> friendsList;

    @Before
    public void setUp() {
        friendsList = Arrays.asList(
                new Friends(1, "Friend1", "friend1@example.com"),
                new Friends(2, "Friend2", "friend2@example.com")
        );
        followListResponse = new FollowListResponse();
        followListResponse.setFollows(friendsList);
    }

    @Test
    public void testGetFollows() {
        List<Friends> retrievedList = followListResponse.getFollows();
        assertNotNull("List should not be null", retrievedList);
        assertEquals("List size should match", friendsList.size(), retrievedList.size());
        assertEquals("List contents should match", friendsList, retrievedList);
    }

    // Additional tests for setters and other methods if they are added
}
