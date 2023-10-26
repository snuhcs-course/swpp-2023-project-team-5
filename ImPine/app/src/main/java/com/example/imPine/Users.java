package com.example.imPine;

public class Users {
    private String userImageText;
    private String userNameText;
    private String userIDText;

    private int editImageResource;

    public Users(String userID, String userName) {
        this.userNameText = userNameText;
        this.userIDText = userIDText;
    }

    public String getUserImageText() {
        return userImageText;
    }

    public void setUserImageText(String userImageText) {
        this.userImageText = userImageText;
    }

    public String getUserNameText() {
        return userNameText;
    }

    public void setUserNameText(String userNameText) {
        this.userNameText = userNameText;
    }

    public String getUserIDText() {
        return userIDText;
    }

    public void setUserIDText(String userIDText) {
        this.userIDText = userIDText;
    }

    public int getEditImageResource() {
        return editImageResource;
    }

    public void setEditImageResource(int editImageResource) {
        this.editImageResource = editImageResource;
    }
}


