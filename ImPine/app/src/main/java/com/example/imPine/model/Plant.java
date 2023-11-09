package com.example.imPine.model;

import java.util.Date;

public class Plant {
    private String name;
    private int height;

    private String lastWatered;
    private String status;

    public Plant(String name, int height) {
        this.name = name;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public String getLastWatered() {
        return lastWatered;
    }
    public String getStatus() {
        return status;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setLastWatered(String lastWatered) {
        this.lastWatered = lastWatered;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
