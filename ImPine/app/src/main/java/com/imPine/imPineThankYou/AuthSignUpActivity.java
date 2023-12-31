package com.imPine.imPineThankYou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.imPine.imPineThankYou.model.SignUpRequest;
import com.imPine.imPineThankYou.model.SignUpResponse;
import com.imPine.imPineThankYou.network.ApiInterface;
import com.imPine.imPineThankYou.network.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthSignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword, editTextPasswordConfirm, editTextUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
        editTextUsername = findViewById(R.id.editTextUsername);


        findViewById(R.id.buttonSignUp).setOnClickListener(v -> signUp());
        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);

        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (getCurrentFocus() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus(); // Optional: Clear focus from the current EditText
                }
                return false;
            }
        });
    }

    private void signUp() {
        String user = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordConfirm = editTextPasswordConfirm.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || user.isEmpty()) {
            // Handle empty fields
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address");
            editTextEmail.requestFocus();
            return;
        }

        // Check password length
        if (password.length() < 6) {
            editTextPassword.setError("Password should be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        // Check if passwords match
        if (!password.equals(passwordConfirm)) {
            editTextPasswordConfirm.setError("Passwords do not match");
            editTextPasswordConfirm.requestFocus();
            return;
        }

        // Sign up with Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Firebase sign up success, now get the Firebase token
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            firebaseUser.getIdToken(true)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Got the token, now sign up on your backend
                                            String firebaseToken = task1.getResult().getToken();
                                            signUpOnBackend(user, email, firebaseToken);
                                        } else {
                                            // Handle error - Could not get Firebase token
                                            Toast.makeText(AuthSignUpActivity.this, "Failed to get Firebase token: " + task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        // Firebase sign up failed
                        if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                            Toast.makeText(AuthSignUpActivity.this, "The password is too weak", Toast.LENGTH_LONG).show();
                        } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(AuthSignUpActivity.this, "An account already exists with this email address", Toast.LENGTH_LONG).show();
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(AuthSignUpActivity.this, "Invalid email format", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AuthSignUpActivity.this, "Firebase Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signUpOnBackend(String username, String email, String firebaseToken) {
        ApiInterface apiInterface = RetrofitClient.getClient().create(ApiInterface.class);

        // Creating the sign-up request with the username and email
        SignUpRequest signUpRequest = new SignUpRequest(username, email);

        // Making the call to your backend
        Call<SignUpResponse> call = apiInterface.createUser("Bearer " + firebaseToken, signUpRequest);

        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful()) {
                    // Backend sign up success
                    Toast.makeText(AuthSignUpActivity.this, "Sign Up Successful.", Toast.LENGTH_SHORT).show();
                    // Proceed to login activity and pass the email
                    navigateToLogin(email);
                } else {
                    // Backend sign up failed with response from server
                    Toast.makeText(AuthSignUpActivity.this, "Sign Up Failed: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                // Backend sign up failed with no response from server (network error, etc.)
                Toast.makeText(AuthSignUpActivity.this, "Sign Up Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToLogin(String email) {
        Intent intent = new Intent(AuthSignUpActivity.this, AuthLoginActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish(); // Finish this activity so the user can't navigate back to it
    }
}

