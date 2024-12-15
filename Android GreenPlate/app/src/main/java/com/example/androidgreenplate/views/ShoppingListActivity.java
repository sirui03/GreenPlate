package com.example.androidgreenplate.views;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidgreenplate.R;
import com.example.androidgreenplate.model.Ingredient;
import com.example.androidgreenplate.model.MealsAdapter;
import com.example.androidgreenplate.model.ShoppingList;
import com.example.androidgreenplate.model.ShoppingListAdapter;
import com.example.androidgreenplate.viewmodels.ShoppingListViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class ShoppingListActivity extends AppCompatActivity {
    private ShoppingListViewModel shoppingListViewModel;

    private RecyclerView shoppingListRecyclerView;
    private DatabaseReference mDatabase;
    private ArrayList<Ingredient> mShoppingList = new ArrayList<>();

    private ShoppingListAdapter shoppingListAdapter;

    private ArrayAdapter<Ingredient> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shopping_list);
        shoppingListViewModel = new ShoppingListViewModel();

        shoppingListViewModel.getShoppingListItems().observe(this, shoppingListItems -> {
            shoppingListRecyclerView = findViewById(R.id.shoppinglist_recyclerview);
            shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            shoppingListAdapter = new ShoppingListAdapter(shoppingListItems);
            shoppingListRecyclerView.setAdapter(shoppingListAdapter);

            shoppingListAdapter.setOnItemClickListener(ingredient -> showEditQuantityDialog(ingredient));

        });


        shoppingListViewModel = new ViewModelProvider(this).get(ShoppingListViewModel.class);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.getMenu().getItem(4).setChecked(true);
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

        setupAddIngredientButton();
    }

    private void setupAddIngredientButton() {
        System.out.println("aaa");
        Button addIngredientButton = findViewById(R.id.addIngredientButton);
        addIngredientButton.setOnClickListener(v -> showAddIngredientDialog());
    }

    // presents a dialog form to the user, allowing them to input ingredient details (name, quantity, and calories)
    private void showAddIngredientDialog() {
        System.out.println("bbb");
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_shoppinglist);

        EditText editTextIngredientName = dialog.findViewById(R.id.editTextIngredient);
        EditText editTextIngredientQuantity = dialog.findViewById(R.id.editTextQuantity);
        Button submitButton = dialog.findViewById(R.id.buttonSubmit);
        Button closeButton = dialog.findViewById(R.id.buttonClose);

        submitButton.setOnClickListener(v -> {
            String name = editTextIngredientName.getText().toString().trim();
            String quantityStr = editTextIngredientQuantity.getText().toString().trim();

            // Basic validation
            if (name.isEmpty() || quantityStr.isEmpty()) {
                Toast.makeText(ShoppingListActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(ShoppingListActivity.this, "Invalid number format.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create the Ingredient object and set the current user ID
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(ShoppingListActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }
            Ingredient newIngredient = new Ingredient();
            newIngredient.setName(name);
            newIngredient.setQuantity(quantity);
            newIngredient.setUserId(currentUser.getUid());

            // Use ViewModel to add or update the ingredient
            shoppingListViewModel.addToShoppingList(newIngredient);
            dialog.dismiss();
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void showEditQuantityDialog(final Ingredient ingredient) {
        LayoutInflater inflator = LayoutInflater.from(this);
        View dialogView = inflator.inflate(R.layout.dialog_edit_ingredient_quantity, null);
        final TextView textViewIngredientName = dialogView.findViewById(R.id.textView_ingredientName);
        final EditText editTextQuantity = dialogView.findViewById(R.id.editTextNumber);
        Button buttonDecreaseQuantity = dialogView.findViewById(R.id.button_DecreaseQuantity);
        Button buttonIncreaseQuantity = dialogView.findViewById(R.id.button_IncreaseQuantity);

        textViewIngredientName.setText(ingredient.getName());
        editTextQuantity.setText(String.valueOf(ingredient.getQuantity()));

        //set up  decrease button
        buttonDecreaseQuantity.setOnClickListener(v -> {
            int quantity = Integer.parseInt(editTextQuantity.getText().toString());
            quantity = Math.max(quantity - 1, 0);
            editTextQuantity.setText(String.valueOf(quantity));
        });

        buttonIncreaseQuantity.setOnClickListener(v -> {
            int quantity = Integer.parseInt(editTextQuantity.getText().toString());
            quantity = quantity + 1;
            editTextQuantity.setText(String.valueOf(quantity));
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Ingredient Quantity")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    int quantity = Integer.parseInt(editTextQuantity.getText().toString());
                    ingredient.setQuantity(quantity);
                    shoppingListViewModel.updateIngredient(ingredient);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    shoppingListRecyclerView.clearFocus();

                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
