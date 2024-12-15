package com.example.androidgreenplate.model;

import com.google.firebase.auth.FirebaseUser;
public class LoginStatus {
    private boolean isSuccess;
    private FirebaseUser user;
    private String errorMessage;
    public LoginStatus(boolean isSuccess, FirebaseUser user, String errorMessage) {
        this.isSuccess = isSuccess;
        this.user = user;
        this.errorMessage = errorMessage;
    }
    public boolean getIsSuccess() {
        return isSuccess;
    }
    public FirebaseUser getUser() {
        return user;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
    public void setUser(FirebaseUser user) {
        this.user = user;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
