package com.example.imPine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

    private boolean isDialogShown = false;


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
                new PineDiary("1", "Gave water to Piney", "2 cups", false),
                new PineDiary("2", "Piney Sick", "leaves fell off", false),
                new PineDiary("3", "Piney Recovers", "looking well", false),
                new PineDiary("4", "Piney blossoms", "pineapple fruit!", false),
                new PineDiary("5", "Piney's First Anniversary", "One year with us!", false),
                new PineDiary("6", "Trimmed Piney", "Removed dry leaves", false),
                new PineDiary("7", "Fertilized Piney", "Used organic fertilizer", false),
                new PineDiary("8", "Piney Grows", "Height increased by 5 inches", false),
                new PineDiary("9", "Birds Nested on Piney", "Found a bird nest", false),
                new PineDiary("10", "Piney during Rain", "Enjoyed the fresh rain", false),
                new PineDiary("11", "Piney's New Shoots", "Spotted some new growth", false),
                new PineDiary("12", "Piney in Winter", "Wrapped in protective layer", false),
                new PineDiary("13", "Piney under Sun", "Getting some sunlight", false),
                new PineDiary("14", "Piney's Soil", "Replenished top soil", false),
                new PineDiary("15", "Piney's Companions", "Planted flowers around", false)
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

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,0) {
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
                // deletion
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
                receivedDiary.setId(String.valueOf(pineDiaries.size()));
                adapter.notifyDataSetChanged();
            }
        }

    }
}
