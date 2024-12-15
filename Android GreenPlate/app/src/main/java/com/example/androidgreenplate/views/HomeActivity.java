package com.example.androidgreenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidgreenplate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.getMenu().getItem(2).setChecked(true);
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
        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> finish());
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
