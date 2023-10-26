package com.example.imPine;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FriendsDetailActivity extends AppCompatActivity {

    private TextView friendDetailName;
    private List<PineDiary> samplePineDiaries = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_detail);

        friendDetailName = findViewById(R.id.friendDetailName);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("FRIEND_NAME")) {
            String friendName = intent.getStringExtra("FRIEND_NAME");
            friendDetailName.setText(friendName);
        } else {
            // Handle the case where no name was passed to this activity.
            // Maybe finish the activity and show a toast.
            finish();
            showToast("Friend details not available.");
        }


        RecyclerView diariesRecyclerView = findViewById(R.id.diariesRecyclerView);

        // Populate with some sample PineDiaries for demo purposes
        samplePineDiaries.add(new PineDiary("1", "Sample Diary Title 1", "Sample Diary Description 1", false));
        samplePineDiaries.add(new PineDiary("2", "Sample Diary Title 2", "Sample Diary Description 2", false));
        samplePineDiaries.add(new PineDiary("3", "Sample Diary Title 3", "Sample Diary Description 3", false));

        PineDiariesAdapter pineDiariesAdapter = new PineDiariesAdapter(samplePineDiaries);
        diariesRecyclerView.setAdapter(pineDiariesAdapter);
        diariesRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        ImageView pineappleAvatar = findViewById(R.id.pineappleAvatar);

        // Load the animations
        final Animation swayRight = AnimationUtils.loadAnimation(this, R.anim.sway_right);
        final Animation swayLeft = AnimationUtils.loadAnimation(this, R.anim.sway_left);

        // Set animation listeners to create an infinite swaying effect
        swayRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                pineappleAvatar.startAnimation(swayLeft);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        swayLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                pineappleAvatar.startAnimation(swayRight);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Start the animation
        pineappleAvatar.startAnimation(swayRight);

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
