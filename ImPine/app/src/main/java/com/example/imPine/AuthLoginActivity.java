package com.example.imPine;

import androidx.appcompat.app.AppCompatActivity;

//import android.hardware.biometrics.BiometricManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class AuthLoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText editTextEmail;
    EditText editTextPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        findViewById(R.id.buttonSignUp).setOnClickListener(v -> navigateToSignUp());
        findViewById(R.id.buttonLogin).setOnClickListener(v -> login());

        // Check if email is passed in Intent
        String email = getIntent().getStringExtra("email");
        if (email != null) {
            editTextEmail.setText(email); // Set the email
        }
    }

    void navigateToSignUp() {
        Intent intent = new Intent(AuthLoginActivity.this, AuthSignUpActivity.class);
        startActivity(intent);
    }

    void login() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login success
                        Toast.makeText(AuthLoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();

                        // Get IdToken after successful login
                        mAuth.getCurrentUser().getIdToken(true)
                                .addOnCompleteListener(idTokenTask -> {
                                    if (idTokenTask.isSuccessful()) {
                                        String idToken = idTokenTask.getResult().getToken();
                                        // Store the idToken in local storage (SharedPreferences or any other storage)
                                        SharedPreferences sharedPref = getSharedPreferences("my_prefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("firebaseIdToken", idToken);
                                        Log.d("Fbtoken", idToken);
                                        editor.apply();
                                    } else {
                                        // Handle the error in retrieving the IdToken
                                        Log.e("LOGIN", "Failed to get IdToken", idTokenTask.getException());
                                    }
                                });

                        // Start the home page activity
                        Intent intent = new Intent(AuthLoginActivity.this, HomePageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();  // Finish the current activity and the user won't be able to go back to it
                    } else {
                        // If login fails, display a message to the user.
                        String errorMessage = "Authentication Failed.";
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            errorMessage = "Invalid password.";
                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            errorMessage = "No account found with this email.";
                        }
                        Toast.makeText(AuthLoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}