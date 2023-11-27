package com.example.imPine;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.imPine.model.Diary;
import com.example.imPine.network.ApiInterface;
import com.example.imPine.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryNewActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEditText;
    private Button saveButton;
    private ApiInterface apiService;
    private boolean isPrivate;

//    // Define private and public buttons
//    Button privateButton = findViewById(R.id.privateButton);
//    Button publicButton = findViewById(R.id.publicButton);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_new);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        saveButton = findViewById(R.id.saveButton);

        apiService = RetrofitClient.getClient().create(ApiInterface.class);

//        // Set click listeners for the buttons
//        privateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Set is_private to true when Private button is clicked
//                isPrivate = true;
//                // Change the button state to show it's clicked
//                privateButton.setSelected(true);
//                publicButton.setSelected(false);
//            }
//        });
//
//        publicButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Set is_private to false when Public button is clicked
//                isPrivate = false;
//                // Change the button state to show it's clicked
//                publicButton.setSelected(true);
//                privateButton.setSelected(false);
//            }
//        });
        ScrollView mainLayout = findViewById(R.id.mainLayout);

        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (getCurrentFocus() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus(); // Optional: Clear focus from the current EditText
                }
                return false;
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNewDiary();
            }
        });
    }

    private void postNewDiary() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and content cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        String authToken = AuthLoginActivity.getAuthToken(this); // Use the static method from AuthLoginActivity
        Diary newDiary = new Diary.Builder()
                .setTitle(title)
                .setContent(content)
                .setIsPrivate(isPrivate) // Assuming all new diaries are not private by default
                .build();

        Call<Diary> call = apiService.createDiary("Bearer " + authToken, newDiary);
        call.enqueue(new Callback<Diary>() {
            @Override
            public void onResponse(Call<Diary> call, Response<Diary> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DiaryNewActivity.this, "Diary saved successfully.", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after successful posting
                } else {
                    Toast.makeText(DiaryNewActivity.this, "Failed to post diary.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Diary> call, Throwable t) {
                Toast.makeText(DiaryNewActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
