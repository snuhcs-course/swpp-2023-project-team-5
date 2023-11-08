package com.example.imPine.model;

public class UserID {
    private String id; // The variable name should match the key in the JSON response
    private String name; // Additional user information, if any, that might come with the ID
    // ... you can add more fields to match the JSON response

    // Constructor
    public UserID(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter for ID
    public String getId() {
        return id;
    }

    // Getter for Name
    public String getName() {
        return name;
    }

    // Setter for ID (if needed for JSON deserialization)
    public void setId(String id) {
        this.id = id;
    }

    // Setter for Name (if needed for JSON deserialization)
    public void setName(String name) {
        this.name = name;
    }

    // You can override the toString() method if you want to easily print the details of UserID
    @Override
    public String toString() {
        return "UserID{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    // ... any additional methods you might need
}