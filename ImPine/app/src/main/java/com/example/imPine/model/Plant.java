package com.example.imPine.model;

import java.util.Date;

public class Plant {
    private String name;
    private String height;

    // You can remove the other fields like id, created_at, updated_at, and birthday
    // because these are set automatically by the server or not required in the POST request.

    // Default constructor
    public Plant() {
    }

    // Parametrized constructor
    public Plant(String name, String height) {
        this.name = name;
        this.height = height;
    }

    // Getters and setters for the fields that are required in the POST request
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

//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public Date getLastWatered() {
//        return last_watered;
//    }
//
//    public void setLastWatered(Date last_watered) {
//        this.last_watered = last_watered;
//    }

    // ToString method only for the fields that are in use
    @Override
    public String toString() {
        return "Plant{" +
                "name='" + name + '\'' +
                ", height=" + height +
//                ", status='" + status + '\'' +
//                ", last_watered=" + last_watered +
                '}';
    }
}
