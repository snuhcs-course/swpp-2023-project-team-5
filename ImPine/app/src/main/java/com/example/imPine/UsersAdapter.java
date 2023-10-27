package com.example.imPine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<Users> data; //

    public UsersAdapter(List<Users> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users item = data.get(position);
        // Bind data to your list item's views here
        holder.textUserImage.setText(item.getUserImageText());
        holder.textUserName.setText(item.getUserNameText());
        holder.textUserID.setText(item.getUserIDText());
        Glide.with(holder.itemView)
                .load(item.getEditImageResource())
                .into(holder.editImage1);
        Glide.with(holder.itemView)
                .load(item.getEditImageResource())
                .into(holder.editImage2);
        Glide.with(holder.itemView)
                .load(item.getEditImageResource())
                .into(holder.editImage3);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Define views in your list item layout here
        TextView textUserImage;
        TextView textUserName;
        TextView textUserID;
        ImageView editImage1;
        ImageView editImage2;
        ImageView editImage3;
        public ViewHolder(View itemView) {
            super(itemView);
            textUserImage = itemView.findViewById(R.id.textUserImage);
            textUserName = itemView.findViewById(R.id.textUserName);
            textUserID = itemView.findViewById(R.id.textUserID);
            editImage1 = itemView.findViewById(R.id.editImage1);
            editImage2 = itemView.findViewById(R.id.editImage2);
            editImage3 = itemView.findViewById(R.id.editImage3);
        }
    }
}

