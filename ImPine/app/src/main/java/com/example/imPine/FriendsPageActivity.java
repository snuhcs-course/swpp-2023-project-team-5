package com.example.imPine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imPine.model.FollowListResponse;
import com.example.imPine.network.ApiInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsPageActivity extends AppCompatActivity {

    SearchView friendSearchView;
    RecyclerView friendsRecyclerView;
    FriendAdapter friendAdapter;
    List<Friends> friends = new ArrayList<>();

    private void getAuthToken(MakePlantActivity.AuthTokenCallback authTokenCallback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            authTokenCallback.onTokenReceived("Bearer " + idToken);
                        } else {
                            authTokenCallback.onTokenError(task.getException());
                        }
                    });
        } else {
            authTokenCallback.onTokenError(new Exception("User not logged in."));
        }
    }

    public interface AuthTokenCallback {
        void onTokenReceived(String authToken);
        void onTokenError(Exception e);
    }

    private void setupUI() {
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

        friendSearchView = findViewById(R.id.friendSearchView);
        friendsRecyclerView = findViewById(R.id.friendsRecyclerView);

        friendAdapter = new FriendAdapter(friends, friend -> {
            // Navigate to friend detail activity
            Intent intent = new Intent(FriendsPageActivity.this, FriendsDetailActivity.class);
            intent.putExtra("FRIEND_NAME", friend.getName());
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendsRecyclerView.setAdapter(friendAdapter);

        // Sample data:
//        friends.add(new Friends("1", "Alice"));
//        friends.add(new Friends("2","Bob"));
//        friends.add(new Friends("3","Friend3"));
//        friends.add(new Friends("4","Friend4"));
//        friends.add(new Friends("5","Friend5"));
//        friends.add(new Friends("6","Friend6"));
//        //... add more friends
//        friendAdapter.notifyDataSetChanged();

//        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                int fromPosition = viewHolder.getAdapterPosition();
//                int toPosition = target.getAdapterPosition();
//                Collections.swap(friends, fromPosition, toPosition);
//                friendAdapter.notifyItemMoved(fromPosition, toPosition);
//                Log.d("friendsPageActivity", "Moved!!");
//                return true;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                // Do nothing as we don't want swipe functionality
//            }
//        });
//        touchHelper.attachToRecyclerView(friendsRecyclerView);


        // Set up the SearchView listener:
        friendSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Navigate to FriendsSearchResultsActivity with the search query
                Intent intent = new Intent(FriendsPageActivity.this, FriendsSearchResultsActivity.class);
                intent.putExtra("SEARCH_QUERY", query);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                return true; // Handled
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Can be used if you want to display results as user types
                return false; // Not handled
            }
        });


        // Diary button click
        ImageButton diaryButton = findViewById(R.id.diary);
        diaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the DiaryPageActivity
                Intent intent = new Intent(FriendsPageActivity.this, DiaryPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Home button click
        ImageButton homeButton = findViewById(R.id.home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsPageActivity.this, HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }

        });

        // Prediction button click
        ImageButton predictionButton = findViewById(R.id.prediction);
        predictionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsPageActivity.this, PredictionPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Settings button click
        ImageButton setButton = findViewById(R.id.set);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsPageActivity.this, SettingsPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // User button click
        ImageButton userButton = findViewById(R.id.piney);
//        userButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FriendsPageActivity.this, UsersPageActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                startActivity(intent);
//            }
//        });

        // Note button click
        ImageButton noteButton = findViewById(R.id.note);
        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsPageActivity.this, NotificationsPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check for new friends when the activity is resumed
        fetchFriends(this); // Fetch friends from API
    }

    private void fetchFriends(Context context) {
        ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);

        String authToken = AuthLoginActivity.getAuthToken(context);
        if (authToken == null) {
            // Handle the case when the authentication token is null (e.g., user not logged in)
            return;
        }

        Call<FollowListResponse> call = apiService.getFollowList(authToken);
        call.enqueue(new Callback<FollowListResponse>() {
            @Override
            public void onResponse(Call<FollowListResponse> call, Response<FollowListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Friends> newFriends = response.body().getFollows();
                    for (Friends friend : newFriends) {
                        Log.d("FriendsPageActivity", "Friend: " + friend.getName() + ", ID: " + friend.getId() + ", Email: " + friend.getEmail());
                        Log.d("FriendsPageActivityID", "Friend: " + friend.getName() + ", ID: " + friend.getId() + ", Email: " + friend.getEmail());
                    }
                    friendAdapter.updateFriendList(newFriends);
                }
            }

            @Override
            public void onFailure(Call<FollowListResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_page);

        setupUI();

//        fetchFriends(this); // Fetch friends from API

    }
}
