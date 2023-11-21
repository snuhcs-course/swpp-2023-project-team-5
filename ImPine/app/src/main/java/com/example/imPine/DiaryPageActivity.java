package com.example.imPine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Define the HomePageActivity class extending AppCompatActivity
public class DiaryPageActivity extends AppCompatActivity {

    // Define a RecyclerView and Adapter as instance variables
    private RecyclerView recyclerView;
    private DiaryAdapter adapter;

    private List<Diary> pineDiaries;

    private boolean isDialogShown = false;


    private void setPineyImage(int avatarValue) {
        int drawableResourceId = getAvatarDrawableId(avatarValue);
        ImageView pineappleAvatar = findViewById(R.id.piney);

        Glide.with(this)
                .load(drawableResourceId)
                .into(pineappleAvatar);
    }
    private int getAvatarDrawableId(int avatarValue) {
        switch (avatarValue) {
            case 0: return R.drawable.pine_avatar;
            case 1: return R.drawable.twofatty;
            case 2: return R.drawable.threelazy;
            case 3: return R.drawable.fourbrowny;
            case 4: return R.drawable.fivecooly;
            case 5: return R.drawable.sixalien;
            case 6: return R.drawable.sevenalien;
            case 7: return R.drawable.eightavatar;
            case 8: return R.drawable.nineavatar;
            default: return R.drawable.pine_avatar;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
       setPineyImage(HomePageActivity.avatarFromHome);
    }


    // Define the onCreate method that runs when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_page);

        setPineyImage(HomePageActivity.avatarFromHome);

        ImageButton pineyButton = findViewById(R.id.piney);

        // Load the animations
        final Animation swayRight = AnimationUtils.loadAnimation(this, R.anim.sway_right);
        final Animation swayLeft = AnimationUtils.loadAnimation(this, R.anim.sway_left);

        // Set animation listeners to create an infinite swaying effect for piney
        swayRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                pineyButton.startAnimation(swayLeft);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        swayLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                pineyButton.startAnimation(swayRight);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Start the animation
        pineyButton.startAnimation(swayRight);


        recyclerView = findViewById(R.id.recyclerView);

        // Setting the LayoutManager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a list of sample bucket list items using BucketListItem class
        pineDiaries = new ArrayList<>(Arrays.asList(
                new Diary("1", "Gave water to Piney", "2 cups", false),
                new Diary("2", "Piney Sick", "leaves fell off", false),
                new Diary("3", "Piney Recovers", "looking well", false),
                new Diary("4", "Piney blossoms", "pineapple fruit!", false),
                new Diary("5", "Piney's First Anniversary", "One year with us!", false),
                new Diary("6", "Trimmed Piney", "Removed dry leaves", false),
                new Diary("7", "Fertilized Piney", "Used organic fertilizer", false),
                new Diary("8", "Piney Grows", "Height increased by 5 inches", false),
                new Diary("9", "Birds Nested on Piney", "Found a bird nest", false),
                new Diary("10", "Piney during Rain", "Enjoyed the fresh rain", false),
                new Diary("11", "Piney's New Shoots", "Spotted some new growth", false),
                new Diary("12", "Piney in Winter", "Wrapped in protective layer", false),
                new Diary("13", "Piney under Sun", "Getting some sunlight", false),
                new Diary("14", "Piney's Soil", "Replenished top soil", false),
                new Diary("15", "Piney's Companions", "Planted flowers around", false)
        ));
        // Add a new diary
        FloatingActionButton fabAddDiary = findViewById(R.id.fab_add_item);
        fabAddDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryPageActivity.this, DiaryNewActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Create a new adapter with the sample list of BucketListItem objects
        adapter = new DiaryAdapter(pineDiaries);

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

        // logout button click
        ImageButton outButton = findViewById(R.id.logOut);
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryPageActivity.this, AuthLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Diary receivedDiary = (Diary) data.getSerializableExtra("newDiary");
            if (receivedDiary != null) {
                pineDiaries.add(receivedDiary);
                receivedDiary.setId(String.valueOf(pineDiaries.size()));
                adapter.notifyDataSetChanged();
            }
        }

    }
}
