package com.example.androidgreenplate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.text.TextUtils;
import com.example.androidgreenplate.model.LoginStatus;
import com.example.androidgreenplate.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountViewModel extends ViewModel {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MutableLiveData<LoginStatus> createAccountStatus = new MutableLiveData<>();

    private MutableLiveData<LoginStatus> saveUserStatus = new MutableLiveData<>();

    public LiveData<LoginStatus> getCreateAccountStatus() {
        return createAccountStatus;
    }
    public LiveData<LoginStatus> getSaveUserStatus() {
        return saveUserStatus;
    }

    public void createAccount(String username, String password, String confirmedPassword) {
        if (!checkAccountValid(username, password, confirmedPassword)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        createAccountStatus.postValue(new LoginStatus(true, user, "Success!"));
                        System.out.println("Authentication success");
                    } else {
                        System.out.println("Authentication failed");
                        createAccountStatus.postValue(new LoginStatus(false, null,
                                task.getException().getMessage()));
                    }
                });

    }

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public void saveUser() {
        User userData = new User("Male", 0, 0);
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).setValue(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserStatus.postValue(new LoginStatus(true, null,
                                "Successfully saved!"));
                    } else {
                        String errorMessage = "Failed!";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        saveUserStatus.postValue(new LoginStatus(false, null, errorMessage));
                    }
                });
    }

    public boolean checkAccountValid(String username, String password, String confirmedPassword) {
        if (TextUtils.isEmpty(username)) {
            createAccountStatus.postValue(new LoginStatus(false, null,
                    "Email is empty"));
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            createAccountStatus.postValue(new LoginStatus(false, null,
                    "Password is empty"));
            return false;
        }
        if (TextUtils.isEmpty(confirmedPassword)) {
            createAccountStatus.postValue(new LoginStatus(false, null,
                    "Confirmed Password is empty"));
            return false;
        }
        if (username.contains(" ")) {
            createAccountStatus.postValue(new LoginStatus(false, null,
                    "Email cannot contain whitespaces!"));
            return false;
        }
        if (password.contains(" ")) {
            createAccountStatus.postValue(new LoginStatus(false, null,
                    "Password cannot contain whitespaces!"));
            return false;
        }
        if (confirmedPassword.contains(" ")) {
            createAccountStatus.postValue(new LoginStatus(false, null,
                    "Confirmed password cannot contain whitespaces!"));
            return false;
        }
        if (!password.equals(confirmedPassword)) {
            createAccountStatus.postValue(new LoginStatus(false, null,
                    "Password does not match!"));
            return false;
        }
        return true;
    }
}