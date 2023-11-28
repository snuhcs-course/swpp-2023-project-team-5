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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.imPine.model.Diary;
import com.example.imPine.model.DiaryGetResponse;
import com.example.imPine.network.ApiInterface;
import com.example.imPine.network.RetrofitClient;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryDetailActivity extends AppCompatActivity {

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
        setContentView(R.layout.diary_detail);

        // Initialize UI components
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
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
        setupEditButton();
        setupDeleteButton();
    }

    private void fetchDiaryDetails(int diaryId) {
        String authToken = AuthLoginActivity.getAuthToken(this);
        apiService.getDiary("Bearer " + authToken, String.valueOf(diaryId)).enqueue(new Callback<DiaryGetResponse>() {
            @Override
            public void onResponse(Call<DiaryGetResponse> call, Response<DiaryGetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("DiaryDetailActivity", "Success");
                    Diary diary = response.body().getDiary();
                    originalDiary = diary;

                    // Logging the diary details
                    Log.d("DiaryDetailActivity", "Title: " + diary.getTitle());
                    Log.d("DiaryDetailActivity", "Content: " + diary.getContent());
                    Log.d("DiaryDetailActivity", "Private: " + diary.getIsPrivate());
                    Log.d("DiaryDetailActivity", "Category: " + diary.getCategory());
                    Log.d("DiaryDetailActivity", "SRC: " + diary.getImage_src());
                    if (diary.getImage_src() != null) {
                        Log.d("DiaryDetailActivity", "Image URL: " + diary.getImage_src());
                        loadImage(imageView, diary.getImage_src());
                    }

                    // Set the UI components with the diary details
                    titleEditText.setText(diary.getTitle());
                    contentEditText.setText(diary.getContent());
//
//                 initializeCategorySpinner(diary.getCategory());
//                 initializePrivacyButton(diary.getIsPrivate());

                } else {
                    Log.e("DiaryDetailActivity", "Failed to load diary. Response code: " + response.code());
                    try {
                        Log.e("DiaryDetailActivity", "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("DiaryDetailActivity", "Error parsing error body", e);
                    }
                    Toast.makeText(DiaryDetailActivity.this, "Failed to load diary details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DiaryGetResponse> call, Throwable t) {
                Log.e("DiaryDetailActivity", "Error fetching diary: " + t.getMessage(), t);
                Toast.makeText(DiaryDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadImage(ImageView imageView, String imageUrl) {
        // Use Glide
        Glide.with(this).load(imageUrl).into(imageView);
    }

    private void setupEditButton() {
        editButton.setOnClickListener(v -> {
            if (hasChanges()) {
                updateDiary();
            } else {
                Toast.makeText(this, "No changes to be made", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hasChanges() {
        if (!originalDiary.getTitle().equals(titleEditText.getText().toString())) return true;
        if (!originalDiary.getContent().equals(contentEditText.getText().toString())) return true;

        return false;
    }

    private void updateDiary() {
        // Implement the logic to update the diary
        // Check if the image has changed and call either updateDiary or updateDiaryWithImage
    }

    private void setupDeleteButton() {
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Diary")
                    .setMessage("Are you sure you want to delete this diary?")
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> deleteDiary(diaryId))
                    .setNegativeButton(android.R.string.no, null).show();
        });
    }

    private void deleteDiary(int diaryId) {
        String authToken = AuthLoginActivity.getAuthToken(this);
        RequestBody diaryIdBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "diary_id=" + diaryId);
        apiService.deleteDiaryWithBody("Bearer " + authToken, diaryIdBody).enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DiaryDetailActivity.this, "Diary deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Handle error more specifically
                    Log.d("DiaryDelete", "Unsuccessful deletion attempt. Response code: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.d("DiaryDelete", "Error Body: " + errorBody);
                    } catch (IOException e) {
                        Log.e("DiaryDelete", "Error parsing error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure more specifically
                Log.e("DiaryDelete", "Error deleting diary: " + t.getMessage(), t);
            }
        });
    }
}
