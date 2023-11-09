package com.example.imPine;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import com.example.imPine.model.Plant;
import com.example.imPine.network.ApiInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakePlantActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ImageView imageView;

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

        imageView = findViewById(R.id.imageView);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Log.d("MakePlantActivity", "Image capture successful");
                            Bundle extras = result.getData().getExtras();
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            imageView.setImageBitmap(imageBitmap);
                        } else if (result.getResultCode() == RESULT_CANCELED) {
                            Log.d("MakePlantActivity", "Image capture cancelled by user");
                        } else {
                            Log.e("MakePlantActivity", "Image capture failed with result code: " + result.getResultCode());
                        }
                    }
                });

        findViewById(R.id.btn_take_picture).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            } else {
                dispatchTakePictureIntent();
            }
        });

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            EditText nameEditText = findViewById(R.id.editPlantName);
            String plantName = nameEditText.getText().toString();
            EditText heightEditText = findViewById(R.id.editHeight);
            String heightString = heightEditText.getText().toString();

            Plant plant = (new Plant(plantName, Integer.parseInt(heightString)));
            getAuthToken(new AuthTokenCallback() {
                @Override
                public void onTokenReceived(String authToken) {
                    createPlant(plant, authToken);
                }

                @Override
                public void onTokenError(Exception e) {
                    Log.e("MakePlantActivity", "Authentication error", e);
                }
            });
        });
    }

    private void createPlant(Plant plant, String authToken) {
        ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
        // Log the URL here
        Log.d("MakePlantActivity", "Attempting to create plant at URL: " + RetrofitClient.getBaseUrl() + "/api/plant/");
        Log.d("MakePlantActivity", "Token: " + authToken);
        Call<Plant> call = apiService.createPlant(authToken, plant);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Plant> call, Response<Plant> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MakePlantActivity.this, "Plant saved!", Toast.LENGTH_SHORT).show();
                    navigateToHomePage();
                } else {
                    navigateToHomePage();
                    handleResponseError(response);
                }
            }

            @Override
            public void onFailure(Call<Plant> call, Throwable t) {
                Toast.makeText(MakePlantActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MakePlantActivity", "Network error", t);
            }
        });
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            Toast.makeText(this, "Camera permission is required to use this feature", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleResponseError(Response<Plant> response) {
        String message = "An error occurred: ";
        if (response.errorBody() != null) {
            try {
                String errorBodyString = response.errorBody().string();
                // If the response is HTML, log the entire response for debugging
                if (errorBodyString.trim().startsWith("<!DOCTYPE html>")) {
                    Log.e("MakePlantActivity", "HTML response received, full error page: " + errorBodyString);
                    message += "HTML response received. Check logs for details.";
                } else {
                    // Try to parse it as JSON
                    JSONObject errorJson = new JSONObject(errorBodyString);
                    message += errorJson.optString("message", "Unknown error");
                }
            } catch (Exception e) {
                Log.e("MakePlantActivity", "Error parsing error body", e);
            }
        } else {
            message += "Unknown error";
        }
        Toast.makeText(MakePlantActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
