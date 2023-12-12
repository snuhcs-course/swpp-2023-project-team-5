package com.imPine.imPineThankYou.model;

public class Friends {
    private int id;
    private String name;
    private String email;



    public Friends(int id, String username) {
        this.id = id;
        this.name = username;
    }


    public Friends(int id, String username, String email) {
        this.id = id;
        this.name = username;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

}
