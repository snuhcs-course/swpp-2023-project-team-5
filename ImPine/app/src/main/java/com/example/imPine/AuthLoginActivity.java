package com.example.imPine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imPine.model.Plant;
import com.example.imPine.model.PlantResponse;
import com.example.imPine.model.UserResponse;
import com.example.imPine.network.ApiInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private void navigateToSignUp() {
        Intent intent = new Intent(AuthLoginActivity.this, AuthSignUpActivity.class);
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
                        // Login success, now check for plants
                        checkUserPlants();
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

    private void checkUserPlants() {
        mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String idToken = task.getResult().getToken();
                String authToken = "Bearer " + idToken;
                // Save the token here
                saveAuthToken(idToken);

                Log.d("fbtoken", idToken);
                // Use Retrofit to create a service for the API interface
                ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);

                // First, make a call to getUser to fetch the user ID
                Call<UserResponse> callUser = apiService.getUser(authToken);
                callUser.enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String userId = response.body().getUser().getId();
                            Log.d("AuthLoginActivity", "User ID: " + userId); // Add this line to check the userID.
                            // Now make a call to getUserPlants using the fetched userID
                            Call<PlantResponse> callPlants = apiService.getUserPlants(authToken, userId);
                            callPlants.enqueue(new Callback<PlantResponse>() {
                               @Override
                                public void onResponse(Call<PlantResponse> call, Response<PlantResponse> response) {
                                    if (response.isSuccessful()) {
                                        PlantResponse plantResponse = response.body();
                                        List<Plant> plants = plantResponse != null ? plantResponse.getPlants() : null;
                                        if (plants != null && !plants.isEmpty()) {
                                            for (Plant plant : plants) {
                                                Log.d("AuthLoginActivity", "Plant ID: " + plant.toString());
                                            }
                                            navigateToActivity(HomePageActivity.class);
                                        } else {
                                            Log.d("AuthLoginActivity", "No plants found for the user.");
                                            navigateToActivity(TutorialActivity.class);
                                        }
                                    } else {
                                        Log.e("AuthLoginActivity", "Error fetching plants: " + response.code());
                                        Toast.makeText(AuthLoginActivity.this, "Error fetching plants.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<PlantResponse> call, Throwable t) {
                                    Log.e("AuthLoginActivity", "Network error or API is down", t);
                                    Toast.makeText(AuthLoginActivity.this, "Check your network connection.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Handle error in getting the userID
                            Log.e("AuthLoginActivity", "Error fetching user ID: " + response.code());
                            Toast.makeText(AuthLoginActivity.this, "Error fetching user ID.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Log.e("AuthLoginActivity", "Error getting user ID", t);
                        Toast.makeText(AuthLoginActivity.this, "Error during authentication.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Handle the error in getting the auth token
                Log.e("AuthLoginActivity", "Error getting auth token", task.getException());
                Toast.makeText(AuthLoginActivity.this, "Error during authentication.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(AuthLoginActivity.this, activityClass);
        startActivity(intent);
        finish();
    }

    private void saveAuthToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.saved_auth_token), token);
        editor.apply();
    }

    private String getAuthToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPreferences.getString(getString(R.string.saved_auth_token), null);
    }

}
