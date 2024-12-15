package com.example.androidgreenplate.views;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidgreenplate.R;
import com.example.androidgreenplate.viewmodels.UserInfoViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserInfoActivity extends AppCompatActivity {
    private UserInfoViewModel userInfoViewModel;
    private EditText heightInputText;
    private EditText weightInputText;
    private EditText genderInputText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        userInfoViewModel = UserInfoViewModel.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            item.setChecked(true);
            if (item.getItemId() == R.id.nav_homeActivity) {
                userInfoViewModel.setUserinfoStatus();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_ingredientsActivity) {
                userInfoViewModel.setUserinfoStatus();
                Intent intent = new Intent(this, IngredientsActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_inputMealActivity) {
                userInfoViewModel.setUserinfoStatus();
                Intent intent = new Intent(this, InputMealActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_recipeActivity) {
                userInfoViewModel.setUserinfoStatus();
                Intent intent = new Intent(this, RecipeActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_shoppingListActivity) {
                userInfoViewModel.setUserinfoStatus();
                Intent intent = new Intent(this, ShoppingListActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_userInfoActivity) {
                userInfoViewModel.setUserinfoStatus();
                Intent intent = new Intent(this, UserInfoActivity.class);
                startActivity(intent);
                finish();
            }
            return false;
        });

        heightInputText = findViewById(R.id.heightInputText);
        weightInputText = findViewById(R.id.weightInputText);
        genderInputText = findViewById(R.id.genderInputText);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> savePersonalInfo());

        userInfoViewModel.getUserinfoStatus().observe(this, status -> {
            System.out.println(status.getIsSuccess());
            System.out.println(status.getErrorMessage());
            if (status.getIsSuccess()) {
                Toast.makeText(this, "Personal info saved successfully", Toast.LENGTH_LONG).show();
            } else {
                String errorMessage = status.getErrorMessage();
                if (!TextUtils.isEmpty(errorMessage)) {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
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

    private void savePersonalInfo() {
        String height = heightInputText.getText().toString();
        String weight = weightInputText.getText().toString();
        String gender = genderInputText.getText().toString();

        userInfoViewModel.savePersonalInfo(height, weight, gender);
    }


}
    
