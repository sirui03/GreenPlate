package com.example.androidgreenplate.model;
import java.util.ArrayList;

public class Recipe {
    
    private String recipeName;
    private ArrayList<Ingredient> recipe;
    private int calories;
    private ArrayList<Integer> quantities;

    public Recipe() {
        this.recipe = new ArrayList<>();
    }

    public Recipe(ArrayList<Ingredient> ingredientsList, String name) {
        this.recipe = ingredientsList;
        this.recipeName = name;
    }
    public String getRecipeName() {
        return recipeName;
    }
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }
    public ArrayList<Ingredient> getRecipeIngredients() {
        return recipe;
    }
    public void setRecipeIngredients(ArrayList<Ingredient> recipeIngredients) {
        this.recipe = recipeIngredients;
    }
    public int getCalories() {
        int calories = 0;
        for (Ingredient i : recipe) {
            calories += i.getCalories() * i.getQuantity();
        }
        return calories;
    }
    public void setCalories(int calories) {
        this.calories = calories;
    }


}
