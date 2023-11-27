package com.example.imPine.model;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Plant {
    private String name;
    private int height;

    private String last_watered;
    private String created_at;
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

    public String getLast_watered() {
        return last_watered;
    }
    public String getStatus() {
        return status;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setLast_watered(String last_watered) {
        this.last_watered = last_watered;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Method to format the created_at date string to "yyyy-MM-dd"
    public String getFormattedCreatedAt() {
        // The current format of date string
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

        // The desired format for the date string
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            // Parse the input date string into a Date object
            Date date = inputFormat.parse(this.created_at);

            // Format the Date object into the desired date string format
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Return null or some default value if parsing fails
        }
    }


}
