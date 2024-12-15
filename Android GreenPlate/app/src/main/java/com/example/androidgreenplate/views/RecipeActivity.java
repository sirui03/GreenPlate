
package com.example.androidgreenplate.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidgreenplate.R;
import com.example.androidgreenplate.model.RecipeAdapter;
import com.example.androidgreenplate.viewmodels.sortingstrategies.SortByIngredientCount;
import com.example.androidgreenplate.viewmodels.sortingstrategies.SortByDefault;
import com.example.androidgreenplate.viewmodels.sortingstrategies.SortByNameStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.androidgreenplate.viewmodels.RecipeViewModel;


import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {
    private RecipeViewModel recipeViewModel;
    private LinearLayout container;
    private Button addButton;
    private ArrayList<EditText> nameEditTexts = new ArrayList<>();
    private ArrayList<EditText> quantityEditTexts = new ArrayList<>();
    private RecyclerView recipesRecyclerView;
    private RecipeAdapter recipeAdapter;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recipeViewModel = RecipeViewModel.getInstance();

        Button createRecipe = findViewById(R.id.createRecipeButton);
        createRecipe.setOnClickListener(v -> showCreateRecipe());

        recipesRecyclerView = findViewById(R.id.recipesRecyclerView);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeAdapter = new RecipeAdapter(new ArrayList<>());
        recipesRecyclerView.setAdapter(recipeAdapter);


        recipeViewModel.getRecipes().observe(this, recipes -> {
            if (recipes != null) {
                recipeAdapter.setRecipes(recipes);
                recipeAdapter.notifyDataSetChanged();
            }
        });

        recipeViewModel.getEnoughIngredientStatus().observe(this, status -> {
            if (!status.getIsSuccess()) {
                String errorMessage = status.getErrorMessage();
                if (!TextUtils.isEmpty(errorMessage)) {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    recipeViewModel.setDefault();
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.getMenu().getItem(3).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            item.setChecked(true);
            if (item.getItemId() == R.id.nav_homeActivity) {
                recipeViewModel.setDefault();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_ingredientsActivity) {
                recipeViewModel.setDefault();
                Intent intent = new Intent(this, IngredientsActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_inputMealActivity) {
                recipeViewModel.setDefault();
                Intent intent = new Intent(this, InputMealActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_recipeActivity) {
                recipeViewModel.setDefault();
                Intent intent = new Intent(this, RecipeActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_shoppingListActivity) {
                recipeViewModel.setDefault();
                Intent intent = new Intent(this, ShoppingListActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_userInfoActivity) {
                recipeViewModel.setDefault();
                Intent intent = new Intent(this, UserInfoActivity.class);
                startActivity(intent);
                finish();
            }
            return false;
        });
        setupSortingButtons();
    }

    private void setupSortingButtons() {

        findViewById(R.id.buttonSortByName).setOnClickListener(v -> {
            recipeViewModel.setSortingStrategy(new SortByNameStrategy());
            recipeViewModel.sortRecipes();
        });

        findViewById(R.id.buttonSortByIngredientQuantity).setOnClickListener(v -> {
            recipeViewModel.setSortingStrategy(new SortByIngredientCount());
            recipeViewModel.sortRecipes();
        });

        findViewById(R.id.buttonSortByDefault).setOnClickListener(v -> {
            recipeViewModel.setSortingStrategy(new SortByDefault());
            recipeViewModel.sortRecipes();
        });

    }

    private void showCreateRecipe() {
        recipeViewModel.setRecipeStatus();

        Dialog createRecipeDialog = new Dialog(this);
        createRecipeDialog.setContentView(R.layout.dialog_create_recipe);
        container = createRecipeDialog.findViewById(R.id.container);
        addButton = createRecipeDialog.findViewById(R.id.button_add);

        // avoiding keep adding observer to SaveRecipeStatus
        createRecipeDialog.setOnDismissListener(d -> recipeViewModel
                .getSaveRecipeStatus().removeObservers(this));

        addButton.setOnClickListener(v -> {
            // Create TextView for "Ingredient name:"
            TextView ingredientNameTextView = new TextView(createRecipeDialog.getContext());
            ingredientNameTextView.setText("Ingredient name:");
            container.addView(ingredientNameTextView);

            EditText ingredientNameEditText = new EditText(createRecipeDialog.getContext());
            ingredientNameEditText.setHint("Enter ingredient name");
            container.addView(ingredientNameEditText);
            nameEditTexts.add(ingredientNameEditText);

            // Add the TextView and EditText for quantity
            TextView quantityTextView = new TextView(createRecipeDialog.getContext());
            quantityTextView.setText("Quantity:");
            container.addView(quantityTextView);

            EditText quantityEditText = new EditText(createRecipeDialog.getContext());
            quantityEditText.setHint("Enter quantity");
            quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            container.addView(quantityEditText);
            quantityEditTexts.add(quantityEditText);
        });

        Button closeButton = createRecipeDialog.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> {
            nameEditTexts = new ArrayList<>();
            quantityEditTexts = new ArrayList<>();
            recipeViewModel.setRecipeStatus();
            createRecipeDialog.dismiss();
        });

        // handle save logic
        Button saveButton = createRecipeDialog.findViewById(R.id.button_save);
        saveButton.setOnClickListener(v -> {
            ArrayList<String> ingredients = new ArrayList<>();
            ArrayList<String> quantities = new ArrayList<>();

            for (int i = 0; i < nameEditTexts.size(); i++) {
                String ingredient = nameEditTexts.get(i).getText().toString().trim();
                String quantity = quantityEditTexts.get(i).getText().toString().trim();

                if (!ingredient.isEmpty()) {
                    ingredients.add(ingredient);
                    quantities.add(quantity);
                }
            }

            EditText recipeNameEdit = createRecipeDialog.findViewById(R.id.enterRecipeName);
            String recipeName = recipeNameEdit.getText().toString().trim();
            recipeViewModel.save(ingredients, quantities, recipeName);
        });

        createRecipeDialog.show();
        recipeViewModel.getSaveRecipeStatus().observe(this, status -> {
            if (status.getIsSuccess()) {
                Toast.makeText(this, "Recipe saved successfully", Toast.LENGTH_LONG).show();
                nameEditTexts = new ArrayList<>();
                quantityEditTexts = new ArrayList<>();
                recipeViewModel.setRecipeStatus();
                createRecipeDialog.dismiss(); //dismiss the thing, this logic can be improved
            } else {
                String errorMessage = status.getErrorMessage();
                if (!TextUtils.isEmpty(errorMessage)) {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
