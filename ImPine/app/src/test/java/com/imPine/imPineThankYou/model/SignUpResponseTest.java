package com.imPine.imPineThankYou.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SignUpResponseTest {

    private SignUpResponse signUpResponse;
    private final String testMessage = "User created successfully";

    @Before
    public void setUp() {
        signUpResponse = new SignUpResponse();
        signUpResponse.message = testMessage; // Assuming direct field access. Adjust if using a constructor or setter method.
    }

    @Test
    public void getMessage_returnsCorrectMessage() {
        assertEquals("Message should match the assigned value", testMessage, signUpResponse.getMessage());
    }
}
