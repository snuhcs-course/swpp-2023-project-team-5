package com.example.imPine;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.imPine.model.Plant;
import com.example.imPine.model.PlantResponse;
import com.example.imPine.model.UserResponse;
import com.example.imPine.network.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // TODO: get the pineapple profile
//        ImageView pineappleProfile = findViewById(R.id.pineappleProfile);
//        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
//        String imagePath = prefs.getString("profile_image_path", null);
//        if (imagePath != null) {
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//            pineappleProfile.setImageBitmap(bitmap);
//        }

        // Retrieve the name of the SharedPreferences file and the key for the auth token from the resources
        String prefsFile = getString(R.string.preference_file_key);
        String authTokenKey = getString(R.string.saved_auth_token);

        // Retrieve the auth token from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(prefsFile, MODE_PRIVATE);
        String authToken = "Bearer " + prefs.getString(authTokenKey, null); // Second parameter is the default value.

        // Make sure you have RetrofitClient set up to provide the Retrofit instance
        ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
        TextView usernameTextView = findViewById(R.id.username);
        // Get User Details
        Call<UserResponse> userCall = apiService.getUser(authToken);
        userCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    UserResponse userResponse = response.body();
                    usernameTextView.setText("Username: " + userResponse.getUser().getName());
                    String userId;
                    userId = userResponse.getUser().getId();
                    Log.d("HomePageActivity", "UserId: " + userId);
                    // Get Plant Details for the User
                    // Assuming you have a way to get the user's ID, pass it to the getUserPlants method
                    Log.d("HomePageActivity", "UserId before GET: " + userId);
                    Log.d("HomePageActivity", "AuthToken: " + authToken);

                    Call<PlantResponse> plantCall = apiService.getUserPlants(authToken, userId);
                    TextView pineappleNameTextView, heightTextView, lastWateredTextView, statusTextView;
                    pineappleNameTextView = findViewById(R.id.pineappleName);
                    heightTextView= findViewById(R.id.height);
//                    lastWateredTextView = findViewById(R.id.lastWatered);
                    statusTextView = findViewById(R.id.status);

                    plantCall.enqueue(new Callback<PlantResponse>() {
                        @Override
                        public void onResponse(Call<PlantResponse> call, Response<PlantResponse> response) {
                            if (response.isSuccessful()) {
                                PlantResponse plantResponse = response.body();
                                pineappleNameTextView.setText("Pineapple Name: " + plantResponse.getPlants().get(0).getName());
                                heightTextView.setText("Height: " + plantResponse.getPlants().get(0).getHeight() + "cm");
//                                lastWateredTextView.setText("Last Watered Date: " + plantResponse.getPlants().get(0).getLastWatered());
                                statusTextView.setText("Status: " + plantResponse.getPlants().get(0).getStatus());

                            } else {
                                if (response.errorBody() != null) {
                                    try {
                                        // Convert the error body to a string
                                        String errorString = response.errorBody().string();
                                        Log.e("HomePageActivity", "Error fetching plant details: " + errorString);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PlantResponse> call, Throwable t) {
                            Log.e("HomePageActivity", "Network error when fetching plant details: " + t.getMessage());
                        }
                    });

                } else {
                    // Handle the error
                    Log.e("HomePageActivity", "Error fetching user details: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("HomePageActivity", "Network error when fetching user details: " + t.getMessage());
            }
        });




        ImageButton pineyButton = findViewById(R.id.piney);
        // Load the animations
        final Animation swayRight = AnimationUtils.loadAnimation(this, R.anim.sway_right);
        final Animation swayLeft = AnimationUtils.loadAnimation(this, R.anim.sway_left);

        // Set animation listeners to create an infinite swaying effect for piney
        swayRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                pineyButton.startAnimation(swayLeft);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        swayLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                pineyButton.startAnimation(swayRight);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Start the animation
        pineyButton.startAnimation(swayRight);

        // Diary button click
        ImageButton diaryButton = findViewById(R.id.diary);
        diaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the DiaryPageActivity
                Intent intent = new Intent(HomePageActivity.this, DiaryPageActivity.class);
                startActivity(intent);
            }
        });

        // Prediction button click
        ImageButton predictionButton = findViewById(R.id.prediction);
        predictionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, PredictionPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Friends button click
        ImageButton friendButton = findViewById(R.id.friend);
        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, FriendsPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Settings button click
        ImageButton setButton = findViewById(R.id.set);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, SettingsPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Piney button click
        ImageButton userButton = findViewById(R.id.piney);
//        userButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomePageActivity.this, UsersPageActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                startActivity(intent);
//            }
//        });

        // Note button click
        ImageButton noteButton = findViewById(R.id.note);
        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, NotificationsPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        ImageView pineappleAvatar = findViewById(R.id.pineappleAvatar);


        // Set animation listeners to create an infinite swaying effect
        swayRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                pineappleAvatar.startAnimation(swayLeft);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        swayLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                pineappleAvatar.startAnimation(swayRight);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Start the animation
        pineappleAvatar.startAnimation(swayRight);

    }

}
