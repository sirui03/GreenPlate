
package com.example.androidgreenplate.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidgreenplate.model.Ingredient;
import com.example.androidgreenplate.model.LoginStatus;
import com.example.androidgreenplate.model.Recipe;
import com.example.androidgreenplate.viewmodels.sortingstrategies.RecipeSortingStrategy;
import com.example.androidgreenplate.viewmodels.sortingstrategies.SortByDefault;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeViewModel extends ViewModel {
    private static MutableLiveData<LoginStatus> recipeStatus = new MutableLiveData<>();

    private static RecipeViewModel instance;
    private DatabaseReference databaseReference;
    private MutableLiveData<List<Recipe>> recipes = new MutableLiveData<>();
    private MutableLiveData<List<Ingredient>> userIngredientsLiveData = new MutableLiveData<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static MutableLiveData<LoginStatus> enoughIngredientStatus = new MutableLiveData<>();


    private RecipeViewModel() {
        databaseReference = FirebaseDatabase.getInstance().getReference(); //root node of the db
    }

    public static RecipeViewModel getInstance() {
        if (instance == null) {
            instance = new RecipeViewModel();
        }
        instance.setDefault();
        instance.fetchIngredientsForCurrentUser();
        instance.getRecipesFromDatabase();
        return instance;
    }

    public void save(List<String> ingredientNames, List<String> ingredientQuantities, String name) {
        if (name.isEmpty()) {
            recipeStatus.postValue(new LoginStatus(false, null, "Recipe name cannot be empty!"));
            return;
        }
        ArrayList<Ingredient> ingredientArrayList = new ArrayList<>();

        if (!ingredientNames.isEmpty()) {
            for (int i = 0; i < ingredientNames.size(); i++) {
                if (ingredientNames.get(i).isEmpty()) {
                    recipeStatus.postValue(new LoginStatus(false, null,
                            "Ingredients names cannot be empty!"));
                    return;
                }
                if (ingredientQuantities.get(i).isEmpty()) {
                    recipeStatus.postValue(new LoginStatus(false, null,
                            "Ingredients quantity must be positive integer!"));
                    return;
                } else {
                    if (Integer.parseInt(ingredientQuantities.get(i)) <= 0) {
                        recipeStatus.postValue(new LoginStatus(false, null,
                                "Ingredients quantity must be positive integer!"));
                        return;
                    }
                }
                Ingredient ingredient = new Ingredient(ingredientNames.get(i),
                        Integer.parseInt(ingredientQuantities.get(i)));
                ingredientArrayList.add(ingredient);
            }
        } else {
            recipeStatus.postValue(new LoginStatus(false, null, "Ingredients list empty!"));
            return;
        }

        Recipe recipe = new Recipe(ingredientArrayList, name);
        String recipeId = databaseReference.child("recipes").push().getKey();
        databaseReference.child("recipes").child(recipeId).setValue(recipe)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recipeStatus.postValue(new LoginStatus(true, null,
                                "Successfully saved!"));
                        getRecipesFromDatabase();
                    } else {
                        String errorMessage = "Failed!";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        recipeStatus.postValue(new LoginStatus(false, null, errorMessage));
                    }
                });
    }

    public LiveData<LoginStatus> getSaveRecipeStatus() {
        return recipeStatus;
    }

    public void setRecipeStatus() {
        recipeStatus.postValue(new LoginStatus(false, null, ""));
    }

    // Retrieves recipes from the Firebase database and updates the recipes LiveData.
    public void getRecipesFromDatabase() {
        databaseReference.child("recipes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Recipe> recipeList = new ArrayList<>();
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    recipeList.add(recipe);
                }
                Collections.reverse(recipeList);
                recipes.setValue(recipeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RecipeViewModel", "Error retrieving recipes from the database",
                        error.toException());
            }
        });
    }


    // Checks if the user has enough ingredients in their pantry for the given recipe.
    public void fetchIngredientsForCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        // feel like that's unnecessary, and also currentUserStatusLiveData is unnecessary as well
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
    public boolean hasEnoughIngredients(Recipe recipe) {
        List<Ingredient> requiredIngredients = recipe.getRecipeIngredients();
        List<Ingredient> userIngredients = userIngredientsLiveData.getValue();

        if (userIngredients == null) {
            System.out.println("User Ingredients is null");
            return false;
        }

        for (Ingredient requiredIngredient : requiredIngredients) {
            boolean found = false;
            for (Ingredient availableIngredient : userIngredients) {
                if (requiredIngredient.getName().equals(availableIngredient.getName())) {
                    if (requiredIngredient.getQuantity() <= availableIngredient.getQuantity()) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

    public LiveData<LoginStatus> getEnoughIngredientStatus() {
        return this.enoughIngredientStatus;
    }
    public void setDefault() {
        enoughIngredientStatus.postValue(new LoginStatus(true, null, ""));
        // recipeStatus.postValue(new LoginStatus(false, null, ""));
    }

    private RecipeSortingStrategy sortingStrategy;

    public void setSortingStrategy(RecipeSortingStrategy strategy) {
        this.sortingStrategy = strategy;
    }

    public void sortRecipes() {
        if (recipes.getValue() != null && sortingStrategy != null) {
            System.out.println(recipes.getValue());
            List<Recipe> sortedList = sortingStrategy.sort(new ArrayList<>(recipes.getValue()));
            recipes.setValue(sortedList);
            if (sortingStrategy instanceof SortByDefault) {
                getRecipesFromDatabase();
            }
        }
    }
}
