package com.example.androidgreenplate.viewmodels;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidgreenplate.model.LoginStatus;
import com.example.androidgreenplate.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.androidgreenplate.model.Meal;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputMealViewModel extends ViewModel {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static MutableLiveData<LoginStatus> saveMealStatus = new MutableLiveData<>();
    private MutableLiveData<Meal> saveMealInfo = new MutableLiveData<>();

    private final DatabaseReference databaseReference;

    private MutableLiveData<User> user = new MutableLiveData<>();

    private MutableLiveData<List<User>> userListLiveData = new MutableLiveData<>();
    public LiveData<List<User>> getUserListLiveData() {
        return userListLiveData;
    }

    private MutableLiveData<List<Meal>> userMealsLiveData = new MutableLiveData<>();

    public LiveData<List<Meal>> getUserMealsLiveData() {
        return userMealsLiveData;
    }

    public LiveData<User> getUser() {
        return user;
    }

    private static InputMealViewModel instance;

    private InputMealViewModel() {
        databaseReference = FirebaseDatabase.getInstance().getReference(); //root node of the db
    }

    public static synchronized InputMealViewModel getInstance() {
        if (instance == null) {
            instance = new InputMealViewModel();
        }
        saveMealStatus.postValue(new LoginStatus(false, null, ""));
        return instance;
    }

    public void initialize() {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = databaseReference.child("users");
        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    user.setValue(dataSnapshot.child(userId).getValue(User.class));
                }
            }
        });
        fetchUsers();
        fetchMealsForCurrentUser();
    }

    public void save(String mealName, String caloriesString) {

        int calories = 0;
        try {
            calories = Integer.parseInt(caloriesString);
        } catch (NumberFormatException e) {
            saveMealStatus.postValue(new LoginStatus(false, null, "Calories is not an integer!"));
            return;
        }

        Log.d("mealName", mealName);
        Log.d("calories", String.valueOf(calories));

        String userId = mAuth.getCurrentUser().getUid();
        String mealId = databaseReference.child("meals").push().getKey();
        Meal newMeal = new Meal(mealName, calories, userId);
        //The other way of writing
        //databaseReference.child("users").child(userId).child("meals").child(mealId).setValue()
        databaseReference.child("meals").child(mealId).setValue(newMeal)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveMealStatus.postValue(new LoginStatus(true, null,
                                "Successfully saved!"));
                        fetchMealsForCurrentUser();
                    } else {
                        String errorMessage = "Failed!";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        saveMealStatus.postValue(new LoginStatus(false, null, errorMessage));
                    }
                });
    }

    public LiveData<LoginStatus> getSaveMealStatus() {
        return saveMealStatus;
    }

    public void setSaveMealStatus() {
        saveMealStatus.postValue(new LoginStatus(false, null, ""));
    }



    private MutableLiveData<Integer> totalCaloriesLiveData = new MutableLiveData<>();

    public LiveData<Integer> getTotalCaloriesLiveData() {
        return totalCaloriesLiveData;
    }



    //this method updates the calsConsumed attribute for the current user
    private void updateCaloriesConsumedForCurrentUser(int totalCals) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = databaseReference.child("users").child(userId);

        Log.d("Updating Calories", "Updating calories consumed for userID: " + userId);

        // Prepare the update map
        Map<String, Object> updates = new HashMap<>();
        updates.put("calsConsumed", totalCals);

        // Updating the calsConsumed attribute for the user
        userRef.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Update Success",
                        "Calories consumed updated successfully for userID: " + userId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Update Failure",
                        "Failed to update calories consumed for userID: " + userId, e);
            }
        });
    }

    //method to get all meals of the current user
    public void fetchMealsForCurrentUser() {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference mealsRef = databaseReference.child("meals");
        Log.d("Searching for meals", "Searching for meals under userID" + userId);
        final int[] calories = new int[1];
        mealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Meal> userMeals = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Meal meal = snapshot.getValue(Meal.class);
                    // Check if the user ID of the current meal OBEJCT matches the current user's ID
                    if (meal.getUserId().equals(userId)) {
                        userMeals.add(meal);
                        calories[0] = calories[0] + meal.getCalories();
                    }
                }

                // Update totalCals LiveData with the total calories
                totalCaloriesLiveData.postValue(calories[0]);
                userMealsLiveData.postValue(userMeals);
                Log.d("Meal list generation", "Meal list generated");
                Log.d("Calories updated", String.valueOf(calories[0]));
                System.out.println(userMeals);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Meal list generation", "Meal list generation FAILED");
            }
        });
    }
    public void fetchUsers() {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference usersRef = databaseReference.child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user1 = snapshot.getValue(User.class);
                    users.add(user1);
                }
                userListLiveData.postValue(users);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Meal list generation", "Meal list generation FAILED");
            }
        });
    }



}