package com.example.imPine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.imPine.model.User;
import com.example.imPine.model.UserAdapter;
import com.example.imPine.model.UserListResponse;
import com.example.imPine.network.ApiInterface;
import com.example.imPine.network.RetrofitClient;
import okhttp3.ResponseBody;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsSearchResultsActivity extends AppCompatActivity {

    private RecyclerView searchResultsRecyclerView;
    private UserAdapter userAdapter;
    private Set<Integer> followedUserIds = new HashSet<>();
    String myUserName;
    private static final String TAG = "SearchResultsActivity";
    private void followUser(final int userId) {
        if (followedUserIds.contains(userId)) {
            Toast.makeText(this, "You are already following this user.", Toast.LENGTH_SHORT).show();
            return;
        }

        String authToken = AuthLoginActivity.getAuthToken(this);
        ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiService.followUser("Bearer " + authToken, userId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    followedUserIds.add(userId); // Mark this user as followed
                    Log.d(TAG, "Followed user successfully");
                    userAdapter.setFollowedStatus(userId, true);
                    Toast.makeText(FriendsSearchResultsActivity.this, "Followed successfully!", Toast.LENGTH_SHORT).show();
                    userAdapter.notifyItemChanged(userId); // If you have position, use it here
                } else {
                    Log.e(TAG, "Failed to follow user with code: " + response.code());
                    Toast.makeText(FriendsSearchResultsActivity.this, "Failed to follow user.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Failed to follow user", t);
                Toast.makeText(FriendsSearchResultsActivity.this, "Error occurred while following user.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_search_results);

        ImageView backButton = findViewById(R.id.backButton); // Get the "Back" button by ID

        backButton.setOnClickListener(v -> {
//            Intent intent = new Intent(FriendsSearchResultsActivity.this, FriendsPageActivity.class);
//            startActivity(intent);
            finish();
        });

        searchResultsRecyclerView = findViewById(R.id.searchResultRecyclerView);

        // Retrieve followed user IDs and setup RecyclerView
        myUserName = getIntent().getStringExtra("MYUSERNAME");
        List<Integer> followedIds = getIntent().getIntegerArrayListExtra("FOLLOWED_IDS");
        if (followedIds != null) {
            followedUserIds.clear();
            followedUserIds.addAll(followedIds);
        }

        userAdapter = new UserAdapter(new ArrayList<>(), user -> followUser(user.getId()), followedUserIds);
        searchResultsRecyclerView.setAdapter(userAdapter);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String query = getIntent().getStringExtra("SEARCH_QUERY");
        fetchSearchResults(query);
    }

    private void fetchSearchResults(String query) {
        String authToken = AuthLoginActivity.getAuthToken(this);
        ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
        Call<UserListResponse> call = apiService.searchUsers("Bearer " + authToken, query);

        call.enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserListResponse users = response.body();
                    List<User> userList = users.getUsers();

                    // Remove the current user from the list if present
                    Iterator<User> iterator = userList.iterator();
                    while (iterator.hasNext()) {
                        User user = iterator.next();
                        if (user.getName().equals(myUserName)) {
                            iterator.remove();
                        }
                    }

                    if (userList.isEmpty()) {
                        // No users found, show the empty search message
                        findViewById(R.id.tv_empty_search).setVisibility(View.VISIBLE);
                    } else {
                        // Users found, hide the empty search message
                        findViewById(R.id.tv_empty_search).setVisibility(View.GONE);
                        userAdapter.updateUserList(userList);
                    }
                } else {
                    Log.e(TAG, "Search results fetch failed with code: " + response.code());
                    // In case of failure, consider showing the empty search message or another error message
                    findViewById(R.id.tv_empty_search).setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                Log.e(TAG, "Search results fetch failed", t);
            }
        });
    }
}
