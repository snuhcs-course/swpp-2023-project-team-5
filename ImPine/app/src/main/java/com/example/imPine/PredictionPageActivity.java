package com.example.imPine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Rain, Wind Speed, Humidity, Cloud
public class PredictionPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prediction_page);

        // Prediction button click
        Button btnPredict = findViewById(R.id.btnPredict);
        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editRain = findViewById(R.id.editRain);
                EditText editWindSpeed = findViewById(R.id.editWindSpeed);
                EditText editHumidity = findViewById(R.id.editHumidity);
                EditText editCloud = findViewById(R.id.editCloud);

                String rainValue = editRain.getText().toString().trim();
                String windSpeedValue = editWindSpeed.getText().toString().trim();
                String humidityValue = editHumidity.getText().toString().trim();
                String cloudValue = editCloud.getText().toString().trim();

                Intent intent = new Intent(PredictionPageActivity.this, PredictionResultActivity.class);
                intent.putExtra("rainValue", rainValue);
                intent.putExtra("windSpeedValue", windSpeedValue);
                intent.putExtra("humidityValue", humidityValue);
                intent.putExtra("cloudValue", cloudValue);

                startActivity(intent);
            }
        });

        // Diary button click
        ImageButton diaryButton = findViewById(R.id.diary);
        diaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the DiaryPageActivity
                Intent intent = new Intent(PredictionPageActivity.this, DiaryPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Home button click
        ImageButton homeButton = findViewById(R.id.home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PredictionPageActivity.this, HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Friends button click
        ImageButton friendButton = findViewById(R.id.friend);
        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PredictionPageActivity.this, FriendsPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Settings button click
        ImageButton setButton = findViewById(R.id.set);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PredictionPageActivity.this, SettingsPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // User button click
        ImageButton userButton = findViewById(R.id.user);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PredictionPageActivity.this, UserPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Note button click
        ImageButton noteButton = findViewById(R.id.note);
        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PredictionPageActivity.this, NotificationsPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }


}
