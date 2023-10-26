package com.example.imPine;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class FriendsPageActivityTest {

    @Test
    public void testSwapFriendsOrder() {
        List<Friends> testFriends = new ArrayList<>();
        testFriends.add(new Friends("1", "Alice"));
        testFriends.add(new Friends("2", "Bob"));

        // Swap elements
        Collections.swap(testFriends, 0, 1);

        assertEquals("2", testFriends.get(0).getId());
        assertEquals("1", testFriends.get(1).getId());
    }
}
