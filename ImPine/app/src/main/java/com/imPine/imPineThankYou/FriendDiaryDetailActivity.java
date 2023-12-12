package com.imPine.imPineThankYou;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.imPine.imPineThankYou.model.Diary;
import com.imPine.imPineThankYou.model.DiaryGetResponse;
import com.imPine.imPineThankYou.network.ApiInterface;
import com.imPine.imPineThankYou.network.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendDiaryDetailActivity extends AppCompatActivity {

    private TextView titleEditText, contentEditText, category, date;
    private ImageView imageView;

    private ApiInterface apiService;
    private int diaryId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_diary_detail);

        // Initialize UI components
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        imageView = findViewById(R.id.imageView);
        category = findViewById(R.id.category);
        date = findViewById(R.id.dateLabel);
//        categorySpinner = findViewById(R.id.categorySpinner);

        // API Service Initialization
        apiService = RetrofitClient.getClient().create(ApiInterface.class);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("DIARY_ID")) {
            diaryId = intent.getIntExtra("DIARY_ID", 0);
            Log.d("DiaryID", "GOT DiaryID: " + diaryId);
            fetchDiaryDetails(diaryId);
        }
    }

    private void fetchDiaryDetails(int diaryId) {
        String authToken = AuthLoginActivity.getAuthToken(this);
        apiService.getDiary("Bearer " + authToken, String.valueOf(diaryId)).enqueue(new Callback<DiaryGetResponse>() {
            @Override
            public void onResponse(Call<DiaryGetResponse> call, Response<DiaryGetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("FriendDiaryDetailActivity", "Success");
                    Diary diary = response.body().getDiary();

                    // Logging the diary details
                    Log.d("FriendDiaryDetailActivity", "Title: " + diary.getTitle());
                    Log.d("FriendDiaryDetailActivity", "Content: " + diary.getContent());
                    Log.d("FriendDiaryDetailActivity", "Private: " + diary.getIsPrivate());
                    Log.d("FriendDiaryDetailActivity", "Category: " + diary.getCategory());
                    Log.d("FriendDiaryDetailActivity", "SRC: " + diary.getImage_src());
                    if (diary.getImage_src() != null) {
                        Log.d("FriendDiaryDetailActivity", "Image URL: " + diary.getImage_src());
                        loadImage(imageView, diary.getImage_src());
                    }

                    // Set the UI components with the diary details
                    titleEditText.setText(diary.getTitle());
                    contentEditText.setText(diary.getContent());
                    category.setText(diary.getCategory());
                    date.setText(diary.getFormattedCreatedAt());

                } else {
                    Log.e("FriendDiaryDetailActivity", "Failed to load diary. Response code: " + response.code());
                    try {
                        Log.e("FriendDiaryDetailActivity", "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("FriendDiaryDetailActivity", "Error parsing error body", e);
                    }
                    Toast.makeText(FriendDiaryDetailActivity.this, "Failed to load diary details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DiaryGetResponse> call, Throwable t) {
                Log.e("FriendDiaryDetailActivity", "Error fetching diary: " + t.getMessage(), t);
                Toast.makeText(FriendDiaryDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadImage(ImageView imageView, String imageUrl) {
        // Use Glide
        Glide.with(this).load(imageUrl).into(imageView);
    }
}
