package com.example.androidgreenplate;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.example.androidgreenplate.model.Ingredient;
import androidx.lifecycle.Observer;
import androidx.annotation.Nullable;

import com.example.androidgreenplate.viewmodels.LoginViewModel;
import com.example.androidgreenplate.viewmodels.ShoppingListViewModel;


import java.util.List;
import java.util.concurrent.CountDownLatch;


import org.junit.Rule;
import org.junit.Test;

public class UnitTest_Sprint4_sirui {


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Test
    public void test_addShoppingListItemQuantity() throws InterruptedException {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn("777@gmail.com", "777777");
        Thread.sleep(1000);

        ShoppingListViewModel shoppingListViewModel = new ShoppingListViewModel();
        final CountDownLatch latch = new CountDownLatch(1);
        LiveData<List<Ingredient>> shoppingList = shoppingListViewModel.getShoppingListItems();
        shoppingList.observeForever(new Observer<List<Ingredient>>() {
            public void onChanged(@Nullable List<Ingredient> ingredientsList) {
                latch.countDown(); //Allow the thread to proceed after LiveData is updated.
            }
        });

        if (shoppingList.getValue() != null) {
            int len = shoppingList.getValue().size();
            System.out.println(len);
            Ingredient updateIngredient = shoppingList.getValue().get(0);
            updateIngredient.setQuantity(8);
            shoppingListViewModel.updateIngredient(updateIngredient);
        }

    }
    @Test
    public void test_removeShoppingListItem() throws InterruptedException {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn("777@gmail.com", "777777");
        Thread.sleep(1000);

        ShoppingListViewModel shoppingListViewModel = new ShoppingListViewModel();
        final CountDownLatch latch = new CountDownLatch(1);
        LiveData<List<Ingredient>> shoppingList = shoppingListViewModel.getShoppingListItems();
        shoppingList.observeForever(new Observer<List<Ingredient>>() {
            public void onChanged(@Nullable List<Ingredient> ingredientsList) {
                latch.countDown(); //Allow the thread to proceed after LiveData is updated.
            }
        });

        if (shoppingList.getValue() != null) {
            int len = shoppingList.getValue().size();
            System.out.println(len);
            Ingredient updateIngredient = shoppingList.getValue().get(0);
            updateIngredient.setQuantity(0);
            shoppingListViewModel.updateIngredient(updateIngredient);
            assert (shoppingList.getValue().size() == len - 1);
        }

    }
}
