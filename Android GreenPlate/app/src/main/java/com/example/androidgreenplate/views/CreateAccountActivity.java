package com.example.androidgreenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidgreenplate.R;
import com.example.androidgreenplate.viewmodels.CreateAccountViewModel;

public class CreateAccountActivity extends AppCompatActivity {
    private CreateAccountViewModel createAccountViewModel;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        createAccountViewModel = new ViewModelProvider(this).get(CreateAccountViewModel.class);
        createAccountViewModel.getCreateAccountStatus().observe(this, status -> {
            if (status.getIsSuccess()) {
                createAccountViewModel.saveUser();
            } else {
                String errorMessage = status.getErrorMessage();
                System.out.println(errorMessage);
                if (!TextUtils.isEmpty(errorMessage)) {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        createAccountViewModel.getSaveUserStatus().observe(this, status -> {
            if (status.getIsSuccess()) {
                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                String errorMessage = status.getErrorMessage();
                System.out.println(errorMessage);
                if (!TextUtils.isEmpty(errorMessage)) {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });


        userNameEditText = findViewById(R.id.usernameText);
        passwordEditText = findViewById(R.id.passwordText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordText);

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        Button createAccountButton = findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(v -> {
            
            String username = userNameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            createAccountViewModel.createAccount(username, password, confirmPassword);
        });

    }

}
