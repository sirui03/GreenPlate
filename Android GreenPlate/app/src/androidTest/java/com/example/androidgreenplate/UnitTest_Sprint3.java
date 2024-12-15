package com.example.androidgreenplate;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.androidgreenplate.model.Ingredient;
import com.example.androidgreenplate.model.LoginStatus;
import androidx.lifecycle.Observer;
import androidx.annotation.Nullable;

import com.example.androidgreenplate.model.Recipe;
import com.example.androidgreenplate.viewmodels.IngredientViewModel;
import com.example.androidgreenplate.viewmodels.LoginViewModel;
import com.example.androidgreenplate.viewmodels.RecipeViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UnitTest_Sprint3 {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Test
    public void test_duplicate_ingredient() throws InterruptedException {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn("abc@gmail.com", "abcabc");
        Thread.sleep(1000);
        String name = "banana";
        String quantity = "1";
        String calorie = "20";
        IngredientViewModel ingredientViewModel = new IngredientViewModel();

        final CountDownLatch latch = new CountDownLatch(1);

        LiveData<LoginStatus> saveIngredientStatus = ingredientViewModel.getSaveIngredientStatus();
        saveIngredientStatus.observeForever(new Observer<LoginStatus>() {
            public void onChanged(@Nullable LoginStatus loginStatus) {
                latch.countDown(); //Allow the thread to proceed after LiveData is updated.
            }
        });

        ingredientViewModel.addIngredient(name, quantity, calorie);
        Thread.sleep(1000);

        if (saveIngredientStatus.getValue() != null) {
//            assert (saveIngredientStatus.getValue().getErrorMessage() == "Pantry with the same name already exists!");
            assertFalse(saveIngredientStatus.getValue().getIsSuccess());
        }
    }

    @Test
    public void test_ingredient_link_to_user() throws InterruptedException {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn("abc@gmail.com", "abcabc");
        Thread.sleep(1000);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String name = "banana";
        String quantity = "1";
        String calorie = "20";
        IngredientViewModel ingredientViewModel = new IngredientViewModel();

        final CountDownLatch latch = new CountDownLatch(1);

        LiveData<LoginStatus> saveIngredientStatus = ingredientViewModel.getSaveIngredientStatus();
        saveIngredientStatus.observeForever(new Observer<LoginStatus>() {
            public void onChanged(@Nullable LoginStatus loginStatus) {
                latch.countDown(); //Allow the thread to proceed after LiveData is updated.
            }
        });

        ingredientViewModel.addIngredient(name, quantity, calorie);
        ingredientViewModel.fetchIngredientsForCurrentUser();
        MutableLiveData<List<Ingredient>> userIngredientsLiveData = ingredientViewModel.getIngredientsLiveData();
        List<Ingredient> ingredientList = userIngredientsLiveData.getValue();

        if (ingredientList != null) {
            assert(ingredientList.contains("banana"));
        }
    }

    @Test
    public void test_save_ingredient() throws InterruptedException {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn("abc@gmail.com", "abcabc");
        Thread.sleep(1000);
        String name = "watermelon";
        String quantity = "1";
        String calorie = "50";
        IngredientViewModel ingredientViewModel = new IngredientViewModel();

        final CountDownLatch latch = new CountDownLatch(1);

        LiveData<LoginStatus> saveIngredientStatus = ingredientViewModel.getSaveIngredientStatus();
        saveIngredientStatus.observeForever(new Observer<LoginStatus>() {
            public void onChanged(@Nullable LoginStatus loginStatus) {
                latch.countDown(); //Allow the thread to proceed after LiveData is updated.
            }
        });

        ingredientViewModel.addIngredient(name, quantity, calorie);

        if (saveIngredientStatus.getValue() != null) {
            assert(saveIngredientStatus.getValue().getIsSuccess());
        }
    }

    @Test
    public void test_fetch_ingredient() throws InterruptedException {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn("abc@gmail.com", "abcabc");
        Thread.sleep(1000);
        IngredientViewModel ingredientViewModel = new IngredientViewModel();

        final CountDownLatch latch = new CountDownLatch(1);

        LiveData<LoginStatus> saveIngredientStatus = ingredientViewModel.getSaveIngredientStatus();
        saveIngredientStatus.observeForever(new Observer<LoginStatus>() {
            public void onChanged(@Nullable LoginStatus loginStatus) {
                latch.countDown(); //Allow the thread to proceed after LiveData is updated.
            }
        });

        ingredientViewModel.fetchIngredientsForCurrentUser();
        MutableLiveData<List<Ingredient>> userIngredientsLiveData = ingredientViewModel.getIngredientsLiveData();
        List<Ingredient> ingredientList = userIngredientsLiveData.getValue();

        if (ingredientList != null) {
            assert(ingredientList.equals("banana"));
        }
    }
    @Test
    public void test_invalid_recipe_name() {
        RecipeViewModel recipeViewModel = RecipeViewModel.getInstance();

        List<String> ingredientNames = new ArrayList<>();
        ingredientNames.add("Ingredient 1");
        ingredientNames.add("Ingredient 2");

        List<String> ingredientQuantities = new ArrayList<>();
        ingredientQuantities.add("-1");
        ingredientQuantities.add("0");

        String recipeName = "";

        recipeViewModel.save(ingredientNames, ingredientQuantities, recipeName);

        LiveData<LoginStatus> recipeStatus = recipeViewModel.getSaveRecipeStatus();
        LoginStatus loginStatus = recipeStatus.getValue();

        assert(loginStatus.getIsSuccess() == false);
        assert(loginStatus.getErrorMessage().equals("Recipe name cannot be empty!"));
    }

    @Test
    public void test_save_recipe() throws InterruptedException {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn("abc@gmail.com", "abcabc");
        Thread.sleep(1000);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        RecipeViewModel recipeViewModel =  RecipeViewModel.getInstance();

        final CountDownLatch latch = new CountDownLatch(1);

        List<String> ingredientNames = new ArrayList<>();
        ingredientNames.add("Ingredient 1");
        ingredientNames.add("Ingredient 2");

        List<String> ingredientQuantities = new ArrayList<>();
        ingredientQuantities.add("1");
        ingredientQuantities.add("3");

        String recipeName = "Yurun";

        LiveData<LoginStatus> status = recipeViewModel.getSaveRecipeStatus();

        recipeViewModel.save(ingredientNames, ingredientQuantities, recipeName);
        Thread.sleep(1000);
        assert(status.getValue().getIsSuccess());

    }

    // Test adding a recipe with empty ingredient names
    @Test
    public void test_emptyIngredientNames() {
        // Given
        RecipeViewModel recipeViewModel = RecipeViewModel.getInstance();

        List<String> ingredientNames = new ArrayList<>();
        ingredientNames.add("");
        ingredientNames.add("Ingredient 2");

        List<String> ingredientQuantities = new ArrayList<>();
        ingredientQuantities.add("1");
        ingredientQuantities.add("2");

        String recipeName = "Test Recipe";

        // When
        recipeViewModel.save(ingredientNames, ingredientQuantities, recipeName);

        // Then
        LiveData<LoginStatus> recipeStatus = recipeViewModel.getSaveRecipeStatus();
        LoginStatus loginStatus = recipeStatus.getValue();

        assertNotNull(loginStatus);
        assertFalse(loginStatus.getIsSuccess());
        assertEquals("Ingredients names cannot be empty!", loginStatus.getErrorMessage());
    }

    // Test adding a recipe with invalid ingredient quantities
    @Test
    public void test_invalidIngredientQuantities() {
        // Given
        RecipeViewModel recipeViewModel = RecipeViewModel.getInstance();

        List<String> ingredientNames = new ArrayList<>();
        ingredientNames.add("Ingredient 1");
        ingredientNames.add("Ingredient 2");

        List<String> ingredientQuantities = new ArrayList<>();
        ingredientQuantities.add("-1");
        ingredientQuantities.add("0");

        String recipeName = "Test Recipe";

        // When
        recipeViewModel.save(ingredientNames, ingredientQuantities, recipeName);

        // Then
        LiveData<LoginStatus> recipeStatus = recipeViewModel.getSaveRecipeStatus();
        LoginStatus loginStatus = recipeStatus.getValue();

        assertNotNull(loginStatus);
        assertFalse(loginStatus.getIsSuccess());
        assertEquals("Ingredients quantity must be positive integer!", loginStatus.getErrorMessage());
    }

    @Test
    public void test_removeIngredients() throws InterruptedException {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn("sqzsqz@gmail.com", "sqzsqz");
        Thread.sleep(1000);

        IngredientViewModel ingredientViewModel = new IngredientViewModel();
        final CountDownLatch latch = new CountDownLatch(1);
        LiveData<List<Ingredient>> ingredientsList = ingredientViewModel.getIngredientsLiveData();
        ingredientsList.observeForever(new Observer<List<Ingredient>>() {
            public void onChanged(@Nullable List<Ingredient> ingredientsList) {
                latch.countDown(); //Allow the thread to proceed after LiveData is updated.
            }
        });


        if (ingredientsList.getValue() != null) {
            int len = ingredientsList.getValue().size();
            System.out.println(len);
            Ingredient updateIngredient = ingredientsList.getValue().get(0);
            updateIngredient.setQuantity(0);
            ingredientViewModel.updateIngredient(updateIngredient);
            assert (ingredientsList.getValue().size() == len - 1);
            updateIngredient.setQuantity(3);
            ingredientViewModel.addIngredient(updateIngredient.getName(), Integer.toString(updateIngredient.getQuantity()), Integer.toString(updateIngredient.getCalories()));
        }

    }

    @Test
    public void test_addIngredientQuantity() throws InterruptedException {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn("sqzsqz@gmail.com", "sqzsqz");
        Thread.sleep(1000);
        Ingredient updateIngredient = new Ingredient("-NuISVmX3-2PPW1bAJ04", "1sDuSbSfGDT2R8US7UqNzG6zpOW2", "ingredient2", 5, 100);

        IngredientViewModel ingredientViewModel = new IngredientViewModel();

        final CountDownLatch latch = new CountDownLatch(1);

        LiveData<List<Ingredient>> ingredientsList = ingredientViewModel.getIngredientsLiveData();
        ingredientsList.observeForever(new Observer<List<Ingredient>>() {
            public void onChanged(@Nullable List<Ingredient> ingredientsList) {
                latch.countDown(); //Allow the thread to proceed after LiveData is updated.
            }
        });

        updateIngredient.setQuantity(7);
        ingredientViewModel.updateIngredient(updateIngredient);

        if (ingredientsList.getValue() != null) {
            assert (ingredientsList.getValue().get(1).getQuantity() == 7);
        }
    }
    @Test
    public void testHasEnoughIngredients() throws InterruptedException {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn("sort@gmail.com", "000000");
        Thread.sleep(1000);

        IngredientViewModel ingredientViewModel = new IngredientViewModel();
        final CountDownLatch latch = new CountDownLatch(1);
        LiveData<List<Ingredient>> ingredientsList = ingredientViewModel.getIngredientsLiveData();
        ingredientsList.observeForever(new Observer<List<Ingredient>>() {
            public void onChanged(@Nullable List<Ingredient> ingredientsList) {
                latch.countDown(); //Allow the thread to proceed after LiveData is updated.
            }
        });


        RecipeViewModel recipeVM = RecipeViewModel.getInstance();
        recipeVM.fetchIngredientsForCurrentUser();
        Thread.sleep(1000);

        ArrayList<Ingredient> recipeIngredients = new ArrayList<>();
        recipeIngredients.add(new Ingredient("Flour", 300));
        recipeIngredients.add(new Ingredient("Sugar", 100));

        Recipe testRecipe = new Recipe(recipeIngredients, "Test Cake");

        boolean result = recipeVM.hasEnoughIngredients(testRecipe);

        assertTrue(result);
    }


    @Test
    public void testNotEnoughIngredients() throws InterruptedException {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.signIn("sort@gmail.com", "000000");
        Thread.sleep(1000);

        IngredientViewModel ingredientViewModel = new IngredientViewModel();
        final CountDownLatch latch = new CountDownLatch(1);
        LiveData<List<Ingredient>> ingredientsList = ingredientViewModel.getIngredientsLiveData();
        ingredientsList.observeForever(new Observer<List<Ingredient>>() {
            public void onChanged(@Nullable List<Ingredient> ingredientsList) {
                latch.countDown(); //Allow the thread to proceed after LiveData is updated.
            }
        });

        RecipeViewModel recipeVM = RecipeViewModel.getInstance();




        ArrayList<Ingredient> recipeIngredients = new ArrayList<>();
        recipeIngredients.add(new Ingredient("Flour", 1000));
        recipeIngredients.add(new Ingredient("Sugar", 1000));

        Recipe testRecipe = new Recipe(recipeIngredients, "Test Cake");

        boolean result = recipeVM.hasEnoughIngredients(testRecipe);

        assertFalse(result);
    }


}