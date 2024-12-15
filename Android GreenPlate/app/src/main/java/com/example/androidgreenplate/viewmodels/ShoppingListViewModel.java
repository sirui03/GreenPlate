package com.example.androidgreenplate.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidgreenplate.model.Ingredient;
import com.example.androidgreenplate.model.LoginStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class ShoppingListViewModel extends ViewModel {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference;
    private MutableLiveData<LoginStatus> saveIngredientStatus = new MutableLiveData<>();


    private MutableLiveData<List<Ingredient>> shoppingListItems = new MutableLiveData<>();

    public ShoppingListViewModel() {
        databaseReference = FirebaseDatabase.getInstance().getReference("shoppingList");
        fetchShoppingListItemsForCurrentUser();
    }

    public LiveData<List<Ingredient>> getShoppingListItems() {
        return shoppingListItems;
    }

    public void updateIngredient(Ingredient ingredient) {
        FirebaseUser currUser = mAuth.getCurrentUser();
        if (currUser == null) {
            return;
        }
        String userId = currUser.getUid();
        databaseReference.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Ingredient> ingredients = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ingredient dbIngredient = snapshot.getValue(Ingredient.class);
                    if (dbIngredient != null &&dbIngredient.getName().equals(ingredient.getName())) {
                        DatabaseReference itemRef = snapshot.getRef();
                        if (ingredient.getQuantity() == 0){
                            itemRef.removeValue();
                        }else{
                            itemRef.child("quantity").setValue(ingredient.getQuantity());
                        }
                        break;
                    }
                    shoppingListItems.setValue(ingredients);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                System.out.println("loadPost:onCancelled " + databaseError.toException());
            }
        });
        fetchShoppingListItemsForCurrentUser();
    }

    // Fetches shopping list items associated with the current user and updates the LiveData.
    private void fetchShoppingListItemsForCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String userId = currentUser.getUid();

        databaseReference.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Ingredient> ingredients = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ingredient ingredient = snapshot.getValue(Ingredient.class);
                    if (ingredient != null) {
                        ingredients.add(ingredient);
                    }
                }
                shoppingListItems.setValue(ingredients);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                System.out.println("loadPost:onCancelled " + databaseError.toException());
            }
        });
    }


    // Checks for duplicate ingredients in the shopping list and updates if found, otherwise adds new.
    public void addToShoppingList(Ingredient ingredient) {
        if (ingredient == null || ingredient.getQuantity() <= 0 || ingredient.getName().isEmpty()) {
            saveIngredientStatus.postValue(new LoginStatus(false, null, "Please fill in all fields correctly."));
            return; // Validation failed
        }
  
        String userId = mAuth.getCurrentUser().getUid();

        // Check shopping list for existing ingredient under this specific user
        databaseReference.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean itemExists = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ingredient item = snapshot.getValue(Ingredient.class);
                    // If found, update the quantity instead of adding a new entry
                    if (item != null && item.getName().equals(ingredient.getName())) {
                        int newQuantity = item.getQuantity() + ingredient.getQuantity();
                        snapshot.getRef().child("quantity").setValue(newQuantity);
                        itemExists = true;
                        break; // Stop processing as ingredient is updated
                    }
                }
                if (!itemExists) {
                    // if not found, add as new ingredients to shopping list under this user
                    String key = databaseReference.push().getKey();
                    if (key != null) {
                        databaseReference.child(key).setValue(ingredient).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                saveIngredientStatus.postValue(new LoginStatus(
                                        true, null, "Successfully saved!"));
                                fetchShoppingListItemsForCurrentUser();
                            } else {
                                String errorMessage = "Failed to add item!";
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

    
    public LiveData<LoginStatus> getSaveIngredientStatus() {
        return saveIngredientStatus;
    }


}
