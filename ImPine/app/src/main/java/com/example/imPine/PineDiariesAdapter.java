package com.example.imPine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Define the adapter class and extend it from RecyclerView.Adapter
public class PineDiariesAdapter extends RecyclerView.Adapter<PineDiariesAdapter.BucketListViewHolder> {

    private List<PineDiary> pineDiaries;

    public PineDiariesAdapter(List<PineDiary> pineDiaries) {
        this.pineDiaries = pineDiaries;
    }

    @NonNull
    @Override
    public BucketListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bucket_list_item, parent, false);
        return new BucketListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BucketListViewHolder holder, int position) {
        PineDiary item = pineDiaries.get(position);
        holder.bucketListItemTitle.setText(item.getTitle());
        holder.bucketListItemDescription.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return pineDiaries.size();
    }

    public static class BucketListViewHolder extends RecyclerView.ViewHolder {
        public TextView bucketListItemTitle;
        public TextView bucketListItemDescription;

        public BucketListViewHolder(View view) {
            super(view);
            bucketListItemTitle = view.findViewById(R.id.bucketListItemTitle);
            bucketListItemDescription = view.findViewById(R.id.bucketListItemDescription);
        }
    }
}