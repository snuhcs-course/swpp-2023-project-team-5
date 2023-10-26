package com.example.imPine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class
FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private List<Friend> friendList;
    private OnFriendClickListener listener;

    public FriendAdapter(List<Friend> friendList, OnFriendClickListener listener) {
        this.friendList = friendList;
        this.listener = listener;
    }

    public void updateFriendList(List<Friend> newFriendList) {
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

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friendName);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onFriendClick(friendList.get(position));
                }
            });
        }

        public void bind(Friend friend) {
            friendName.setText(friend.getUsername());
        }
    }

    public interface OnFriendClickListener {
        void onFriendClick(Friend friend);
    }
}
