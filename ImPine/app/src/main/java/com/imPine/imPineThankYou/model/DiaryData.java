package com.imPine.imPineThankYou.model;

import java.util.ArrayList;
import java.util.List;

interface DiaryObserver {
    void onDiaryDataChanged(List<Diary> diaries);
}

public class DiaryData {
    private List<Diary> diaries = new ArrayList<>();
    private List<DiaryObserver> observers = new ArrayList<>();

    public void addDiary(Diary diary) {
        diaries.add(diary);
        notifyObservers();
    }

    public void setDiaries(List<Diary> diaries) {
        this.diaries = diaries;
        notifyObservers();
    }

    public void registerObserver(DiaryObserver observer) {
        observers.add(observer);
    }

    public void unregisterObserver(DiaryObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (DiaryObserver observer : observers) {
            observer.onDiaryDataChanged(diaries);
        }
    }
}
