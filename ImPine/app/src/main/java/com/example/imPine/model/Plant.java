package com.example.imPine.model;

import java.util.Date;

public class Plant {
    private String name;
    private int height;
    private Date lastWatered;

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

    public Date getLastWatered() {
        return lastWatered;
    }

    public void setLastWatered(Date lastWatered) {
        this.lastWatered = lastWatered;
    }
}
