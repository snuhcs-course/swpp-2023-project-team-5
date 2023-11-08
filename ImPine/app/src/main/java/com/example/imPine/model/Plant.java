package com.example.imPine.model;

public class Plant {
    private String name;
    private String height;

    public Plant(String name, String height) {
        this.name = name;
        this.height = height;
    }

    // Getters and setters are not always necessary, but they are a good practice.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
