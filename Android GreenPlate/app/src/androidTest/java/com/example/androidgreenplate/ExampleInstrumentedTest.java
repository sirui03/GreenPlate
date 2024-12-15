package com.example.androidgreenplate;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.androidgreenplate.model.LoginStatus;
import com.example.androidgreenplate.model.Recipe;
import com.example.androidgreenplate.viewmodels.CreateAccountViewModel;
import com.example.androidgreenplate.viewmodels.IngredientViewModel;
import com.example.androidgreenplate.viewmodels.LoginViewModel;
import com.example.androidgreenplate.viewmodels.RecipeViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void whitespace_email_login() {
        String whitespace = "a c@gmail.com";
        String password = "abc";
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn(whitespace, password);
        LiveData<LoginStatus> status1 = viewModel.getLoginStatus();
        if (status1.getValue() != null) {
            assert (status1.getValue().getErrorMessage() == "Email cannot contain whitespaces!");
        }
    }

    @Test
    public void empty_email_login() {
        String empty_string = "";
        String password = "abc";
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn(empty_string, password);
        LiveData<LoginStatus> status2 = viewModel.getLoginStatus();
        if (status2.getValue() != null) {
            assert (status2.getValue().getErrorMessage() == "Email is empty");
        }
    }


    @Test
    public void whitespace_password_login() {
        String whitespace = "a c";
        String email = "abc@gmail.com";
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn(email, whitespace);
        LiveData<LoginStatus> status1 = viewModel.getLoginStatus();
        if (status1.getValue() != null) {
            assert (status1.getValue().getErrorMessage() == "Password cannot contain whitespaces!");
        }
    }

    @Test
    public void empty_password_login() {
        String empty_string = "";
        String email = "abc@gmail.com";
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn(email, empty_string);
        LiveData<LoginStatus> status2 = viewModel.getLoginStatus();
        if (status2.getValue() != null) {
            assert (status2.getValue().getErrorMessage() == "Password is empty");
        }
    }

    @Test
    public void whitespace_email_create_account() {
        String whitespace = "a c@gmail.com";
        String password = "abc";
        CreateAccountViewModel viewModel = new CreateAccountViewModel();
        viewModel.createAccount(whitespace, password, password);
        LiveData<LoginStatus> status1 = viewModel.getCreateAccountStatus();
        if (status1.getValue() != null) {
            assert (status1.getValue().getErrorMessage() == "Email cannot contain whitespaces!");
        }
    }

    @Test
    public void empty_email_create_account() {
        String whitespace = "a c@gmail.com";
        String empty_string = "";
        String password = "abc";
        CreateAccountViewModel viewModel = new CreateAccountViewModel();
        viewModel.createAccount(empty_string, password, password);
        LiveData<LoginStatus> status2 = viewModel.getCreateAccountStatus();
        if (status2.getValue() != null) {
            assert (status2.getValue().getErrorMessage() == "Email is empty");
        }
    }

    @Test
    public void whitespace_password_create_account() {
        String whitespace = "a c";
        String password = "abc";
        String email = "abc@gmail.com";
        CreateAccountViewModel viewModel = new CreateAccountViewModel();

        viewModel.createAccount(email, whitespace, password);
        LiveData<LoginStatus> status1 = viewModel.getCreateAccountStatus();
        if (status1.getValue() != null) {
            assert (status1.getValue().getErrorMessage() == "Password cannot contain whitespaces!");
        }
    }

    @Test
    public void empty_password_create_account() {
        String empty_string = "";
        String password = "abc";
        String email = "abc@gmail.com";
        CreateAccountViewModel viewModel = new CreateAccountViewModel();
        viewModel.createAccount(email, empty_string, password);
        LiveData<LoginStatus> status2 = viewModel.getCreateAccountStatus();
        if (status2.getValue() != null) {
            assert (status2.getValue().getErrorMessage() == "Password is empty");
        }
    }

    @Test
    public void whitespace_confirmed_create_account() {
        String whitespace = "a c";
        String password = "abc";
        String email = "abc@gmail.com";
        CreateAccountViewModel viewModel = new CreateAccountViewModel();

        viewModel.createAccount(email, password, whitespace);
        LiveData<LoginStatus> status1 = viewModel.getCreateAccountStatus();
        if (status1.getValue() != null) {
            assert (status1.getValue().getErrorMessage() == "Confirmed password cannot contain whitespaces!");
        }
    }

    @Test
    public void empty_confirmed_create_account() {
        String empty_string = "";
        String password = "abc";
        String email = "abc@gmail.com";
        CreateAccountViewModel viewModel = new CreateAccountViewModel();

        viewModel.createAccount(email, password, empty_string);
        LiveData<LoginStatus> status2 = viewModel.getCreateAccountStatus();
        if (status2.getValue() != null) {
            assert (status2.getValue().getErrorMessage() == "Confirmed Password is empty");
        }
    }

    @Test
    public void password_match() {
        String password = "abc";
        String confirmed = "abcabc";
        String email = "abc@gmail.com";
        CreateAccountViewModel viewModel = new CreateAccountViewModel();

        viewModel.createAccount(email, password, confirmed);
        LiveData<LoginStatus> status = viewModel.getCreateAccountStatus();
        if (status.getValue() != null) {
            assert (status.getValue().getErrorMessage() == "Password does not match!");
        }
    }


    @Test
    public void incorrect_password() {
        String email = "abc@gmail.com";
        String password = "123456";
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn(email, password);
        LiveData<LoginStatus> status = viewModel.getLoginStatus();
        if (status.getValue() != null) {
            assert (status.getValue().getIsSuccess() == false);
        }
    }

    @Test
    public void test_success_login() {
        String email = "abc@gmail.com";
        String password = "abcabc";
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn(email, password);
        LiveData<LoginStatus> status = viewModel.getLoginStatus();
        if (status.getValue() != null) {
            assert (status.getValue().getIsSuccess() == true);
        }
    }


}