package com.example.androidgreenplate.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androidgreenplate.viewmodels.IngredientViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidgreenplate.R;
import com.example.androidgreenplate.model.Ingredient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class IngredientsActivity extends AppCompatActivity {


    private IngredientViewModel ingredientViewModel;

    private ListView ingredientsListView;
    private ArrayAdapter<Ingredient> adapter;
    private List<Ingredient> ingredientList = new ArrayList<>();
  
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        Button addIngredientButton = findViewById(R.id.addIngredientButton);
        addIngredientButton.setOnClickListener(v -> addPantry());

        ingredientViewModel = new ViewModelProvider(this).get(IngredientViewModel.class);

        ingredientsListView = findViewById(R.id.ingredientsListView);
        ingredientsListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        ingredientsListView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ingredientList);
        ingredientsListView.setAdapter(adapter);

        // fetch all the ingredients to the list view, and update UI
        ingredientViewModel.getIngredientsLiveData().observe(this, ingredients -> {
            ingredientList.clear();
            ingredientList.addAll(ingredients);
            adapter.notifyDataSetChanged();
        });

        ingredientViewModel.getSaveIngredientStatus().observe(this, status -> {

            if (status != null) {
                if (!status.getIsSuccess()) {
                    String errorMessage = ingredientViewModel.getSaveIngredientStatus()
                            .getValue().getErrorMessage();
                    System.out.println(errorMessage);
                    if (!TextUtils.isEmpty(errorMessage)) {
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }

                    ingredientViewModel.setDefault();

                }
            }
        });

        //create dialog when clicked
        ingredientsListView.setOnItemClickListener((parent, view, position, id) -> {
            Ingredient ingredient = ingredientList.get(position);
            showEditDialog(ingredient);
        });

        // set up botton navigation

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            item.setChecked(true);
            if (item.getItemId() == R.id.nav_homeActivity) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_ingredientsActivity) {
                Intent intent = new Intent(this, IngredientsActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_inputMealActivity) {
                Intent intent = new Intent(this, InputMealActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_recipeActivity) {
                Intent intent = new Intent(this, RecipeActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_shoppingListActivity) {
                Intent intent = new Intent(this, ShoppingListActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_userInfoActivity) {
                Intent intent = new Intent(this, UserInfoActivity.class);
                startActivity(intent);
                finish();
            }
            return false;
        });
    }

    private void addPantry() {
        final Dialog ingredientDialog = new Dialog(this);
        ingredientDialog.setContentView(R.layout.dialog_ingredient);

        final EditText ingredientNameEditText =
                ingredientDialog.findViewById(R.id.editTextIngradient);
        final EditText ingredientQuantityEditText =
                ingredientDialog.findViewById(R.id.editTextQuantity);
        final EditText ingredientCalorieEditText =
                ingredientDialog.findViewById(R.id.editTextCalorie);
        Button submitIngredientButton = ingredientDialog.findViewById(R.id.buttonSubmit);
        Button exitButton  = ingredientDialog.findViewById(R.id.buttonClose);
        submitIngredientButton.setOnClickListener(v -> {
            String name = ingredientNameEditText.getText().toString();
            String quantityStr = ingredientQuantityEditText.getText().toString();
            String calorieStr = ingredientCalorieEditText.getText().toString();
            ingredientViewModel.addIngredient(name, quantityStr, calorieStr);

            ingredientDialog.dismiss();
        });

        exitButton.setOnClickListener(view -> ingredientDialog.dismiss());
        ingredientDialog.show();
    }


    //show edit dialog for the ingredient you clicked, including increase/decrease button
    // and edit texts, and then call the viewModel for updating ingredient
    private void showEditDialog(final Ingredient ingredient) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_ingredient, null);
        final EditText editTextName = dialogView.findViewById(R.id.editTextIngredientName);
        final EditText editTextCalories = dialogView.findViewById(R.id.editTextCalories);
        final EditText editTextQuantity = dialogView.findViewById(R.id.editTextQuantity);
        Button buttonDecrease = dialogView.findViewById(R.id.buttonDecrease);
        Button buttonIncrease = dialogView.findViewById(R.id.buttonIncrease);

        editTextName.setText(ingredient.getName());
        editTextCalories.setText(String.valueOf(ingredient.getCalories()));
        editTextQuantity.setText(String.valueOf(ingredient.getQuantity()));

        //buttons for increase, decrease
        buttonDecrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(editTextQuantity.getText().toString());
            quantity = Math.max(quantity - 1, 0);
            editTextQuantity.setText(String.valueOf(quantity));
        });

        buttonIncrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(editTextQuantity.getText().toString());
            quantity = quantity + 1;
            editTextQuantity.setText(String.valueOf(quantity));
        });

        //set up buttons for update ingredient, and cancel the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Ingredient")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String name = editTextName.getText().toString();
                    int calories = Integer.parseInt(editTextCalories.getText().toString());
                    int quantity = Integer.parseInt(editTextQuantity.getText().toString());

                    ingredient.setName(name);
                    ingredient.setCalories(calories);
                    ingredient.setQuantity(quantity);
                    //in use
                    ingredientViewModel.updateIngredient(ingredient);

                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    ingredientsListView.clearFocus();
                    ingredientsListView.setSelection(-1);
                    adapter.notifyDataSetChanged();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
