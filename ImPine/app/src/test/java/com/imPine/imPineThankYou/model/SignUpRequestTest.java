package com.imPine.imPineThankYou.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SignUpRequestTest {

    private SignUpRequest signUpRequest;
    private final String testName = "John Doe";
    private final String testEmail = "john@example.com";

    @Before
    public void setUp() {
        signUpRequest = new SignUpRequest(testName, testEmail);
    }

    @Test
    public void constructor_assignsValuesCorrectly() {
        assertEquals("Name should match the constructor argument", testName, signUpRequest.name);
        assertEquals("Email should match the constructor argument", testEmail, signUpRequest.email);
    }
}
