package com.imPine.imPineThankYou;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.content.SharedPreferences;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.imPine.imPineThankYou.model.Plant;
import com.imPine.imPineThankYou.model.PlantResponse;
import com.imPine.imPineThankYou.model.UserResponse;
import com.imPine.imPineThankYou.network.ApiInterface;
import com.imPine.imPineThankYou.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomePageActivity extends AppCompatActivity {
    private PlantResponse plantResponse;
    private RelativeLayout loadingPanel;
    private static String myUserName;

    public static int avatarFromHome;

    public static String getMyUserName() {
        return myUserName;
    }

    private int calculateDaysOld(String createdDateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date createdDate = dateFormat.parse(createdDateStr);
            Date currentDate = new Date(); // Current date

            // Calculate the difference in milliseconds
            long diffInMillies = currentDate.getTime() - createdDate.getTime();

            // Convert milliseconds to days
            return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Return -1 or some error code if parsing fails
        }
    }
    private void setAvatarImage(int avatarValue, int daysOld) {
        if (daysOld > 60) {
            daysOld = 60;
        }
        int drawableResourceId = getAvatarDrawableId(avatarValue);
        ImageView pineappleAvatar = findViewById(R.id.pineappleAvatar);

        // Calculate the scale factor. For example, start at 0.3 times the size and increase by 0.1 every 3 days.
        // The scale will be 1 (full size) after 30 days.
        float scaleFactor = 0.3f + ((float) Math.min(daysOld / 3, 30) * (0.7f / 10));

        // Now set the scale of the ImageView
        pineappleAvatar.setScaleX(scaleFactor); // Scale in X
        pineappleAvatar.setScaleY(scaleFactor); // Scale in Y

        Glide.with(this)
                .load(drawableResourceId)
                .into(pineappleAvatar);
    }
    private void setPineyImage(int avatarValue) {
        int drawableResourceId = getAvatarDrawableId(avatarValue);
        ImageView piney = findViewById(R.id.piney);
        Glide.with(this)
                .load(drawableResourceId)
                .into(piney);
    }
    private int getAvatarDrawableId(int avatarValue) {
        switch (avatarValue) {
            case 0: return R.drawable.pine_avatar;
            case 1: return R.drawable.twofatty;
            case 2: return R.drawable.threelazy;
            case 3: return R.drawable.fourbrowny;
            case 4: return R.drawable.fivecooly;
            case 5: return R.drawable.sixalien;
            case 6: return R.drawable.sevenalien;
            case 7: return R.drawable.eightavatar;
            case 8: return R.drawable.nineavatar;
            default: return R.drawable.pine_avatar;
        }
    }

    private void setBoldLabel(TextView textView, String label, String value) {
        SpannableString spannable = new SpannableString(label + " " + value);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("HomePageActivity", "Resume after edit");
        setPineyImage(HomePageActivity.avatarFromHome);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

//        setPineyImage(HomePageActivity.avatarFromHome);
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
                    myUserName = userResponse.getUser().getName();
                    setBoldLabel(usernameTextView, "Username: ", userResponse.getUser().getName());
                    String userId;
                    userId = userResponse.getUser().getId();
                    Log.d("HomePageActivity", "UserId: " + userId);
                    // Get Plant Details for the User
                    // Assuming you have a way to get the user's ID, pass it to the getUserPlants method
                    Log.d("HomePageActivity", "UserId before GET: " + userId);
                    Log.d("HomePageActivity", "AuthToken: " + authToken);

                    Call<PlantResponse> plantCall = apiService.getUserPlants(authToken, userId);
                    TextView pineappleNameTextView, heightTextView, lastWateredTextView, statusTextView, birthdayTextView;
                    pineappleNameTextView = findViewById(R.id.pineappleName);
                    heightTextView= findViewById(R.id.height);
                    lastWateredTextView = findViewById(R.id.lastWatered);
                    statusTextView = findViewById(R.id.status);
                    birthdayTextView = findViewById(R.id.birthday);
                    int daysOld = 0;

                    plantCall.enqueue(new Callback<PlantResponse>() {
                        @Override
                        public void onResponse(Call<PlantResponse> call, Response<PlantResponse> response) {
                            if (response.isSuccessful()) {
                                plantResponse = response.body();
                                setBoldLabel(pineappleNameTextView, "Pineapple Name: ", plantResponse.getPlants().get(0).getName());
                                setBoldLabel(heightTextView, "Height: ", String.valueOf(plantResponse.getPlants().get(0).getHeight()) + "cm");
                                setBoldLabel(statusTextView, "Status: ", plantResponse.getPlants().get(0).getStatus());
                                String createdDate = plantResponse.getPlants().get(0).getFormattedCreatedAt();
                                int daysOld = calculateDaysOld(createdDate);
                                setBoldLabel(birthdayTextView, "Has been with me since: \n", createdDate + "\n("+ daysOld + " days old)");
                                setBoldLabel(lastWateredTextView, "Last Watered Date:\n", plantResponse.getPlants().get(0).getLast_watered());
                                String imagePath = plantResponse.getPlants().get(0).getImage();
                                int avatar = plantResponse.getPlants().get(0).getAvatar();
                                setAvatarImage(avatar, daysOld);
                                setPineyImage(avatar);
                                avatarFromHome = avatar;


                                if (imagePath != null && !imagePath.isEmpty()) {
                                    ImageView pineappleProfile = findViewById(R.id.pineappleProfile);
                                    Glide.with(HomePageActivity.this)
                                            .load(imagePath) // Use Glide or another image loading library to handle image loading and caching
                                            .into(pineappleProfile);
                                    Log.d("HomePageActivityPic", "Loading...");
                                } else {
                                    Log.d("HomePageActivityPic", "No image path for the plant: " + imagePath);
                                }

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

        // Find the Edit button by its ID
        ImageButton editButton = findViewById(R.id.editButton);

        // Set an OnClickListener for the Edit button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plantResponse != null && plantResponse.getPlants() != null && !plantResponse.getPlants().isEmpty()) {
                    Plant currentPlant = plantResponse.getPlants().get(0);

                    // Get the current plant details
                    String pineappleName = currentPlant.getName(); // Replace with the actual name
                    int height = currentPlant.getHeight(); // Replace with the actual height
                    String imageURL = currentPlant.getImage(); // Replace with the actual image URL
                    String lastWatered = currentPlant.getLast_watered(); // Retrieve the last watered date
                    String status = currentPlant.getStatus(); // Retrieve the status
                    int avatarIntent = currentPlant.getAvatar();

                    // Create an Intent to navigate to the edit page
                    Intent editIntent = new Intent(HomePageActivity.this, EditPlantActivity.class);

                    // Pass the plant details as extras in the Intent
                    editIntent.putExtra("pineappleName", pineappleName);
                    editIntent.putExtra("height", height);
                    editIntent.putExtra("imageURL", imageURL);
                    editIntent.putExtra("lastWatered", lastWatered);
                    editIntent.putExtra("status", status);
                    editIntent.putExtra("avatar", avatarIntent);

                    // Start the EditPlantActivity with the Intent
                    startActivity(editIntent);
                } else {
                    Toast.makeText(HomePageActivity.this, "Plant details not available", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Diary button click
        ImageButton diaryButton = findViewById(R.id.diary);
        diaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the DiaryPageActivity
                Intent intent = new Intent(HomePageActivity.this, DiaryPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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

        // Logout button click
        ImageButton outButton = findViewById(R.id.logOut);
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(HomePageActivity.this)
                        .setTitle("Logout Confirmation")
                        .setMessage("Do you really want to logout?")
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                            // logout
                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove(getString(R.string.saved_auth_token));
                            editor.apply();
                            Intent intent = new Intent(HomePageActivity.this, AuthLoginActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton(android.R.string.no, null).show();
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
