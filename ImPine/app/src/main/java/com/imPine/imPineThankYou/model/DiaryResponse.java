package com.imPine.imPineThankYou.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DiaryResponse {
    @SerializedName("diaries")
    private List<Diary> diaries;

    // Constructor, getters, and setters
    public List<Diary> getDiaries() {
        return diaries;
    }

    public void setDiaries(List<Diary> diaries) {
        this.diaries = diaries;
    }
}