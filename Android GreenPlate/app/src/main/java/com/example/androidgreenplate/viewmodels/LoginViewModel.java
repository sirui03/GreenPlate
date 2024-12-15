package com.example.androidgreenplate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.text.TextUtils;
import com.example.androidgreenplate.model.LoginStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private MutableLiveData<LoginStatus> loginStatus = new MutableLiveData<>();

    public void signIn(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            loginStatus.postValue(new LoginStatus(false, null, "Email is empty"));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            loginStatus.postValue(new LoginStatus(false, null, "Password is empty"));
            return;
        }

        if (username.contains(" ")) {
            loginStatus.postValue(new LoginStatus(false, null,
                    "Email cannot contain whitespaces!"));
            return;
        }

        if (password.contains(" ")) {
            loginStatus.postValue(new LoginStatus(false, null,
                    "Password cannot contain whitespaces!"));
            return;
        }



        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        loginStatus.postValue(new LoginStatus(true, user, "Success!"));
                        System.out.println("Authentication success");
                    } else {
                        System.out.println("Authentication failed");
                        loginStatus.postValue(new LoginStatus(false, null,
                                task.getException().getMessage()));
                    }
                });
    }

    public LiveData<LoginStatus> getLoginStatus() {
        return loginStatus;
    }

}

