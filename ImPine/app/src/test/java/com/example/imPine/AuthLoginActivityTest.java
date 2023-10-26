package com.example.imPine;

import android.content.Intent;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class AuthLoginActivityTest {

    @Mock
    FirebaseAuth mockAuth;

    @Mock
    FirebaseUser mockUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void emailFromIntent_populatesEditText() {
        String testEmail = "test@example.com";
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AuthLoginActivity.class);
        intent.putExtra("email", testEmail);

        try (ActivityScenario<AuthLoginActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                activity.mAuth = mockAuth; // Setting the mock

                EditText emailEditText = activity.findViewById(R.id.editTextEmail);
                assertEquals(testEmail, emailEditText.getText().toString());
            });
        }
    }

    @Test
    public void login_withEmptyEmail_showsError() {
        try (ActivityScenario<AuthLoginActivity> scenario = ActivityScenario.launch(AuthLoginActivity.class)) {
            scenario.onActivity(activity -> {
                activity.mAuth = mockAuth; // Setting the mock

                activity.findViewById(R.id.buttonLogin).performClick();
                EditText emailEditText = activity.findViewById(R.id.editTextEmail);
                assertTrue(emailEditText.getError().toString().contains("Email is required"));
            });
        }
    }

    // Additional tests go here...
}
