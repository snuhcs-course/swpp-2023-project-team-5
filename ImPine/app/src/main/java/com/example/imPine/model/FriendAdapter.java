package com.example.imPine.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.imPine.AuthLoginActivity;
import com.example.imPine.R;
import com.example.imPine.network.ApiInterface;
import com.example.imPine.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    public interface OnFriendClickListener {
        void onFriendClick(Friends friend);
    }
    private List<Friends> friendList;
    private OnFriendClickListener listener;


    public List<Friends> getFriendList() {
        return friendList;
    }

    public FriendAdapter(List<Friends> friendList, OnFriendClickListener listener) {
        this.friendList = friendList;
        this.listener = listener;
    }

    public void updateFriendList(List<Friends> newFriendList) {
        this.friendList = newFriendList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        holder.bind(friendList.get(position));
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        TextView friendEmail; // TextView for email
        ImageView friendPlantImage; // ImageView for the plant image

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friendName);
            friendEmail = itemView.findViewById(R.id.friendEmail);
            friendPlantImage = itemView.findViewById(R.id.friendPlantImage); // Initialize the ImageView

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onFriendClick(friendList.get(position));
                }
            });
        }

        public void bind(Friends friend) {
            friendName.setText(friend.getName());
            friendEmail.setText(friend.getEmail());
            loadPlantImage(friend.getId(), itemView.getContext());
        }

        private void loadPlantImage(int userId, Context context) {
            String authToken = AuthLoginActivity.getAuthToken(context);
            if (authToken == null) {
                Log.e("FriendAdapter", "Auth token is null");
                return;
            }

            ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
            Call<PlantResponse> call = apiService.getUserPlants("Bearer " + authToken, String.valueOf(userId));

            call.enqueue(new Callback<PlantResponse>() {
                @Override
                public void onResponse(Call<PlantResponse> call, Response<PlantResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Plant> plants = response.body().getPlants();
                        if (!plants.isEmpty()) {
                            // Assuming Plant class has a method to get image URL
                            String imageUrl = plants.get(0).getImage();
                            Glide.with(context)
                                    .load(imageUrl)
                                    .circleCrop()
                                    .into(friendPlantImage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<PlantResponse> call, Throwable t) {
                    Log.e("FriendViewHolder", "Failed to load plant image", t);
                }
            });
        }
    }
}
