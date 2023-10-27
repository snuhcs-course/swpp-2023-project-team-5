package com.example.imPine;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


// Define the adapter class and extend it from RecyclerView.Adapter
public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.PineDiaryViewHolder> {

    private List<Diary> pineDiaries;

    public DiaryAdapter(List<Diary> pineDiaries) {
        this.pineDiaries = pineDiaries;
    }

    @NonNull
    @Override
    public PineDiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each diary item
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_item, parent, false);

        // Create and return a new ViewHolder that wraps the inflated layout
        return new PineDiaryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PineDiaryViewHolder holder, int position) {
        // Get the diary object for the given position
        Diary diary = pineDiaries.get(position);

        // Set the values on holder's views
        holder.titleTextView.setText(diary.getId()+ ". " + diary.getTitle());
        String[] descriptionLines = diary.getDescription().split("\n", 2); // Split by the newline, limit to 2 parts
        String firstLineOfDescription = descriptionLines[0];
        holder.descriptionTextView.setText(firstLineOfDescription);

        // When a diary item is clicked, start the DiaryDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DiaryDetailActivity.class);

            // Pass the diary details to the DiaryDetailActivity
            intent.putExtra("DIARY_TITLE", diary.getTitle());
            intent.putExtra("DIARY_DESCRIPTION", diary.getDescription());

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pineDiaries.size();
    }

    public static class PineDiaryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        public PineDiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.diary_title);
            descriptionTextView = itemView.findViewById(R.id.diary_description);
            // Initialize other views if you have them
        }
    }
}