package com.imPine.imPineThankYou;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.imPine.imPineThankYou.model.Friends;

public class FriendsTest {

    private Friends friend;

    @Before
    public void setUp() {
        friend = new Friends(1, "John Doe", "johndoe@example.com");
    }

    @Test
    public void testConstructorWithThreeParameters() {
        assertEquals(1, friend.getId());
        assertEquals("John Doe", friend.getName());
        assertEquals("johndoe@example.com", friend.getEmail());
    }

    @Test
    public void testConstructorWithTwoParameters() {
        Friends friendTwoParams = new Friends(2, "Jane Doe");
        assertEquals(2, friendTwoParams.getId());
        assertEquals("Jane Doe", friendTwoParams.getName());
        assertNull(friendTwoParams.getEmail());
    }

    @Test
    public void testSetAndGetId() {
        friend.setId(10);
        assertEquals(10, friend.getId());
    }

    @Test
    public void testSetAndGetName() {
        friend.setName("Jane Doe");
        assertEquals("Jane Doe", friend.getName());
    }

    @Test
    public void testSetAndGetEmail() {
        friend.setEmail("janedoe@example.com");
        assertEquals("janedoe@example.com", friend.getEmail());
    }
}
