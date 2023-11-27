package com.example.imPine;

import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.imPine.model.Diary;
import com.example.imPine.model.DiaryAdapter;
import com.example.imPine.model.DiaryResponse;
import com.example.imPine.model.PlantResponse;
import com.example.imPine.network.ApiInterface;
import com.example.imPine.network.RetrofitClient;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendsDetailActivity extends AppCompatActivity {

    private TextView friendDetailName;
    private List<Diary> samplePineDiaries = new ArrayList<>();
    private ApiInterface apiService;

    private void fetchAndDisplayDiaries(String userId) {
        String authToken = AuthLoginActivity.getAuthToken(this);
        apiService.getDiariesByUserId(authToken, userId).enqueue(new Callback<DiaryResponse>() {
            @Override
            public void onResponse(Call<DiaryResponse> call, Response<DiaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Diary> diaries = response.body().getDiaries();
                    updateDiariesRecyclerView(diaries);
                } else {
                    Toast.makeText(FriendsDetailActivity.this, "Failed to load diaries.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DiaryResponse> call, Throwable t) {
                Toast.makeText(FriendsDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDiariesRecyclerView(List<Diary> diaries) {
        RecyclerView diariesRecyclerView = findViewById(R.id.diariesRecyclerView);
        DiaryAdapter diariesAdapter = new DiaryAdapter(diaries);
        diariesRecyclerView.setAdapter(diariesAdapter);
        diariesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_detail);
        // Initialize the apiService here
        apiService = RetrofitClient.getClient().create(ApiInterface.class);


        friendDetailName = findViewById(R.id.friendDetailName);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ID")) {
            int id = intent.getIntExtra("ID", 0);
            String friendName = intent.getStringExtra("friendName");

            fetchAndDisplayDiaries(Integer.toString(id));

            // Get Retrofit instance
            Retrofit retrofit = RetrofitClient.getClient();

            // Create an ApiInterface instance
            ApiInterface apiInterface = retrofit.create(ApiInterface.class);

            // Retrieve the authentication token
            String authToken = AuthLoginActivity.getAuthToken(this);

            // Make the API call
            Call<PlantResponse> call = apiInterface.getUserPlants("Bearer " + authToken, Integer.toString(id));
            call.enqueue(new Callback<PlantResponse>() {
                @Override
                public void onResponse(Call<PlantResponse> call, Response<PlantResponse> response) {
                    if (response.isSuccessful()) {
                        // Handle successful response
                        PlantResponse plantResponse = response.body();
                        String pineName = plantResponse.getPlants().get(0).getName();
                        String status = plantResponse.getPlants().get(0).getStatus();
                        int avatar = plantResponse.getPlants().get(0).getAvatar();
                        String src = plantResponse.getPlants().get(0).getImage();
                        String createdAt = plantResponse.getPlants().get(0).getFormattedCreatedAt();
                        int daysOld = calculateDaysOld(createdAt);

                        // Update UI elements
                        TextView friendDetailNameTextView = findViewById(R.id.friendDetailName);
                        TextView pineNameTextView = findViewById(R.id.pineName);
                        TextView statusView = findViewById(R.id.status);
                        ImageView pineappleProfileImageView = findViewById(R.id.pineappleProfile);
                        TextView daysOldView = findViewById(R.id.old);

                        friendDetailNameTextView.setText(friendName);
                        pineNameTextView.setText(pineName);
                        statusView.setText(status);
                        setBoldLabel(friendDetailNameTextView, "Username: ", friendName);
                        setBoldLabel(pineNameTextView, "Pineapple Name: ", pineName);
                        setBoldLabel(statusView, "Status: ", status);
                        setBoldLabel(daysOldView, "", daysOld + " days old");

                        // Load the image
                        Glide.with(getApplicationContext())
                                .load(src)
                                .into(pineappleProfileImageView);


                        setAvatarImage(avatar, daysOld);

                    } else {
                        // Handle error response
                    }
                }

                @Override
                public void onFailure(Call<PlantResponse> call, Throwable t) {
                    // Handle network errors
                }
            });
        } else {
            // Handle the case where no ID was passed to this activity.
            finish();
            showToast("Friends details not available.");
        }


        RecyclerView diariesRecyclerView = findViewById(R.id.diariesRecyclerView);

//        // Populate with some sample PineDiaries for demo purposes
//        samplePineDiaries.add(new Diary("1", "Sample Title 1", "Sample Description 1", false));
//        samplePineDiaries.add(new Diary("2", "Sample Title 2", "Sample Description 2", false));
//        samplePineDiaries.add(new Diary("3", "Sample Title 3", "Sample Description 3", false));

        DiaryAdapter diariesAdapter = new DiaryAdapter(samplePineDiaries);
        diariesRecyclerView.setAdapter(diariesAdapter);
        diariesRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        ImageView pineappleAvatar = findViewById(R.id.pineappleAvatar);

        // Load the animations
        final Animation swayRight = AnimationUtils.loadAnimation(this, R.anim.sway_right);
        final Animation swayLeft = AnimationUtils.loadAnimation(this, R.anim.sway_left);

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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
