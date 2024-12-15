package com.example.androidgreenplate;

import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.androidgreenplate.model.Ingredient;
import com.example.androidgreenplate.model.LoginStatus;
import com.example.androidgreenplate.viewmodels.LoginViewModel;
import com.example.androidgreenplate.viewmodels.ShoppingListViewModel;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UnitTest_Sprint4_yixiao {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Test
    public void test_save_item() throws InterruptedException {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn("abc@gmail.com", "abcabc");
        Thread.sleep(1000);
        String name = "banana";
        int quantity = 1;
        Ingredient newIngredient = new Ingredient();
        newIngredient.setName(name);
        newIngredient.setQuantity(quantity);

        ShoppingListViewModel shoppingListViewModel = new ShoppingListViewModel();

        final CountDownLatch latch = new CountDownLatch(1);

        LiveData<LoginStatus> saveItemStatus = shoppingListViewModel.getSaveIngredientStatus();
        saveItemStatus.observeForever(new Observer<LoginStatus>() {
            public void onChanged(@Nullable LoginStatus loginStatus) {
                latch.countDown(); //Allow the thread to proceed after LiveData is updated.
            }
        });

    }

}