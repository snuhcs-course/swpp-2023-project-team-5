package com.example.imPine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

// Define the HomePageActivity class extending AppCompatActivity
public class DiaryPageActivity extends AppCompatActivity {

    // Define a RecyclerView and Adapter as instance variables
    private RecyclerView recyclerView;
    private PineDiariesAdapter adapter;

    // Define the onCreate method that runs when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_page);

        recyclerView = findViewById(R.id.recyclerView);

        // Setting the LayoutManager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a list of sample bucket list items using BucketListItem class
        List<PineDiary> pineDiaries = Arrays.asList(
                new PineDiary("1", "Gave water", "2 cups", false),
                new PineDiary("2", "Gave water", "2 cups", false),
                new PineDiary("3", "Gave water", "2 cups", false),
                new PineDiary("4", "Gave water", "2 cups", false),
                new PineDiary("5", "Gave water", "2 cups", false),
                new PineDiary("6", "Gave water", "2 cups", false),
                new PineDiary("7", "Gave water", "2 cups", false),
                new PineDiary("8", "Gave water", "2 cups", false),
                new PineDiary("9", "Gave water", "2 cups", false),
                new PineDiary("10", "Gave water", "2 cups", false),
                new PineDiary("11", "Gave water", "2 cups", false)

        );

        // Create a new adapter with the sample list of BucketListItem objects
        adapter = new PineDiariesAdapter(pineDiaries);

        // Set the adapter to the RecyclerView to display the items
        recyclerView.setAdapter(adapter);

        // Home button click
        ImageButton homeButton = findViewById(R.id.home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the DiaryPageActivity
                Intent intent = new Intent(DiaryPageActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
        });

        // Prediction button click
        ImageButton predictionButton = findViewById(R.id.prediction);
        predictionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryPageActivity.this, PredictionPageActivity.class);
                startActivity(intent);
            }
        });

        // Friends button click
        ImageButton friendButton = findViewById(R.id.friend);
        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryPageActivity.this, FriendsPageActivity.class);
                startActivity(intent);
            }
        });

        // Settings button click
        ImageButton setButton = findViewById(R.id.set);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryPageActivity.this, SettingsPageActivity.class);
                startActivity(intent);
            }
        });

        // User button click
        ImageButton userButton = findViewById(R.id.user);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryPageActivity.this, UserPageActivity.class);
                startActivity(intent);
            }
        });

        // Note button click
        ImageButton noteButton = findViewById(R.id.note);
        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DiaryPageActivity.this, "Already at Notification Page!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
