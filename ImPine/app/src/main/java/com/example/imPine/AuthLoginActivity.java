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

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;

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

    private void navigateToSignUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void login() {
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
                        Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();

                        // Get IdToken after successful login
                        mAuth.getCurrentUser().getIdToken(true)
                                .addOnCompleteListener(idTokenTask -> {
                                    if (idTokenTask.isSuccessful()) {
                                        String idToken = idTokenTask.getResult().getToken();
                                        // Store the idToken in local storage (SharedPreferences or any other storage)
                                        SharedPreferences sharedPref = getSharedPreferences("my_prefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("firebaseIdToken", idToken);
                                        editor.apply();
                                    } else {
                                        // Handle the error in retrieving the IdToken
                                        Log.e("LOGIN", "Failed to get IdToken", idTokenTask.getException());
                                    }
                                });

                        // Start the home page activity
                        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish();  // This will finish the current activity and the user won't be able to go back to it
                    } else {
                        // If login fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}