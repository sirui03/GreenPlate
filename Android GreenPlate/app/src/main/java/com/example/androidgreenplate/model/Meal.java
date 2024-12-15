package com.example.androidgreenplate.model;

import java.util.ArrayList;

public class Meal {
        
    private String mealName;
    private int numberDishes;
    private ArrayList<String> dishes;
    private int calories;

    private String userId;

    public Meal() {
        this.mealName = "";
        this.numberDishes = 0;
        this.dishes = null;
        this.calories = 0;
        this.userId = "";
    }
            
        
    public Meal(String mealName, ArrayList<String> dishes, int calories, String userId) {
        this.mealName = mealName;
        this.numberDishes = dishes.size();
        this.dishes = dishes;
        this.calories = calories;
        this.userId = userId;
    }

    public Meal(String mealName, String userId) {
        this.mealName = mealName;
        this.numberDishes = 0;
        this.dishes = new ArrayList<String>();
        this.calories = 0;
        this.userId = userId;

    }

    public Meal(String mealName, int calories, String userId) {
        this.mealName = mealName;
        this.numberDishes = 0;
        this.dishes = new ArrayList<String>();
        this.calories = calories;
        this.userId = userId;

    }
            
    public String getMealName() {
        return mealName;
    }
    public void setMealName(String mealName) {
        this.mealName = mealName;
    }
    public int getNumberDishes() {
        return numberDishes;
    }
    public void setNumberDishes(int numberDishes) {
        this.numberDishes = numberDishes;
    }
    public ArrayList<String> getDishes() {
        return dishes;
    }
    public void setDishes(ArrayList<String> dishes) {
        this.dishes = dishes;
        this.numberDishes = dishes.size();
    }
    public String getDish(int i) {
        if (i < 0 || i >= numberDishes) {
            return null;
        }
        return dishes.get(i);
    }
    public String setDish(int i, String dish) {
        if (i < 0 || i >= numberDishes) {
            return null;
        }
        return dishes.set(i, dish);
    }

    public boolean addDish(int i, String dish) {
        if (i < 0 || i > numberDishes) {
            return false;
        }
        dishes.add(i, dish);
        numberDishes++;
        return true;
    }
    public boolean addDish(String dish) {
        dishes.add(dish);
        numberDishes++;
        return true;
    }

    public int getCalories() {
        return calories;
    }
    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
