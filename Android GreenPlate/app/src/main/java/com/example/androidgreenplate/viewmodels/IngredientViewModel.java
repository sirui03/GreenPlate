package com.example.androidgreenplate.viewmodels;


import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidgreenplate.model.Ingredient;
import com.example.androidgreenplate.model.LoginStatus;
import com.example.androidgreenplate.views.IngredientsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class IngredientViewModel extends ViewModel {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private final DatabaseReference databaseReference;
    // add something for new commits

    public IngredientViewModel() {
        databaseReference = FirebaseDatabase.getInstance().getReference(); //root node of the db
        fetchIngredientsForCurrentUser(); //fetch ingredients when viewModel is created
        setDefault();
    }

    private MutableLiveData<LoginStatus> saveIngredientStatus = new MutableLiveData<>();

    public LiveData<LoginStatus> getSaveIngredientStatus() {
        return saveIngredientStatus;
    }

    private MutableLiveData<List<Ingredient>> userIngredientsLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Ingredient>> getIngredientsLiveData() {
        return userIngredientsLiveData;
    }


    public void updateIngredient(Ingredient ingredient) {
        System.out.println(ingredient);
        System.out.println(ingredient.getId());
        if (ingredient.getQuantity() == 0) {
            databaseReference.child("pantry").child(ingredient.getId()).removeValue();
        } else {
            //use case other attributes of ingredient has to be updated
            databaseReference.child("pantry").child(ingredient.getId()).setValue(ingredient);
        }
        fetchIngredientsForCurrentUser(); //re-fetch ingredients after something changed
    }

    // ******************************************************
    // CURRENT USER STUFF
    // ******************************************************
    // posts all the ingredients that are associated with a current user
    // ingredients found via userId
    public void fetchIngredientsForCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        if (currentUser.equals(null) || userId == null) {
            Log.d("Current User Status", "Current user NOT found");
            return;
        }

        Log.d("Current User Status", "Current user found with ID: " + userId);

        DatabaseReference usersRef = databaseReference.child("pantry");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Ingredient> ingredientList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ingredient ingredient = snapshot.getValue(Ingredient.class);

                    if (ingredient != null) {
                        if (userId.equals(ingredient.getUserId())) {
                            ingredientList.add(ingredient);
                        }
                    } else {
                        Log.d("Current User Ingredients", "No ingredients in the db");

                    }
                }
                userIngredientsLiveData.postValue(ingredientList);
                Log.d("Current User Ingredients",
                        "Posting ingredients List to userIngredientsLiveData");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Current User Ingredients", "Ingredient list generation FAILED");
            }
        });
    }

    public void addIngredient(String name, String quantityStr, String calorieStr) {
        int quantity, calorie;
        if (name.equals("")) {
//            Toast.makeText(IngredientsActivity.this,
//                    "Empty Name!", Toast.LENGTH_LONG).show();
            saveIngredientStatus.postValue(new LoginStatus(false, null, "Empty Name!"));
            return;
        }
        try {
            quantity = Integer.parseInt(quantityStr);
            calorie = Integer.parseInt(calorieStr);
            if (quantity <= 0) {
//                Toast.makeText(IngredientsActivity.this,
//                        "Quantity Invalid!", Toast.LENGTH_LONG).show();
                saveIngredientStatus.postValue(new LoginStatus(false, null, "Invalide Quantity!"));
                return;
            }
        } catch (NumberFormatException e) {
//            Toast.makeText(IngredientsActivity.this, "Invalid Input", Toast.LENGTH_LONG).show();
            saveIngredientStatus.postValue(new LoginStatus(false, null, "Invalid Input!"));
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        // Check if the pantry already exists for the current user
        databaseReference.child("pantry")
                .orderByChild("userId")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean pantryExists = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Ingredient pantry = snapshot.getValue(Ingredient.class);
                            if (pantry != null && pantry.getName().equals(name)
                                    && pantry.getQuantity() > 0) {
                                pantryExists = true;
                                break;
                            }
                        }

                        if (pantryExists) {
                            saveIngredientStatus.postValue(new LoginStatus(false, null,
                                    "Pantry with the same name already exists!"));
                        } else {
                            // If pantry doesn't exist, add it to the database
                            String ingredientId = databaseReference.child("pantry").push().getKey();
                            if (ingredientId != null) {
                                Ingredient newIngredient = new Ingredient(ingredientId,
                                        userId, name, quantity, calorie);
                                databaseReference.child("pantry")
                                        .child(ingredientId)
                                        .setValue(newIngredient)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                saveIngredientStatus.postValue(new LoginStatus(
                                                        true, null, "Successfully saved!"));
                                                fetchIngredientsForCurrentUser();
                                            } else {
                                                String errorMessage = "Failed to add pantry!";
                                                if (task.getException() != null) {
                                                    errorMessage = task.getException().getMessage();
                                                }
                                                saveIngredientStatus.postValue(new LoginStatus(
                                                        false, null, errorMessage));
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                        saveIngredientStatus.postValue(new LoginStatus(false, null,
                                "Database error: " + databaseError.getMessage()));
                    }
                });
    }

    public void setDefault() {
        saveIngredientStatus.postValue(new LoginStatus(true, null, ""));
    }

}
