package com.example.imPine;

public class UserDataModel {
    private String userImageText;
    private String userNameText;
    private String userIDText;
    private int editImageResource; // Assuming you use resource IDs for edit icons

    public UserDataModel(String userImageText, String userNameText, String userIDText, int editImageResource) {
        this.userImageText = userImageText;
        this.userNameText = userNameText;
        this.userIDText = userIDText;
        this.editImageResource = editImageResource;
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


