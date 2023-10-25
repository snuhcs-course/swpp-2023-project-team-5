package com.example.imPine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DiaryDetailActivity extends AppCompatActivity {

    private TextView tvDiaryTitle;
    private TextView tvDiaryDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_detail);

        tvDiaryTitle = findViewById(R.id.diary_title);
        tvDiaryDescription = findViewById(R.id.diary_description);

        // Assuming you're passing the diary details via Intent extras
        Intent intent = getIntent();
        if (intent != null) {
            String diaryTitle = intent.getStringExtra("DIARY_TITLE");
            String diaryDescription = intent.getStringExtra("DIARY_DESCRIPTION");

            tvDiaryTitle.setText(diaryTitle);
            tvDiaryDescription.setText(diaryDescription);
        }
    }
}
