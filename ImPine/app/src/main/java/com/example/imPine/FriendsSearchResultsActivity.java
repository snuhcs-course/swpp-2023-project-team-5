package com.example.imPine;

import static android.content.Intent.getIntent;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView searchResultsRecyclerView;
    private OkHttpClient client;
    private FriendAdapter friendAdapter;  // Adapter to display search results

    private List<Friend> getDemoSearchResults() {
        List<Friend> mockList = new ArrayList<>();

        // Add some mock friends to the list
        mockList.add(new Friend("1", "Alice"));
        mockList.add(new Friend("2", "Bob"));
        mockList.add(new Friend("3", "Charlie"));
        mockList.add(new Friend("4", "David"));
        mockList.add(new Friend("5", "AAA"));
        mockList.add(new Friend("6", "BBB"));
        mockList.add(new Friend("7", "CCC"));
        mockList.add(new Friend("8", "DDD"));
        mockList.add(new Friend("9", "EEE"));
        mockList.add(new Friend("10", "FFF"));
        mockList.add(new Friend("11", "GGG"));
        mockList.add(new Friend("12", "HHH"));


        return mockList;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_search_results);

        searchResultsRecyclerView = findViewById(R.id.searchResultRecyclerView);
        client = new OkHttpClient();

        // Initialize the adapter with empty list and null listener (modify if you have a listener)
        friendAdapter = new FriendAdapter(new ArrayList<>(), null);
        searchResultsRecyclerView.setAdapter(friendAdapter);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        //for demo
        boolean isDemoMode = true;

        if (isDemoMode) {
            List<Friend> demoFriends = getDemoSearchResults();
            friendAdapter.updateFriendList(demoFriends);
        } else {
            String query = getIntent().getStringExtra("SEARCH_QUERY");
            fetchSearchResults(query);
        }

        friendAdapter = new FriendAdapter(new ArrayList<>(), null);
        searchResultsRecyclerView.setAdapter(friendAdapter);

        // For demo purposes: load mock data
        List<Friend> demoFriends = getDemoSearchResults();
        friendAdapter.updateFriendList(demoFriends);
    }

    private void fetchSearchResults(String query) {
        final String url = "http://localhost:8000/api/search/?q=" + query;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the error
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        final List<Friend> friendList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Friend friend = new Friend(String.valueOf(jsonObject.getInt("id")), jsonObject.getString("username"));
                            friendList.add(friend);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Update your RecyclerView with the friends list
                                friendAdapter.updateFriendList(friendList);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
