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
import com.example.androidgreenplate.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.getLoginStatus().observe(this, loginStatus -> {
            if (loginStatus.getIsSuccess()) {
                Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
                startActivity(intent);
                finish();
            } else {
                String errorMessage = loginStatus.getErrorMessage();
                System.out.println(errorMessage);
                if (!TextUtils.isEmpty(errorMessage)) {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        usernameEditText = findViewById(R.id.usernameText);
        passwordEditText = findViewById(R.id.passwordText);

        Button loginButton = findViewById(R.id.loginButton);
        Button createAccountButton = findViewById(R.id.createButton);
        Button exitButton = findViewById(R.id.exit);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            loginViewModel.signIn(username, password);
        });
        createAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(intent);
            finish();
        });
        exitButton.setOnClickListener(v -> finish());

    }
}


