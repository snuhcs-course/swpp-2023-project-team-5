package com.example.imPine;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imPine.model.Plant;
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
                // Use Retrofit to create a service for the API interface
                ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
                String userId = mAuth.getCurrentUser().getUid();
                String authToken = "Bearer " + idToken; // Assuming idToken is correct.
                String requestUrl = RetrofitClient.getClient().baseUrl().toString() + "api/plant/user/" + userId;

                Log.d("AuthLoginActivity", "Requesting URL: " + requestUrl);
                Call<List<Plant>> call = apiService.getUserPlants("Bearer " + idToken, userId);

                call.enqueue(new Callback<List<Plant>>() {
                    @Override
                    public void onResponse(Call<List<Plant>> call, Response<List<Plant>> response) {
                        if (response.isSuccessful()) {
                            List<Plant> plants = response.body();
                            if (plants == null || plants.isEmpty()) {
                                // No plants found, navigate to tutorial
                                navigateToActivity(TutorialActivity.class);
                            } else {
                                // Plants found, navigate to home page
                                navigateToActivity(HomePageActivity.class);
                            }
                        } else {
                            // Handle the backend error
                            Log.e("AuthLoginActivity", "Error fetching plants: " + response.code());
                            Toast.makeText(AuthLoginActivity.this, "Error fetching plants.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Plant>> call, Throwable t) {
                        // Handle the network error
                        Log.e("AuthLoginActivity", "Network error or API is down", t);
                        Toast.makeText(AuthLoginActivity.this, "Check your network connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Handle the error
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
}
