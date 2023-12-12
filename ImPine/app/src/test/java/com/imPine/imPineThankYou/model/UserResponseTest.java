package com.imPine.imPineThankYou.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UserResponseTest {

    private UserResponse userResponse;
    private UserID testUser;

    @Before
    public void setUp() {
        userResponse = new UserResponse();
        testUser = new UserID();
        testUser.setId("12345");
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");

        userResponse.setUser(testUser);
    }

    @Test
    public void getUser_returnsCorrectUser() {
        UserID retrievedUser = userResponse.getUser();
        assertNotNull("User object should not be null", retrievedUser);
        assertEquals("ID should match", "12345", retrievedUser.getId());
        assertEquals("Name should match", "John Doe", retrievedUser.getName());
        assertEquals("Email should match", "john@example.com", retrievedUser.getEmail());
    }

    @Test
    public void setUser_setsUserCorrectly() {
        UserID newUser = new UserID();
        newUser.setId("67890");
        newUser.setName("Jane Doe");
        newUser.setEmail("jane@example.com");

        userResponse.setUser(newUser);

        UserID updatedUser = userResponse.getUser();
        assertEquals("ID should be updated correctly", "67890", updatedUser.getId());
        assertEquals("Name should be updated correctly", "Jane Doe", updatedUser.getName());
        assertEquals("Email should be updated correctly", "jane@example.com", updatedUser.getEmail());
    }
}
