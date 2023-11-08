package com.example.imPine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.imPine.model.Plant;
import com.example.imPine.network.ApiInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakePlantActivity extends AppCompatActivity {
    // ... Other member variables ...

    private void getAuthToken(AuthTokenCallback authTokenCallback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            authTokenCallback.onTokenReceived("Bearer " + idToken);
                        } else {
                            authTokenCallback.onTokenError(task.getException());
                        }
                    });
        } else {
            authTokenCallback.onTokenError(new Exception("User not logged in."));
        }
    }

    public interface AuthTokenCallback {
        void onTokenReceived(String authToken);
        void onTokenError(Exception e);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_plant_page);

        // ... Initialize your input fields ...

        // Save button
        findViewById(R.id.btn_save).setOnClickListener(v -> {
            // ... Get data from input fields ...
            EditText nameEditText = findViewById(R.id.editPlantName);
            String plantName = nameEditText.getText().toString();
            // ... Get other plant data ...

            // ... Construct plant object from data ...
            Plant plant = new Plant();
            plant.setName(plantName);
            // ... Set other data ...

            getAuthToken(new AuthTokenCallback() {
                @Override
                public void onTokenReceived(String authToken) {
                    // Now that we have the auth token, we can proceed to create the plant
                    ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
                    Call<Plant> call = apiService.createPlant(authToken, plant);
                    call.enqueue(new Callback<Plant>() {
                        @Override
                        public void onResponse(Call<Plant> call, Response<Plant> response) {
                            if (response.isSuccessful()) {
                                // Plant created successfully, handle response body if needed
                                Toast.makeText(MakePlantActivity.this, "Plant saved!", Toast.LENGTH_SHORT).show();
                                navigateToHomePage();
                            } else {
                                // Server returned an error
                                String errorMessage = "Failed to save plant. Error code: " + response.code();
                                try {
                                    // Convert error body to a string
                                    String errorBody = response.errorBody().string();
                                    // Log the error body or show it in the UI as appropriate
                                    Log.e("MakePlantActivity", "Error Body: " + errorBody);
                                    errorMessage += "\n" + errorBody;
                                } catch (IOException e) {
                                    // Handle IOException from errorBody().string()
                                    Log.e("MakePlantActivity", "Error extracting error body", e);
                                    errorMessage += "\n" + "Error extracting error information";
                                }
                                Toast.makeText(MakePlantActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Plant> call, Throwable t) {
                            // Network error or other unexpected error occurred
                            Toast.makeText(MakePlantActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("MakePlantActivity", "Network error", t);
                        }
                    });
                }

                @Override
                public void onTokenError(Exception e) {
                    // Authentication error occurred, handle it
                    Toast.makeText(MakePlantActivity.this, "Authentication error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }
}
