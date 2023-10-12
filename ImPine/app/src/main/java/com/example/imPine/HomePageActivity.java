package com.example.imPine;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

// Define the HomePageActivity class extending AppCompatActivity
public class HomePageActivity extends AppCompatActivity {

    // Define a RecyclerView and Adapter as instance variables
    private RecyclerView recyclerView;
    private pineDiariesAdapter adapter;

    // Define the onCreate method that runs when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

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
        adapter = new pineDiariesAdapter(pineDiaries);

        // Set the adapter to the RecyclerView to display the items
        recyclerView.setAdapter(adapter);
    }
}
