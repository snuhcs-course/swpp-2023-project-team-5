package com.example.imPine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.imPine.model.FollowListResponse;
import com.example.imPine.model.FriendAdapter;
import com.example.imPine.model.Friends;
import com.example.imPine.model.PlantResponse;
import com.example.imPine.network.ApiInterface;
import com.example.imPine.network.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsPageActivity extends AppCompatActivity {

    SearchView friendSearchView;
    RecyclerView friendsRecyclerView;
    FriendAdapter friendAdapter;
    List<Friends> friends = new ArrayList<>();
    TextView tvEmptyFriend, emptySearch;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Set<Integer> followedUserIds = new HashSet<>();


    private void unfollowFriend(int userId) {
        ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
        String token = "Bearer " + AuthLoginActivity.getAuthToken(this);

        apiService.unfollowUser(token, userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    for (Integer i : followedUserIds) {
                        Log.d("removingFriend", "FriendID: " + i);

                    }
                    followedUserIds.remove(userId); // Update the set after unfollowing
                    for (Integer i : followedUserIds) {
                        Log.d("removingFriend", "FriendID: " + i);
                    }
                    Toast.makeText(FriendsPageActivity.this, "Successfully unfollowed the user", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FriendsPageActivity.this, "Failed to unfollow the user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(FriendsPageActivity.this, "Network error or API is down", Toast.LENGTH_SHORT).show();
            }
        });
    }
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
            ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
            String token = "Bearer " + AuthLoginActivity.getAuthToken(this);

            // Make the API call to get the friend's plants
            apiService.getUserPlants(token, Integer.toString(friend.getId())).enqueue(new Callback<PlantResponse>() {
                @Override
                public void onResponse(Call<PlantResponse> call, Response<PlantResponse> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().getPlants().isEmpty()) {
                        // The user has plants, navigate to FriendsDetailActivity
                        Intent intent = new Intent(FriendsPageActivity.this, FriendsDetailActivity.class);
                        intent.putExtra("ID", friend.getId());
                        intent.putExtra("friendName", friend.getName());
                        // Launch the activity
                        activityResultLauncher.launch(intent);
                    } else {
                        new AlertDialog.Builder(FriendsPageActivity.this)
                                .setTitle("Unfollow User")
                                .setMessage("This user doesn't have a pineapple profile! Do you want to unfollow this user?")
                                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                                    // User chose to unfollow, make the API call
                                    unfollowFriend(friend.getId());
                                    // Refresh or update the necessary data or views here
                                    fetchFriends(FriendsPageActivity.this); // Assuming this fetches and updates the list
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<PlantResponse> call, Throwable t) {
                    // API call failed, show a toast message
                    Toast.makeText(FriendsPageActivity.this, "Failed to retrieve friend's plants", Toast.LENGTH_SHORT).show();
                }
            });
        });

        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendsRecyclerView.setAdapter(friendAdapter);
        friendSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(FriendsPageActivity.this, FriendsSearchResultsActivity.class);
                intent.putExtra("SEARCH_QUERY", query);
                intent.putExtra("MYUSERNAME", HomePageActivity.getMyUserName());
                intent.putExtra("FOLLOWED_IDS", new ArrayList<>(followedUserIds)); // Passing the list of followed IDs
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
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

        // Logout button click
        ImageButton outButton = findViewById(R.id.logOut);
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(FriendsPageActivity.this)
                        .setTitle("Logout Confirmation")
                        .setMessage("Do you really want to logout?")
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                            // logout
                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove(getString(R.string.saved_auth_token));
                            editor.apply();
                            // Handle the logout logic here
                            Intent intent = new Intent(FriendsPageActivity.this, AuthLoginActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        setPineyImage(HomePageActivity.avatarFromHome);

        SearchView searchView = findViewById(R.id.friendSearchView);

        // Prevent automatic focus
        searchView.setFocusable(false);
        searchView.setIconifiedByDefault(true);
        searchView.clearFocus();

        // Explicitly manage focus
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
                searchView.setFocusable(true);
                searchView.requestFocusFromTouch();
            }
        });
        // Check for new friends when the activity is resumed
        fetchFriends(this); // Fetch friends from API

    }
    private void updateEmptyFriendView(FriendAdapter fa) {
        if (fa.getFriendList().isEmpty()) {
            tvEmptyFriend.setVisibility(View.VISIBLE);
        } else {
            tvEmptyFriend.setVisibility(View.GONE);
        }
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
                        followedUserIds.add(friend.getId());
                    }
                    friendAdapter.updateFriendList(newFriends);
                    updateEmptyFriendView(friendAdapter);  // Update the visibility of the empty view
                }
            }

            @Override
            public void onFailure(Call<FollowListResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_page);
        // Initialize the ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Get data from the result
                        Intent data = result.getData();
                        if (data != null) {
                            int unfollowedFriendId = data.getIntExtra("unfollowedFriendId", -1);
                            if (unfollowedFriendId != -1) {
                                followedUserIds.remove(unfollowedFriendId);
                                fetchFriends(this);
                            }
                        }
                    }
                });
        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);

        SearchView searchView = findViewById(R.id.friendSearchView);
        tvEmptyFriend = findViewById(R.id.tv_empty_friend);

        // Set the SearchView to be expanded by default
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.clearFocus();

        // Set OnClickListener to handle focus
        searchView.setOnClickListener(v -> {
            searchView.setIconified(false);
            searchView.setFocusable(true);
            searchView.requestFocusFromTouch();
        });

        // Accessing the TextView inside SearchView
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);

        // Set custom font
        Typeface customFont = ResourcesCompat.getFont(this, R.font.short_stack);
        textView.setTypeface(customFont);

        // Optional: Adjust text size and padding as needed
         textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
         textView.setPadding(0, 0, 0, 0);

        setupUI();

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
}
