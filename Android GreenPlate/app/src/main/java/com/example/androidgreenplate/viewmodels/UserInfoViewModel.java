package com.example.androidgreenplate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidgreenplate.model.LoginStatus;
import com.example.androidgreenplate.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserInfoViewModel extends ViewModel {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final DatabaseReference databaseReference;
    private MutableLiveData<Boolean> saveSuccessLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    private static MutableLiveData<LoginStatus> userinfoStatus = new MutableLiveData<>();

    private static UserInfoViewModel instance;

    private UserInfoViewModel() {
        databaseReference = FirebaseDatabase.getInstance().getReference(); //root node of the db
    }

    public static UserInfoViewModel getInstance() {
        if (instance == null) {
            instance = new UserInfoViewModel();
        }
        userinfoStatus.postValue(new LoginStatus(false, null, ""));
        System.out.println(userinfoStatus);
        return instance;
    }

    public void savePersonalInfo(String height, String weight, String gender) {
        if (!(gender.equals("Male") || gender.equals("male") || gender.equals("MALE")
                || gender.equals("Female") || gender.equals("female") || gender.equals("FEMALE"))) {
            userinfoStatus.postValue(new LoginStatus(false, null, "Gender must be male or female"));
            return;
        }
        int height1 = Integer.parseInt(height);
        int weight1 = Integer.parseInt(weight);
        User user = new User(gender, height1, weight1);
        user.calculateAndSetGoal();
        String userId = mAuth.getCurrentUser().getUid();
        System.out.println(userId);
        DatabaseReference userRef = databaseReference.child("users").child(userId);
        //User user = new User(height, weight, gender);
        //should we upload user object to database instead of the three attributes
        userRef.setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userinfoStatus.postValue(new LoginStatus(true, null, "Success!"));
                    } else {
                        userinfoStatus.postValue(new LoginStatus(false, null,
                                task.getException().getMessage()));
                    }
                });
    }

    public LiveData<Boolean> getSaveSuccessLiveData() {
        return saveSuccessLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public LiveData<LoginStatus> getUserinfoStatus() {
        return userinfoStatus;
    }
    public void setUserinfoStatus() {
        userinfoStatus.postValue(new LoginStatus(false, null, ""));
    }
}
