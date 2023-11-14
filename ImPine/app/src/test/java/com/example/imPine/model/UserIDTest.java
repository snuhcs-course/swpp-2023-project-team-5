package com.example.imPine.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UserIDTest {

    private UserID userID;
    private final String testId = "12345";
    private final String testName = "John Doe";
    private final String testEmail = "john@example.com";

    @Before
    public void setUp() {
        userID = new UserID();
        userID.setId(testId);
        userID.setName(testName);
        userID.setEmail(testEmail);
    }

    @Test
    public void getId_returnsCorrectId() {
        assertEquals("ID should match the assigned value", testId, userID.getId());
    }

    @Test
    public void getName_returnsCorrectName() {
        assertEquals("Name should match the assigned value", testName, userID.getName());
    }

    @Test
    public void getEmail_returnsCorrectEmail() {
        assertEquals("Email should match the assigned value", testEmail, userID.getEmail());
    }

    @Test
    public void setId_setsIdCorrectly() {
        String newId = "67890";
        userID.setId(newId);
        assertEquals("ID should be updated correctly", newId, userID.getId());
    }

    @Test
    public void setName_setsNameCorrectly() {
        String newName = "Jane Doe";
        userID.setName(newName);
        assertEquals("Name should be updated correctly", newName, userID.getName());
    }

    @Test
    public void setEmail_setsEmailCorrectly() {
        String newEmail = "jane@example.com";
        userID.setEmail(newEmail);
        assertEquals("Email should be updated correctly", newEmail, userID.getEmail());
    }
}
