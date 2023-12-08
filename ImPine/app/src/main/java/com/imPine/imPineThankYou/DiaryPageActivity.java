package com.imPine.imPineThankYou;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.imPine.imPineThankYou.model.Diary;
import com.imPine.imPineThankYou.model.DiaryAdapter;
import com.imPine.imPineThankYou.model.DiaryData;
import com.imPine.imPineThankYou.model.DiaryResponse;
import com.imPine.imPineThankYou.network.ApiInterface;
import com.imPine.imPineThankYou.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryPageActivity extends AppCompatActivity {
    private DiaryData diaryData;
    private DiaryAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddDiary;
    private ImageButton pineyButton, homeButton, predictionButton, friendButton, outButton;
    private ApiInterface apiService; // Declare the apiService variable
    TextView tvEmptyDiary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_page);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvEmptyDiary = findViewById(R.id.tv_empty_diary);


        diaryData = new DiaryData();
        adapter = new DiaryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        diaryData.registerObserver(adapter);

        // Initialize the apiService variable
        apiService = RetrofitClient.getClient().create(ApiInterface.class);

        adapter.setOnItemClickListener(new DiaryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Diary diary) {
                Intent intent = new Intent(DiaryPageActivity.this, DiaryDetailActivity.class);
                Log.d("DiaryID", "DiaryID: " + diary.getId());
                intent.putExtra("DIARY_ID", diary.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        fabAddDiary = findViewById(R.id.fab_add_item);
        fabAddDiary.setOnClickListener(view -> {
            // Start DiaryNewActivity for result
            Intent intent = new Intent(DiaryPageActivity.this, DiaryNewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        pineyButton = findViewById(R.id.piney);
        homeButton = findViewById(R.id.home);
        predictionButton = findViewById(R.id.prediction);
        friendButton = findViewById(R.id.friend);
        // Logout button click
        ImageButton outButton = findViewById(R.id.logOut);
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DiaryPageActivity.this)
                        .setTitle("Logout Confirmation")
                        .setMessage("Do you really want to logout?")
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                            // logout
                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove(getString(R.string.saved_auth_token));
                            editor.apply();
                            // Handle the logout logic here
                            Intent intent = new Intent(DiaryPageActivity.this, AuthLoginActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });


        setPineyImage(HomePageActivity.avatarFromHome);

        setUpAnimations();

        setUpNavigation();

        fetchDiariesFromApi();
    }

    private void setUpAnimations() {
        final Animation swayRight = AnimationUtils.loadAnimation(this, R.anim.sway_right);
        final Animation swayLeft = AnimationUtils.loadAnimation(this, R.anim.sway_left);

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

        pineyButton.startAnimation(swayRight);
    }

    private void setUpNavigation() {
        homeButton.setOnClickListener(view -> navigateTo(HomePageActivity.class));
        predictionButton.setOnClickListener(view -> navigateTo(PredictionPageActivity.class));
        friendButton.setOnClickListener(view -> navigateTo(FriendsPageActivity.class));
    }

    private void navigateTo(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void setPineyImage(int avatarValue) {
        int drawableResourceId = getAvatarDrawableId(avatarValue);
        Glide.with(this).load(drawableResourceId).into(pineyButton);
    }

    private int getAvatarDrawableId(int avatarValue) {
        switch (avatarValue) {
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


    @Override
    protected void onResume() {
        super.onResume();
        setPineyImage(HomePageActivity.avatarFromHome);
        fetchDiariesFromApi();
    }

    private void fetchDiariesFromApi() {
        String authToken = AuthLoginActivity.getAuthToken(this);

        apiService = RetrofitClient.getClient().create(ApiInterface.class);
        Call<DiaryResponse> call = apiService.getDiaries("Bearer " + authToken);

        call.enqueue(new Callback<DiaryResponse>() {
            @Override
            public void onResponse(Call<DiaryResponse> call, Response<DiaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Diary> diaries = response.body().getDiaries();
                    if (diaries.isEmpty()) {
                        tvEmptyDiary.setVisibility(View.VISIBLE);
                    } else {
                        tvEmptyDiary.setVisibility(View.GONE);
                    }

                    diaryData.setDiaries(diaries);
                    adapter.notifyDataSetChanged();

                    // Log each diary's attributes
                    for (Diary diary : diaries) {
                        Log.d("DiaryPageActivity", "Diary: Title=" + diary.getTitle() + ", Content=" + diary.getContent() + ", IsPrivate=" + diary.getIsPrivate());
                    }

                } else {
                    Toast.makeText(DiaryPageActivity.this, "Failed to load diaries.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DiaryResponse> call, Throwable t) {
                Toast.makeText(DiaryPageActivity.this, "Error fetching diaries: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        diaryData.unregisterObserver(adapter);
    }
}
