package com.imPine.imPineThankYou.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.imPine.imPineThankYou.R;

import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> implements DiaryObserver {

    private List<Diary> diaries;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(Diary diary);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener =  listener;
    }

    public DiaryAdapter(List<Diary> diaries) {
        this.diaries = diaries;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_item, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        Diary diary = diaries.get(position);
        // Truncate the title if it's longer than 17 characters and append "..."
        String title = diary.getTitle();
        if (title.length() > 15) {
            title = title.substring(0, 15) + "...";
        }
        holder.titleTextView.setText(title);
        holder.categoryTextView.setText(diary.getCategory());
        holder.categoryTextView.setText(diary.getCategory());

        // Define a character limit and find the index of the first newline
        String content = diary.getContent();
        int charLimit = 20; // Character limit, e.g., 100 characters
        int newlineIndex = content.indexOf("\n");

        // Determine the ending index for the substring based on newline or char limit
        int endIndex = Math.min(charLimit, content.length());
        if (newlineIndex >= 0 && newlineIndex < endIndex) {
            endIndex = newlineIndex;
        }

        // Truncate the content at the determined index and add ellipsis if needed
        String displayedContent = content.substring(0, endIndex);
        if (content.length() > endIndex) {
            displayedContent += "..."; // Append ellipsis if the content is truncated
        }

        holder.contentTextView.setText(displayedContent);

        if (diary.getIsPrivate()) {
            holder.lockStatusImageView.setImageResource(R.drawable.lock);
        } else {
            holder.lockStatusImageView.setImageResource(R.drawable.unlock);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (listener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(diaries.get(adapterPosition));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return diaries.size();
    }

    @Override
    public void onDiaryDataChanged(List<Diary> newDiaries) {
        this.diaries = newDiaries;
        notifyDataSetChanged();
    }

    static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        ImageView lockStatusImageView; // Add this for the lock status image

        TextView categoryTextView;

        public DiaryViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.diary_title);
            contentTextView = view.findViewById(R.id.diary_content);
            lockStatusImageView = view.findViewById(R.id.lockStatus);
            categoryTextView = view.findViewById(R.id.diary_category);
        }
    }



}
