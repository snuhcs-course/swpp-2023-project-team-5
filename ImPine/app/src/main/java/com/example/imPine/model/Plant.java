package com.example.imPine.model;

import java.util.Date;

public class Plant {
    private int id;
    private String name;
    private int user_id; // Assuming user_id is used instead of a User object for simplicity
    private Date birthday;
    private int height;
    private String status;
    private Date last_watered;
    private Date created_at;
    private Date updated_at;

    // Default constructor
    public Plant() {
    }

    // Parametrized constructor
    public Plant(int id, String name, int user_id, Date birthday, int height, String status, Date last_watered, Date created_at, Date updated_at) {
        this.id = id;
        this.name = name;
        this.user_id = user_id;
        this.birthday = birthday;
        this.height = height;
        this.status = status;
        this.last_watered = last_watered;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastWatered() {
        return last_watered;
    }

    public void setLastWatered(Date last_watered) {
        this.last_watered = last_watered;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(Date updated_at) {
        this.updated_at = updated_at;
    }

    // Optional: Override toString() for easy printing of Plant details
    @Override
    public String toString() {
        return "Plant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", user_id=" + user_id +
                ", birthday=" + birthday +
                ", height=" + height +
                ", status='" + status + '\'' +
                ", last_watered=" + last_watered +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
