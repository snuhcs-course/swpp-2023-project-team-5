package com.example.imPine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_page); // Replace with your actual layout resource ID

        Button getStartedButton = findViewById(R.id.btn_get_started); // Replace with your actual button ID
        getStartedButton.setOnClickListener(v -> navigateToMakePlantActivity());
    }

    private void navigateToMakePlantActivity() {
        Intent intent = new Intent(TutorialActivity.this, MakePlantActivity.class);
        startActivity(intent);
    }
}
