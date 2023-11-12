package com.example.imPine.model;

import java.util.Date;

public class Plant {
    private String name;
    private int height;

    private String lastWatered;
    private String status;

    private String image_src;
    private int avatar;
    private int id;
    public Plant() {
    }

    public Plant(String name, int height) {
        this.name = name;
        this.height = height;
    }
    public Plant(String name, int height, String image_src) {
        this.name = name;
        this.height = height;
        this.image_src = image_src;
    }

    public int getPlant_id() {
        return id;
    }

    public void setPlant_id(int plant_id) {
        this.id = plant_id;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getImage() {
        return image_src;
    }

    public void setImage(String image_src) {
        this.image_src = image_src;
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
