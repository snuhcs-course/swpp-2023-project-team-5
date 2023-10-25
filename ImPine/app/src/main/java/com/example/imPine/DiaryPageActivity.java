package com.example.imPine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Define the HomePageActivity class extending AppCompatActivity
public class DiaryPageActivity extends AppCompatActivity {

    // Define a RecyclerView and Adapter as instance variables
    private RecyclerView recyclerView;
    private PineDiariesAdapter adapter;

    private List<PineDiary> pineDiaries;


    // Define the onCreate method that runs when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_page);

        recyclerView = findViewById(R.id.recyclerView);

        // Setting the LayoutManager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a list of sample bucket list items using BucketListItem class
        pineDiaries = new ArrayList<>(Arrays.asList(
                new PineDiary("Gave water to Piney", "2 cups", false),
                new PineDiary("Piney Sick", "leaves fell off", false),
                new PineDiary("Piney Recovers", "looking well", false),
                new PineDiary("Piney blossoms", "pineapple fruit!", false)
        ));

        // Add a new diary
        FloatingActionButton fabAddDiary = findViewById(R.id.fab_add_item);
        fabAddDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryPageActivity.this, NewDiaryActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Create a new adapter with the sample list of BucketListItem objects
        adapter = new PineDiariesAdapter(pineDiaries);

        // Set the adapter to the RecyclerView to display the items
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(pineDiaries, fromPosition, toPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Handle deletion if needed in the future
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        // Home button click
        ImageButton homeButton = findViewById(R.id.home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the DiaryPageActivity
                Intent intent = new Intent(DiaryPageActivity.this, HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Prediction button click
        ImageButton predictionButton = findViewById(R.id.prediction);
        predictionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryPageActivity.this, PredictionPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Friends button click
        ImageButton friendButton = findViewById(R.id.friend);
        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryPageActivity.this, FriendsPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Settings button click
        ImageButton setButton = findViewById(R.id.set);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryPageActivity.this, SettingsPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // User button click
        ImageButton userButton = findViewById(R.id.user);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryPageActivity.this, UserPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Note button click
        ImageButton noteButton = findViewById(R.id.note);
        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryPageActivity.this, NotificationsPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            PineDiary receivedDiary = (PineDiary) data.getSerializableExtra("newDiary");
            if (receivedDiary != null) {
                pineDiaries.add(receivedDiary);
                adapter.notifyDataSetChanged();
            }
        }

    }
}
