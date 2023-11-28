package com.example.imPine;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.imPine.model.Diary;
import com.example.imPine.model.DiaryGetResponse;
import com.example.imPine.network.ApiInterface;
import com.example.imPine.network.RetrofitClient;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendDiaryDetailActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private Button editButton, deleteButton;
    private ImageView imageView;
    private Spinner categorySpinner;

    private ApiInterface apiService;
    private int diaryId;
    private Diary originalDiary;
    private Button publicButton, privateButton;
    private String category;

    private void initializeCategorySpinner(String selectedCategory) {
        String[] categories = {"Happy", "Sad", "Angry", "Loving", "Grateful"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.spinner_item_text, categories);
        categorySpinner.setAdapter(adapter);
        // Set the spinner to the selected category
        int spinnerPosition = adapter.getPosition(selectedCategory);
        categorySpinner.setSelection(spinnerPosition);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = "Happy";
            }
        });
    }

    private void initializePrivacyButton(boolean isPrivate) {
        ImageView lockImage = findViewById(R.id.lock);
        if (isPrivate) {
            privateButton.setBackgroundColor(getResources().getColor(R.color.darkblue));
            publicButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            lockImage.setImageResource(R.drawable.lock);
        } else {
            publicButton.setBackgroundColor(getResources().getColor(R.color.darkblue));
            privateButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            lockImage.setImageResource(R.drawable.unlock);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_diary_detail);

        // Initialize UI components
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        imageView = findViewById(R.id.imageView);
        categorySpinner = findViewById(R.id.categorySpinner);

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
                    originalDiary = diary;

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
//
//                 initializeCategorySpinner(diary.getCategory());
//                 initializePrivacyButton(diary.getIsPrivate());

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
