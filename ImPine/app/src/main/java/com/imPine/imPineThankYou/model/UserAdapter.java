package com.imPine.imPineThankYou.model;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imPine.imPineThankYou.R;

import java.util.List;
import java.util.Set;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Set<Integer> followedUserIds;

    public void setFollowedStatus(int userId, boolean isFollowed) {
        if (isFollowed) {
            followedUserIds.add(userId);
        } else {
            followedUserIds.remove(userId);
        }
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getId() == userId) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    public UserAdapter(List<User> userList, UserClickListener clickListener, Set<Integer> followedUserIds) {
        this.userList = userList;
        this.clickListener = clickListener;
        this.followedUserIds = followedUserIds;
    }

    public interface UserClickListener {
        void onFollowClick(User user);
    }

    private List<User> userList;
    private final UserClickListener clickListener;

    public UserAdapter(List<User> userList, UserClickListener clickListener) {
        this.userList = userList;
        this.clickListener = clickListener;
    }

    public void updateUserList(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userNameTextView.setText(user.getName());
        holder.userEmailTextView.setText(user.getEmail());

        // Update follow button appearance based on follow status
        if (followedUserIds.contains(user.getId())) {
            holder.followImageView.setImageResource(R.drawable.follow);
            holder.applyShadeToFollowImage(holder.followImageView); // Apply shade if followed
        } else {
            holder.followImageView.setImageResource(R.drawable.follow);
            holder.followImageView.clearColorFilter(); // Remove shade if not followed
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView userEmailTextView;
        ImageView followImageView;

        UserViewHolder(View itemView, final UserClickListener clickListener) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.friendName);
            userEmailTextView = itemView.findViewById(R.id.friendEmail);
            followImageView = itemView.findViewById(R.id.follow);

            followImageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (clickListener != null && position != RecyclerView.NO_POSITION) {
                    clickListener.onFollowClick(userList.get(position));
                }
            });
        }

        private void applyShadeToFollowImage(ImageView imageView) {
            imageView.setColorFilter(Color.argb(150, 50, 50, 50), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }
}
